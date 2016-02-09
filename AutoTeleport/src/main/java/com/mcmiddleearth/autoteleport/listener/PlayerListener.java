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
package com.mcmiddleearth.autoteleport.listener;

import com.mcmiddleearth.autoteleport.AutoTeleportPlugin;
import com.mcmiddleearth.autoteleport.data.PluginData;
import com.mcmiddleearth.autoteleport.data.TeleportationArea;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Eriol_Eandur
 */
public class PlayerListener implements Listener{
    
    @EventHandler
    public void playerMove(PlayerMoveEvent event) {
        if(PluginData.isStopped() || PluginData.isExcluded(event.getPlayer())) {
            return;
        }
        final Player player = event.getPlayer();
        Location playerLocation = player.getLocation();
        for(TeleportationArea area : PluginData.getTeleportAreas().values()) {
            if(area.getTarget()!=null && area.isInside(playerLocation)) {
Logger.getGlobal().info("teleport");
                Location target = calculateTarget(area, player);
                final Vector vel = player.getVelocity();
                loadTargetChunks(target);
                final boolean dynamic = area.isDynamic();
                final TeleportationArea targetArea = area;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(isChunkListLoaded()) {
Logger.getGlobal().info("loaded");
                            Location loc = calculateTarget(targetArea, player);
                            player.teleport(loc);  
                            if(dynamic) {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        player.setVelocity(vel);
                                    }
                                }.runTaskLater(AutoTeleportPlugin.getPluginInstance(),2);
                            }
                            this.cancel();
                        }
                    }
                }.runTaskTimer(AutoTeleportPlugin.getPluginInstance(),1,1);
                //return;
            }
        }
    }

    private Location calculateTarget(TeleportationArea area, Player player) {
        Location target = area.getTarget().clone();
        Location playerLocation = player.getLocation();
        target.setX(target.getBlockX()-area.getCenter().getBlockX()+playerLocation.getX());
        target.setY(target.getBlockY()-area.getCenter().getBlockY()+playerLocation.getY());
        target.setZ(target.getBlockZ()-area.getCenter().getBlockZ()+playerLocation.getZ());
        if(area.isKeepOrientation()) {
            target.setPitch(playerLocation.getPitch());
            target.setYaw(playerLocation.getYaw());
        }
        return target;
    }
     
    private World targetWorld;
     
    List<Chunk> chunkList = new ArrayList<>();

    List<ChunkLoadingTask> workList = new ArrayList<>();
     
    private int viewDist;
     
     private class ChunkLoadingTask {
         
         int centerX, centerZ, shiftX, shiftZ;
         
         ChunkLoadingTask(int centerX, int centerZ, int shiftX, int shiftZ) {
             this.centerX = centerX;
             this.centerZ = centerZ;
             this.shiftX = shiftX;
             this.shiftZ = shiftZ;
         }
         
         void execute() {
             Chunk chunk = targetWorld.getChunkAt(centerX+shiftX, centerZ+shiftZ);
             chunk.load();
//Logger.getGlobal().info("");
//Logger.getGlobal().info("load " + (shiftX) +" "+(shiftZ)+ "          step "+(Math.abs(shiftX)+Math.abs(shiftZ)));
             chunkList.add(chunk);
             if(shiftX>=0 && shiftX<viewDist && (shiftZ==0 || shiftX<shiftZ || shiftX<-shiftZ)) {
                 workList.add(new ChunkLoadingTask(centerX,centerZ,shiftX+16,shiftZ));
//Logger.getGlobal().info("add posx "+(shiftX+16)+" "+shiftZ);
             }
             if(shiftX<=0 && -shiftX<viewDist && (shiftZ==0 || -shiftX<-shiftZ || -shiftX<shiftZ)) {
                 workList.add(new ChunkLoadingTask(centerX,centerZ,shiftX-16,shiftZ));
//Logger.getGlobal().info("add negx "+(shiftX-16)+" "+shiftZ);
             }
             if(shiftZ>=0 && shiftZ<viewDist && (shiftX==0 || shiftZ+16<shiftX || (shiftZ+16)<-shiftX)) {
                 workList.add(new ChunkLoadingTask(centerX,centerZ,shiftX,shiftZ+16));
//Logger.getGlobal().info("add posz "+shiftX+" "+(shiftZ+16));
             }
             if(shiftZ<=0 && -shiftZ<viewDist && (shiftX==0 || -(shiftZ-16)<-shiftX  || -(shiftZ-16)<shiftX)) {
                 workList.add(new ChunkLoadingTask(centerX,centerZ,shiftX,shiftZ-16));
//Logger.getGlobal().info("add negz "+(shiftX)+" "+(shiftZ-16));
             }
             workList.remove(this);
         }
     }
     
     
     private void loadTargetChunks(Location target) {
         targetWorld = target.getWorld();
         Chunk centerChunk = target.getChunk();
         int chunkX = centerChunk.getX();
         int chunkZ = centerChunk.getZ();
         viewDist = Math.min(Bukkit.getViewDistance(),5)*16;
         chunkList.clear();
         centerChunk.load();
         chunkList.add(centerChunk);
         workList.clear();
         workList.add(new ChunkLoadingTask(chunkX, chunkZ, 16,0));
         workList.add(new ChunkLoadingTask(chunkX, chunkZ, 0,16));
         workList.add(new ChunkLoadingTask(chunkX, chunkZ, -16,0));
         workList.add(new ChunkLoadingTask(chunkX, chunkZ, 0,-16));
         while(!workList.isEmpty()) {
             workList.get(0).execute();
         }
Logger.getGlobal().info("number of chunks "+chunkList.size());
     }
     
     private boolean isChunkListLoaded() {
         for(Chunk chunk:chunkList) {
             if(!chunk.isLoaded()) {
                 return false;
             }
         }
         return true;
     }
}
