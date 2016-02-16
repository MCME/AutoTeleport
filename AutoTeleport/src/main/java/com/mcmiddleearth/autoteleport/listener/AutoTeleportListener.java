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

import com.mcmiddleearth.autoteleport.data.PluginData;
import com.mcmiddleearth.autoteleport.data.TeleportationArea;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

/**
 *
 * @author Eriol_Eandur
 */
public class AutoTeleportListener implements Listener{
    
    @EventHandler
    public void playerMove(PlayerMoveEvent event) {
        if(PluginData.isStopped() 
                || PluginData.isExcluded(event.getPlayer()) 
                || PluginData.isInTeleportation(event.getPlayer())) {
            return;
        }
        final Player player = event.getPlayer();
        Location playerLocation = player.getLocation();
        for(TeleportationArea area : PluginData.getTeleportAreas().values()) {
            if(area.getTarget()!=null) {
//player.sendMessage("-----> isnearArea??");
               if(area.isNear(playerLocation)) { 
player.sendMessage("-----> nearArea "+area.isArmed());
                    area.addNearPlayer(player);
                } else {
                    area.remove(player);
                }
            }
            if(area.getTarget()!=null && area.isInside(playerLocation)) {
player.sendMessage("-----> inArea");
                TeleportationHandler handler = new TeleportationHandler(player, area);
                PluginData.registerTeleportation(player, handler);
                handler.startTeleportation();
                return;
            }
        }
    }
    
    @EventHandler
    public void playerLeave(PlayerQuitEvent event) {
        for(TeleportationArea area : PluginData.getTeleportAreas().values()) {
            area.remove(event.getPlayer());
// Logger.getGlobal().info("player quit");
        }
    }
    
    @EventHandler
    public void chunkUnload(ChunkUnloadEvent event) {
        for(TeleportationArea area : PluginData.getTeleportAreas().values()) {
            if(area.isNeeded(event.getChunk())) {
                event.setCancelled(true);
//Logger.getGlobal().info("cancelling unload");
                return;
            }
        }
    }
    
    @EventHandler
    public void chunkLoad(ChunkLoadEvent event) {
//        Logger.getGlobal().info("Load chunk: "+event.getChunk().getX()+" "+event.getChunk().getZ());
    }
    
    
}

