/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.autoteleport.command;

import com.mcmiddleearth.autoteleport.AutoTeleportPlugin;
import com.mcmiddleearth.autoteleport.conversation.ConfirmationFactory;
import com.mcmiddleearth.autoteleport.conversation.Confirmationable;
import com.mcmiddleearth.autoteleport.data.CuboidTeleportationArea;
import com.mcmiddleearth.autoteleport.data.PluginData;
import com.mcmiddleearth.autoteleport.data.SphericalTeleportationArea;
import com.mcmiddleearth.autoteleport.data.TeleportationArea;
import com.mcmiddleearth.pluginutils.message.MessageUtil;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class AtpSet extends AtpCommand implements Confirmationable{
    
    private TeleportationArea area;
    
    private String areaName;
    
    private Location center;
    
    private boolean cuboid;
    
    private boolean newDynamic;
    
    public AtpSet(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Defines a teleportation area.");
        setUsageDescription(" <AreaName> [shape]: Location of command sender becomes center of teleportation area with name <AreaName>. Shape can be 'cuboid' or 'sphere'. Defaut is 'cuboid'.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        areaName = args[0];
        area = PluginData.getTeleportationArea(args[0]);
        center = ((Player)cs).getLocation().clone();
        cuboid = true;
        if(args.length>1) {
            if(args[1].equalsIgnoreCase("sphere")) {
                cuboid = false;
            }
            else if(!args[1].equalsIgnoreCase("cuboid")) {
                sentInvalidArgumentMessage(cs);
            }
        }
        if(area==null) {
            if(cuboid) {
                area = new CuboidTeleportationArea(center);
            }
            else {
                area = new SphericalTeleportationArea(center);
            }
            PluginData.addTeleportationArea(areaName, area);
            saveData(cs);
            sendNewAreaMessage(cs);
        }
        else {
            newDynamic = area.isDynamic();
            if(area.isDynamic()
                    && !area.getTarget().getWorld().equals(((Player)cs).getWorld())) {
                newDynamic = false;
                sendDynamicToStaticMessage(cs);
            }
            boolean wasCuboid = area instanceof CuboidTeleportationArea;
            if(cuboid != wasCuboid) {
                new ConfirmationFactory(AutoTeleportPlugin.getPluginInstance()).start((Player) cs, 
                        "Specified shape differs from current shape. "+
                        "You will need to define area size again. Do you want to continue?", this);
                return;
            }
            area.setCenter(center);
            area.setDynamic(newDynamic);
            saveData(cs);
            sendCenterSetMessage(cs);
        }
    }

    private void saveData(CommandSender cs){
        try {
            PluginData.saveData();
        } catch (IOException ex) {
            sendIOErrorMessage(cs);
            Logger.getLogger(AtpSet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void confirmed(Player player) {
        Location target = area.getTarget();
        boolean keepOrientation = area.isKeepOrientation();
        if(cuboid) {
            area = new CuboidTeleportationArea(center);
        }
        else {
            area = new SphericalTeleportationArea(center);
        }
        area.setDynamic(newDynamic);
        area.setTarget(target);
        area.setKeepOrientation(keepOrientation);
        PluginData.addTeleportationArea(areaName, area);
        saveData(player);
        sendCenterAndShapeMessage(player);
    }

    @Override
    public void cancelled(Player player) {
        MessageUtil.sendErrorMessage(player, "You cancelled setting of area. No changes were made.");
    }

    private void sendCenterSetMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "Center of teleport area was moved to your location.");
    }

    private void sendNewAreaMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "New teleport area created.");
    }

    private void sendCenterAndShapeMessage(Player player) {
        MessageUtil.sendInfoMessage(player, "Center and shape of teleport area was changed.");
    }
}
