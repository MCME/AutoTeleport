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
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
         Player player = event.getPlayer();
         Location playerLocation = player.getLocation();
         for(TeleportationArea area : PluginData.getTeleportAreas().values()) {
             if(area.getTarget()!=null && area.isInside(playerLocation)) {
                Location target = area.getTarget().clone();
                target.setX(target.getBlockX()-area.getCenter().getBlockX()+playerLocation.getX());
                target.setY(target.getBlockY()-area.getCenter().getBlockY()+playerLocation.getY());
                target.setZ(target.getBlockZ()-area.getCenter().getBlockZ()+playerLocation.getZ());
                if(area.isKeepOrientation()) {
                    target.setPitch(playerLocation.getPitch());
                    target.setYaw(playerLocation.getYaw());
                }
                final Player play = player;
                final Location loc = target;
                final Vector vel = player.getVelocity();
                final boolean dynamic = area.isDynamic();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        play.teleport(loc);  
                        if(dynamic) {
                            play.setVelocity(vel);
                        }
                    }
                }.runTaskLater(AutoTeleportPlugin.getPluginInstance(), 1);
             }
         }
     }
}
