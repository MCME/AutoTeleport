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
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import com.mcmiddleearth.pluginutil.message.MessageType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            PluginData.getMessageUtil().sendInfoMessage(cs, "Details for teleportation area "+args[0]+".");
            new FancyMessage(MessageType.HIGHLIGHT_INDENTED,PluginData.getMessageUtil())
                        .addFancy("Center"+PluginData.getMessageUtil().HIGHLIGHT_STRESSED
                                                   +": "+ area.getCenter().getWorld().getName()
                                                   +" "+area.getCenter().getBlockX()
                                                   +" "+area.getCenter().getBlockY()
                                                   +" "+area.getCenter().getBlockZ(),
                                              "/atp warp "+args[0],
                                              "Click to warp to area. Make sure it is not active ;).")
                        .send((Player) cs);
            if(area instanceof CuboidTeleportationArea) {
                CuboidTeleportationArea cuboid = (CuboidTeleportationArea)area;
                PluginData.getMessageUtil().sendNoPrefixInfoMessage(cs, ChatColor.YELLOW+"Cuboid area with"
                                                       +" dx="+cuboid.getSizeX()
                                                       +" dy="+cuboid.getSizeY()
                                                       +" dz="+cuboid.getSizeZ());
            }
            else {
                SphericalTeleportationArea sphere = (SphericalTeleportationArea)area;
                PluginData.getMessageUtil().sendNoPrefixInfoMessage(cs, ChatColor.YELLOW+"Spheric area with "
                                                       +"radius "+sphere.getRadius());
            }
            Location target = area.getTarget();
            if(target!=null) {
                new FancyMessage(MessageType.HIGHLIGHT_INDENTED,PluginData.getMessageUtil())
                        .addFancy("Target"+ PluginData.getMessageUtil().HIGHLIGHT_STRESSED
                                                       +": "+ area.getTarget().getWorld().getName()
                                                       +" x: "+area.getTarget().getBlockX()
                                                       +" y: "+area.getTarget().getBlockY()
                                                       +" z: "+area.getTarget().getBlockZ(),
                                                 "/atp warp "+args[0]+" target",
                                                 "Click here to warp to target area.")
                        .send((Player)cs);
            }
            else {
                PluginData.getMessageUtil().sendNoPrefixInfoMessage(cs,  ChatColor.YELLOW+ "Target: NO TARGET"); 
            }
            if(area.isKeepOrientation()) {
                PluginData.getMessageUtil().sendNoPrefixInfoMessage(cs, ChatColor.YELLOW
                                                        +area.getType()+" teleport with unchanged orientation.");
            }
            else {
                PluginData.getMessageUtil().sendNoPrefixInfoMessage(cs, ChatColor.YELLOW
                                                    +area.getType()+" teleport with orientation set to:");
                PluginData.getMessageUtil().sendNoPrefixInfoMessage(cs, ChatColor.YELLOW
                                                    +"    Yaw: "+(target!=null?area.getTarget().getYaw():"NO TARGET")
                                                    +" - Pitch: "+(target!=null?area.getTarget().getPitch():"NO TARGET"));
            }
        }
    }
    
}
