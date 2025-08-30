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
import com.mcmiddleearth.autoteleport.util.DevUtil;
import com.mcmiddleearth.connect.util.ConnectUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * @author Eriol_Eandur
 */
public class TeleportationHandler {

    private final TeleportationArea area;

    final Player player;

    Location target;

    public TeleportationHandler(Player player, TeleportationArea area) {
        this.player = player;
        this.area = area;
    }

    public void startTeleportation() {
        target = calculateTarget();
        if (area.getServer() == null || area.getServer().equals("")) {
            final Vector vel = player.getVelocity();
            area.loadTargetChunks();
            new BukkitRunnable() {

                int waitTics = 0;

                @Override
                public void run() {
                    if (area.isChunkListLoaded()) {
                        if (waitTics < area.getTeleportDelay()) {
                            DevUtil.log("-----> waiting....");
                            waitTics++;
                            return;
                        }
                        Location loc = target;
                        if (area.isRecalculateTarget()) {
                            loc = calculateTarget();
                        }
                        player.teleport(loc, TeleportCause.END_PORTAL);
                        DevUtil.log("-----> teleport! " + player.getName() + " " + loc.getBlockX() + " " + loc.getBlockZ());
                        if (area.isDynamic()) {
                            new BukkitRunnable() {
                                int reps = 0;

                                @Override
                                public void run() {
                                    if (reps < area.getVelocityReps()) {
                                        DevUtil.log("-----> set velocity");
                                        player.setVelocity(vel);
                                        reps++;
                                    } else {
                                        this.cancel();
                                    }
                                }
                            }.runTaskTimer(AutoTeleportPlugin.getPluginInstance(), area.getVelocityDelay(), 1);
                        }
                        PluginData.ungegisterTeleportation(player);
                        this.cancel();
                    } else {
                        DevUtil.log("-----> waiting for chunk preloading ");
                    }
                }
            }.runTaskTimer(AutoTeleportPlugin.getPluginInstance(), area.getFirstDelay(), 1);
        } else {
            ConnectUtil.teleportPlayer(player, area.getServer(), area.getCrossServerWorld(), target);
            new BukkitRunnable() {
                @Override
                public void run() {
                    PluginData.ungegisterTeleportation(player);
                }
            }.runTaskLater(AutoTeleportPlugin.getPluginInstance(), 100);
        }
    }

    private Location calculateTarget() {
        Location newTarget = area.getTarget().clone();
        if(area.isTargetFixed()) {
            return newTarget;
        }
        Location playerLocation = player.getLocation();
        newTarget.setX(newTarget.getBlockX() - area.getLocation().getBlockX() + playerLocation.getX());
        newTarget.setY(newTarget.getBlockY() - area.getLocation().getBlockY() + playerLocation.getY());
        newTarget.setZ(newTarget.getBlockZ() - area.getLocation().getBlockZ() + playerLocation.getZ());
        if (area.isKeepOrientation()) {
            newTarget.setPitch(playerLocation.getPitch());
            newTarget.setYaw(playerLocation.getYaw());
        }
        return newTarget;
    }

}
