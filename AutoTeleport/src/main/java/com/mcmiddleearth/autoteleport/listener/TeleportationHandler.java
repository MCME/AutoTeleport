/*
 * Copyright (C) 2016 Lars
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
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Eriol_Eandur
 */
public class TeleportationHandler {

    private final TeleportationArea area;
    
    final Player player;
    
    Location target;
    
    List<Chunk> chunkList = new ArrayList<>();

    List<ChunkLoadingTask> workList = new ArrayList<>();
     
    public TeleportationHandler(Player player, TeleportationArea area) {
        this.player = player;
        this.area = area;
    }
    
    public void startTeleportation() {
        target = calculateTarget();
        final Vector vel = player.getVelocity();
        loadTargetChunks();
        new BukkitRunnable() {
            
            int waitTics = 0;
            @Override
            public void run() {
                if(isChunkListLoaded()) {
                    if(waitTics==0 && area.isRefreshChunks()) {
player.sendMessage("-----> sending chunk refresh packages");
//Logger.getGlobal().info("refresh");
                            for(Chunk chunk: chunkList) {
                                target.getWorld().refreshChunk(chunk.getX(), chunk.getZ());
                            }
                    }
                    if(waitTics<area.getTeleportDelay()) {
player.sendMessage("-----> waiting....");
//Logger.getGlobal().info("wait");
                        waitTics++;
                        return;
                    }
                    Location loc = target;
                    if(area.isRecalculateTarget()) {
                        loc = calculateTarget();
                    }
                    player.teleport(loc);  
player.sendMessage("-----> teleport!");
//Logger.getGlobal().info("teleport");
                    if(area.isDynamic()) {
                        new BukkitRunnable() {
                            int reps=0;
                            @Override
                            public void run() {
                                if(reps<area.getVelocityReps()) {
player.sendMessage("-----> set velocity");
//Logger.getGlobal().info("set velocity");
                                    player.setVelocity(vel);
                                    reps++;
                                }
                                else
                                {
                                    this.cancel();
                                }
                            }
                        }.runTaskTimer(AutoTeleportPlugin.getPluginInstance(),area.getVelocityDelay(),1);
                    }
                    PluginData.ungegisterTeleportation(player);
                    this.cancel();
                }
                else {
player.sendMessage("-----> waiting for chunk preloading ");
//Logger.getGlobal().info("not loaded");
                }
            }
        }.runTaskTimer(AutoTeleportPlugin.getPluginInstance(),area.getFirstDelay(),1);
    }

    private Location calculateTarget() {
        Location newTarget = area.getTarget().clone();
        Location playerLocation = player.getLocation();
        newTarget.setX(newTarget.getBlockX()-area.getCenter().getBlockX()+playerLocation.getX());
        newTarget.setY(newTarget.getBlockY()-area.getCenter().getBlockY()+playerLocation.getY());
        newTarget.setZ(newTarget.getBlockZ()-area.getCenter().getBlockZ()+playerLocation.getZ());
        if(area.isKeepOrientation()) {
            newTarget.setPitch(playerLocation.getPitch());
            newTarget.setYaw(playerLocation.getYaw());
        }
        return newTarget;
    }

    private class ChunkLoadingTask {
         
        int shiftX, shiftZ;
         
        ChunkLoadingTask(int shiftX, int shiftZ) {
            this.shiftX = shiftX;
            this.shiftZ = shiftZ;
        }
         
        void execute() {
            int viewDist = area.getViewDistance();
            Chunk chunk = area.getTarget().getWorld().getChunkAt(target.getChunk().getX()+shiftX, 
                                                                 target.getChunk().getZ()+shiftZ);
            chunk.load();
//Logger.getGlobal().info("");
//Logger.getGlobal().info("load " + (shiftX) +" "+(shiftZ)+ "          step "+(Math.abs(shiftX)+Math.abs(shiftZ)));
            chunkList.add(chunk);
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
     
    private void loadTargetChunks() {
        Chunk centerChunk = target.getChunk();
        chunkList.clear();
        if(area.getViewDistance()>0) {
//Logger.getGlobal().info("preload");
            centerChunk.load();
            chunkList.add(centerChunk);
            workList.clear();
            workList.add(new ChunkLoadingTask(16,0));
            workList.add(new ChunkLoadingTask(0,16));
            workList.add(new ChunkLoadingTask(-16,0));
            workList.add(new ChunkLoadingTask(0,-16));
            while(!workList.isEmpty()) {
                workList.get(0).execute();
            }
        }
player.sendMessage("-----> preloading "+chunkList.size()+" chunks.");
//Logger.getGlobal().info("number of chunks "+chunkList.size());
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
