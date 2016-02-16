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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public abstract class TeleportationArea {
    
    @Getter
    @Setter
    private Location center;
    
    @Getter
    private Location target;
    
    @Getter
    @Setter
    private boolean dynamic;
    
    @Getter
    @Setter
    private boolean keepOrientation;
    
    @Getter
    @Setter
    private int firstDelay = 1,
                teleportDelay = 0,
                velocityDelay = 2,
                velocityReps = 1,
                preloadDistance = 20;
    
    @Getter
    private int viewDistance = 80; // #Blocks  (160 blocks == 10 chunks)
        
    @Getter
    @Setter
    private boolean recalculateTarget = false,
            refreshChunks = false;
            
    private final Set<Chunk> targetChunks = new HashSet<>();
    
    private int targetChunkMinX, targetChunkMaxX, targetChunkMinZ, targetChunkMaxZ;
    
    private final Set<UUID> nearPlayers = new HashSet<>();
    
    
    @Getter
    private boolean armed = false;
    
    public TeleportationArea(Location center) {
        this.center = center;
    }
    
    public TeleportationArea(ConfigurationSection config) {
        this.center = deserializeLocation(config.getConfigurationSection("center"));
        this.target = deserializeLocation(config.getConfigurationSection("target"));
        this.dynamic = config.getBoolean("dynamic");
        this.keepOrientation = config.getBoolean("keepOrientation");
        getTargetChunks();
    }
    
    public abstract boolean isNear(Location loc);
    
    public abstract boolean isInside(Location loc);
    
    public void setViewDistance(int distanceInBlocks) {
//Logger.getGlobal().info("setViewDistance");
        this.viewDistance=distanceInBlocks;
        getTargetChunks();
    }
    
    public void addNearPlayer(Player player) {
        nearPlayers.add(player.getUniqueId());
        if(!armed) {
            loadTargetChunks();
            armed = true;
        }
    }
    
    public void remove(Player player) {
        nearPlayers.remove(player.getUniqueId());
        if(nearPlayers.isEmpty()) {
            armed = false;
        }
    }
    
    public boolean isNeeded(Chunk chunk) {
        return armed && chunk.getX()>=targetChunkMinX
                     && chunk.getX()<=targetChunkMaxX
                     && chunk.getZ()>=targetChunkMinZ
                     && chunk.getZ()<=targetChunkMaxZ;
    }
    
/*    public boolean arePlayersNear() {
        return !nearPlayers.isEmpty();
    }
*/    
    public void setTarget(Location target) {
        this.target = target;
        getTargetChunks();
    }
    public String getType() {
        if(dynamic) {
            return "Dynamic";
        }
        else {
            return "Static";
        }
    }
    
    public Map<String, Object> serialize() {
        Map<String,Object> result = new HashMap();
        result.put("center", serializeLocation(this.center));
        if(target!=null) {
            result.put("target", serializeLocation(this.target));
        }
        else {
            result.put("target", null);
        }
        result.put("dynamic", dynamic);
        result.put("keepOrientation", keepOrientation);
        return result;
    }
    
    private static Map<String,Object> serializeLocation(Location loc) {
        Map<String,Object> result = new HashMap<>();
        result.put("x", loc.getX());
        result.put("y", loc.getY());
        result.put("z", loc.getZ());
        result.put("yaw", loc.getYaw());
        result.put("pitch", loc.getPitch());
        result.put("world", loc.getWorld().getName());
        return result;
    }
    
    private static Location deserializeLocation(ConfigurationSection data) {
        if(data == null) {
            return null;
        }
        World world = Bukkit.getWorld(data.getString("world"));
        if(world == null) {
            return null;
        }
        else {
            return new Location(world, (Double) data.get("x"), 
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
            int viewDist = getViewDistance();
            Chunk chunk = getTarget().getWorld().getChunkAt(target.getChunk().getX()+shiftX, 
                                                                 target.getChunk().getZ()+shiftZ);
            //chunk.load();
//Logger.getGlobal().info("");
//Logger.getGlobal().info("load " + (shiftX) +" "+(shiftZ)+ "          step "+(Math.abs(shiftX)+Math.abs(shiftZ)));
            targetChunks.add(chunk);
            if(shiftX>=0 && shiftX<viewDist && (shiftZ==0 || shiftX<shiftZ || shiftX<-shiftZ)) {
                workList.add(new ChunkLoadingTask(shiftX+16,shiftZ));
//Logger.getGlobal().info("add posx "+(shiftX+16)+" "+shiftZ);
            }
            if(shiftX<=0 && -shiftX<viewDist && (shiftZ==0 || -shiftX<-shiftZ || -shiftX<shiftZ)) {
                workList.add(new ChunkLoadingTask(shiftX-16,shiftZ));
//Logger.getGlobal().info("add negx "+(shiftX-16)+" "+shiftZ);
            }
            if(shiftZ>=0 && shiftZ<viewDist && (shiftX==0 || shiftZ+16<shiftX || (shiftZ+16)<-shiftX)) {
                workList.add(new ChunkLoadingTask(shiftX,shiftZ+16));
//Logger.getGlobal().info("add posz "+shiftX+" "+(shiftZ+16));
            }
            if(shiftZ<=0 && -shiftZ<viewDist && (shiftX==0 || -(shiftZ-16)<-shiftX  || -(shiftZ-16)<shiftX)) {
                workList.add(new ChunkLoadingTask(shiftX,shiftZ-16));
//Logger.getGlobal().info("add negz "+(shiftX)+" "+(shiftZ-16));
            }
            workList.remove(this);
        }
    }
     
    private final List<ChunkLoadingTask> workList = new ArrayList<>();
    
    private void getTargetChunks() {
        if(target!=null) {
            targetChunks.clear();
            targetChunkMinX = target.getBlockX()-getViewDistance();
            targetChunkMaxX = target.getBlockX()+getViewDistance();
            targetChunkMinZ = target.getBlockZ()-getViewDistance();
            targetChunkMaxZ = target.getBlockZ()+getViewDistance();
            for(int i= targetChunkMinX;i<targetChunkMaxX;i+=16) {
                for(int j= targetChunkMinZ;j<targetChunkMaxZ;j+=16) {
                    targetChunks.add(getTarget().getWorld().getChunkAt(i,j));
                }
            }
//player.sendMessage("-----> preloading "+chunkList.size()+" chunks.");
Logger.getGlobal().info("number of chunks "+targetChunks.size());
        }
    }
    
    private void _invalid_getTargetChunks() {
        if(target!=null) {
            Chunk centerChunk = target.getChunk();
            targetChunks.clear();
            if(getViewDistance()>0) {
//Logger.getGlobal().info("preload");
                //centerChunk.load();
                targetChunks.add(centerChunk);
                workList.clear();
                workList.add(new ChunkLoadingTask(16,0));
                workList.add(new ChunkLoadingTask(0,16));
                workList.add(new ChunkLoadingTask(-16,0));
                workList.add(new ChunkLoadingTask(0,-16));
                while(!workList.isEmpty()) {
                    workList.get(0).execute();
                }
            }
//player.sendMessage("-----> preloading "+chunkList.size()+" chunks.");
Logger.getGlobal().info("number of chunks "+targetChunks.size());
        }
        else {
Logger.getGlobal().info("No chunks got as no target set.");
        }
    }
    
    public void loadTargetChunks() {
        for(Chunk chunk: targetChunks) {
            chunk.load();
        }
//Logger.getGlobal().info("loading "+targetChunks.size()+" chunks.");
    }
    
    public boolean isChunkListLoaded() {
        for(Chunk chunk:targetChunks) {
            if(!chunk.isLoaded()) {
                return false;
            }
        }
        return true;
     }

    public void refreshChunks() {
        for(Chunk chunk: targetChunks) {
            target.getWorld().refreshChunk(chunk.getX(), chunk.getZ());
        }
    }

}
