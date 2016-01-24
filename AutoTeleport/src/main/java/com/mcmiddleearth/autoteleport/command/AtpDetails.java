/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.autoteleport.command;

import com.mcmiddleearth.autoteleport.data.CuboidTeleportationArea;
import com.mcmiddleearth.autoteleport.data.PluginData;
import com.mcmiddleearth.autoteleport.data.SphericalTeleportationArea;
import com.mcmiddleearth.autoteleport.data.TeleportationArea;
import com.mcmiddleearth.autoteleport.util.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Eriol_Eandur
 */
public class AtpDetails extends AtpCommand{
    
    public AtpDetails(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Shows details of a teleportation area.");
        setUsageDescription(" <AreaName>: Shows details of area <AreaName>.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        TeleportationArea area = PluginData.getTeleportationArea(args[0]);
        if(area==null) {
            sendNoAreaErrorMessage(cs);
        }
        else {
            MessageUtil.sendInfoMessage(cs, "Details for teleportation area "+args[0]+".");
            MessageUtil.sendNoPrefixInfoMessage(cs, ChatColor.YELLOW
                                                   +"Center: "+ area.getCenter().getWorld().getName()
                                                   +" "+area.getCenter().getBlockX()
                                                   +" "+area.getCenter().getBlockY()
                                                   +" "+area.getCenter().getBlockZ());
            if(area instanceof CuboidTeleportationArea) {
                CuboidTeleportationArea cuboid = (CuboidTeleportationArea)area;
                MessageUtil.sendNoPrefixInfoMessage(cs, ChatColor.YELLOW+"Cuboid area with"
                                                       +" dx="+cuboid.getSizeX()
                                                       +" dy="+cuboid.getSizeY()
                                                       +" dz="+cuboid.getSizeZ());
            }
            else {
                SphericalTeleportationArea sphere = (SphericalTeleportationArea)area;
                MessageUtil.sendNoPrefixInfoMessage(cs, ChatColor.YELLOW+"Spheric area with "
                                                       +"radius "+sphere.getRadius());
            }
            Location target = area.getTarget();
            if(target!=null) {
                MessageUtil.sendNoPrefixInfoMessage(cs,  ChatColor.YELLOW
                                                       +"Target: "+ area.getTarget().getWorld().getName()
                                                       +" x: "+area.getTarget().getBlockX()
                                                       +" y: "+area.getTarget().getBlockY()
                                                       +" z: "+area.getTarget().getBlockZ());
            }
            else {
                MessageUtil.sendNoPrefixInfoMessage(cs,  ChatColor.YELLOW+ "Target: NO TARGET"); 
            }
            if(area.isKeepOrientation()) {
                MessageUtil.sendNoPrefixInfoMessage(cs, ChatColor.YELLOW
                                                        +area.getType()+" teleport with unchanged orientation.");
            }
            else {
                MessageUtil.sendNoPrefixInfoMessage(cs, ChatColor.YELLOW
                                                    +area.getType()+" teleport with orientation set to:");
                MessageUtil.sendNoPrefixInfoMessage(cs, ChatColor.YELLOW
                                                    +"    Yaw: "+(target!=null?area.getTarget().getYaw():"NO TARGET")
                                                    +" - Pitch: "+(target!=null?area.getTarget().getPitch():"NO TARGET"));
            }
        }
    }
    
}
