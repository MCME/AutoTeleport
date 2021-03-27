/*
 * Copyright (C) 2016 MCME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mcmiddleearth.autoteleport.data;

import com.mcmiddleearth.autoteleport.util.DevUtil;
import com.mcmiddleearth.pluginutil.region.Region;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * @author Eriol_Eandur
 */
public abstract class TeleportationArea {

    protected Region region;
    private String server;
    private String crossServerWorld;

    private Location target;

    private boolean dynamic;
    private boolean keepOrientation;

    private int firstDelay = 1,
            teleportDelay = 0,
            velocityDelay = 2,
            velocityReps = 1,
            preloadDistance = 20,
            bulkSize = 8;

    private int viewDistance = 80; // #Blocks  (160 blocks == 10 chunks)

    private boolean recalculateTarget = false,
            refreshChunks = false;

    private final List<Chunk> targetChunks = new ArrayList<>();
    private int targetChunkMinX, targetChunkMaxX, targetChunkMinZ, targetChunkMaxZ;
    private final Set<UUID> nearPlayers = new HashSet<>();

    private boolean armed = false;

    protected TeleportationArea() {
        server = "";
    }

    public TeleportationArea(ConfigurationSection config) {
        this.server = config.getString("target.server", "");
        if (!server.equals("")) {
            crossServerWorld = config.getString("target.world");
        }
        this.target = deserializeLocation(config.getConfigurationSection("target"));
        this.dynamic = config.getBoolean("dynamic");
        this.keepOrientation = config.getBoolean("keepOrientation");
        this.preloadDistance = config.getInt("preloadDistance", preloadDistance);
        this.viewDistance = config.getInt("viewDistance", viewDistance);
        this.firstDelay = config.getInt("firstDelay", firstDelay);
        this.teleportDelay = config.getInt("teleportDelay", teleportDelay);
        this.velocityDelay = config.getInt("velocityDelay", velocityDelay);
        this.velocityReps = config.getInt("velocityReps", velocityReps);
        this.refreshChunks = config.getBoolean("refreshChunks", refreshChunks);
        this.recalculateTarget = config.getBoolean("recalculateTarget", recalculateTarget);
    }

    public boolean isNear(Location loc) {
        return region.isNear(loc, getPreloadDistance());
    }

    public boolean isInside(Location loc) {
        return region.isInside(loc);
    }

    public void setViewDistance(int distanceInBlocks) {
        this.viewDistance = distanceInBlocks;
        targetChunks.clear();
    }

    public void addNearPlayer(Player player) {
        nearPlayers.add(player.getUniqueId());
        if (!armed) {
            getTargetChunks();
            loadTargetChunks();
            armed = true;
        }
    }

    public void remove(Player player) {
        nearPlayers.remove(player.getUniqueId());
        if (nearPlayers.isEmpty()) {
            armed = false;
        }
    }

    public boolean isNeeded(Chunk chunk) {
        return armed && chunk.getX() >= targetChunkMinX
                && chunk.getX() <= targetChunkMaxX
                && chunk.getZ() >= targetChunkMinZ
                && chunk.getZ() <= targetChunkMaxZ;
    }

    public void setTarget(Location target) {
        this.target = target;
        targetChunks.clear();
    }

    public String getType() {
        if (dynamic) {
            return "Dynamic";
        } else {
            return "Static";
        }
    }

    public Location getLocation() {
        return region.getLocation();
    }

    public void save(ConfigurationSection config) {
        region.save(config);
        if (target != null) {
            Map<String,Object> targetMap = serializeLocation(this.target);
            if (!server.equals("")) {
                targetMap.put("server", server);
                targetMap.put("world", crossServerWorld);
            }
            config.set("target", targetMap);
        } else {
            config.set("target", null);
        }
        config.set("dynamic", dynamic);
        config.set("keepOrientation", keepOrientation);
        config.set("preloadDistance", preloadDistance);
        config.set("viewDistance", viewDistance);
        config.set("firstDelay", firstDelay);
        config.set("teleportDelay", teleportDelay);
        config.set("velocityDelay", velocityDelay);
        config.set("velocityReps", velocityReps);
        config.set("refreshChunks", refreshChunks);
        config.set("recalculateTarget", recalculateTarget);
    }

    private static Map<String, Object> serializeLocation(Location loc) {
        Map<String, Object> result = new HashMap<>();
        result.put("x", loc.getX());
        result.put("y", loc.getY());
        result.put("z", loc.getZ());
        result.put("yaw", loc.getYaw());
        result.put("pitch", loc.getPitch());
        result.put("world", loc.getWorld().getName());
        return result;
    }

    protected static Location deserializeLocation(ConfigurationSection data) {
        if (data == null) {
            return null;
        }
        World world = Bukkit.getWorld(data.getString("world"));
        if (world == null && !data.contains("server")) {
            return null;
        } else {
            if (world == null) {
                world = Bukkit.getWorlds().get(0);
            }
            return new Location(world, (Double) data.get("x"), //Bukkit.getWorlds().get(0)
                    (Double) data.get("y"),
                    (Double) data.get("z"),
                    ((Double) data.get("yaw")).floatValue(),
                    ((Double) data.get("pitch")).floatValue());
        }
    }

    private class ChunkLoadingTask {

        int shiftX, shiftZ;

        ChunkLoadingTask(int shiftX, int shiftZ) {
            this.shiftX = shiftX;
            this.shiftZ = shiftZ;
        }

        void execute() {
            int viewDist = getViewDistance() / 16; // block number -> chunk number
            Chunk chunk = getTarget().getWorld().getChunkAt(target.getChunk().getX() + shiftX,
                    target.getChunk().getZ() + shiftZ);
            targetChunks.add(chunk);
            if (shiftX >= 0 && shiftX < viewDist && (shiftZ == 0 || shiftX < shiftZ || shiftX < -shiftZ)) {
                workList.add(new ChunkLoadingTask(shiftX + 1, shiftZ));
            }
            if (shiftX <= 0 && -shiftX < viewDist && (shiftZ == 0 || -shiftX < -shiftZ || -shiftX < shiftZ)) {
                workList.add(new ChunkLoadingTask(shiftX - 1, shiftZ));
            }
            if (shiftZ >= 0 && shiftZ < viewDist && (shiftX == 0 || shiftZ + 1 < shiftX || (shiftZ + 1) < -shiftX)) {
                workList.add(new ChunkLoadingTask(shiftX, shiftZ + 1));
            }
            if (shiftZ <= 0 && -shiftZ < viewDist && (shiftX == 0 || -(shiftZ - 1) < -shiftX || -(shiftZ - 1) < shiftX)) {
                workList.add(new ChunkLoadingTask(shiftX, shiftZ - 1));
            }
            workList.remove(this);
        }
    }

    private final List<ChunkLoadingTask> workList = new ArrayList<>();

    private void _simple_getTargetChunks() {
        if (!targetChunks.isEmpty()) {
            return;
        }
        if (target != null) {
            targetChunkMinX = (target.getBlockX() - getViewDistance()) / 16;
            targetChunkMaxX = (target.getBlockX() + getViewDistance()) / 16;
            targetChunkMinZ = (target.getBlockZ() - getViewDistance()) / 16;
            targetChunkMaxZ = (target.getBlockZ() + getViewDistance()) / 16;
            for (int i = targetChunkMinX; i < targetChunkMaxX; i++) {
                for (int j = targetChunkMinZ; j < targetChunkMaxZ; j++) {
                    targetChunks.add(getTarget().getWorld().getChunkAt(i, j));
                }
            }
        }
    }

    private void getTargetChunks() {
        /*if (!targetChunks.isEmpty()) { //removed preloading
            return;
        }
        if (target != null) {
            Chunk centerChunk = target.getChunk();
            if (getViewDistance() > 0) {
                DevUtil.log("preload");
                targetChunks.add(centerChunk);
                workList.clear();
                workList.add(new ChunkLoadingTask(1, 0));
                workList.add(new ChunkLoadingTask(0, 1));
                workList.add(new ChunkLoadingTask(-1, 0));
                workList.add(new ChunkLoadingTask(0, -1));
                while (!workList.isEmpty()) {
                    workList.get(0).execute();
                }
            }
            DevUtil.log("-----> getting " + targetChunks.size() + " chunks.");
        } else {
            DevUtil.log("No chunks got as no target set.");
        }*/
    }

    public void loadTargetChunks() {
        /*for (Chunk chunk : targetChunks) { //removed preloading
            if (!chunk.isLoaded()) {
                chunk.load();
            }
        }
        DevUtil.log("loading " + targetChunks.size() + " chunks.");*/
    }

    public boolean isChunkListLoaded() {
        /*for (Chunk chunk : targetChunks) { removed preloading
            if (!chunk.isLoaded()) {
                return false;
            }
        }*/
        return true;
    }

    public String getServer() {
        return server;
    }

    public String getCrossServerWorld() {
        return crossServerWorld;
    }

    public Location getTarget() {
        return target;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public boolean isKeepOrientation() {
        return keepOrientation;
    }

    public void setKeepOrientation(boolean keepOrientation) {
        this.keepOrientation = keepOrientation;
    }

    public int getFirstDelay() {
        return firstDelay;
    }

    public void setFirstDelay(int firstDelay) {
        this.firstDelay = firstDelay;
    }

    public int getTeleportDelay() {
        return teleportDelay;
    }

    public void setTeleportDelay(int teleportDelay) {
        this.teleportDelay = teleportDelay;
    }

    public int getVelocityDelay() {
        return velocityDelay;
    }

    public void setVelocityDelay(int velocityDelay) {
        this.velocityDelay = velocityDelay;
    }

    public int getVelocityReps() {
        return velocityReps;
    }

    public void setVelocityReps(int velocityReps) {
        this.velocityReps = velocityReps;
    }

    public int getPreloadDistance() {
        return preloadDistance;
    }

    public void setPreloadDistance(int preloadDistance) {
        this.preloadDistance = preloadDistance;
    }

    public int getBulkSize() {
        return bulkSize;
    }

    public void setBulkSize(int bulkSize) {
        this.bulkSize = bulkSize;
    }

    public int getViewDistance() {
        return viewDistance;
    }

    public boolean isRecalculateTarget() {
        return recalculateTarget;
    }

    public void setRecalculateTarget(boolean recalculateTarget) {
        this.recalculateTarget = recalculateTarget;
    }

    public boolean isArmed() {
        return armed;
    }
}
