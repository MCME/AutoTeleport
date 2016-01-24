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
         if(PluginData.isStopped()) {
             return;
         }
         Player player = event.getPlayer();
         Location playerLocation = player.getLocation();
         for(TeleportationArea area : PluginData.getTeleportAreas().values()) {
             if(area.getTarget()!=null && area.isInside(playerLocation)) {
                 if(area.getTarget().getWorld().equals(playerLocation.getWorld())
                        && area.isDynamic()) {
                    Vector shift = new Vector(area.getTarget().getBlockX()-area.getCenter().getBlockX(),
                                              area.getTarget().getBlockY()-area.getCenter().getBlockY(),
                                              area.getTarget().getBlockZ()-area.getCenter().getBlockZ());
                    String commandString = "tp "+player.getName()+" ~"
                                  +shift.getBlockX()+ " ~"
                                  +shift.getBlockY()+ " ~"
                                  +shift.getBlockZ();
                    if(!area.isKeepOrientation()) {
                        commandString = commandString +" "+ area.getTarget().getYaw()+ " "
                                                                   + area.getTarget().getPitch();
                    }
                    final String cmdString = commandString;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmdString);
                        }
                    }.runTaskLater(AutoTeleportPlugin.getPluginInstance(), 1);
                 }
                 else {
                    Location target = area.getTarget().clone();
                    target.setX(target.getBlockX()-area.getCenter().getBlockX()+playerLocation.getX());
                    target.setY(target.getBlockY()-area.getCenter().getBlockY()+playerLocation.getY());
                    target.setZ(target.getBlockZ()-area.getCenter().getBlockZ()+playerLocation.getZ());
                    if(area.isKeepOrientation()) {
                        target.setPitch(playerLocation.getPitch());
                        target.setYaw(playerLocation.getYaw());
                    }
                    player.teleport(target);
                 }
             }
         }
     }
}
