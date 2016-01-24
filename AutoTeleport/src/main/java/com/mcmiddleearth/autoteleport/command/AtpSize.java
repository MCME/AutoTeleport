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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Eriol_Eandur
 */
public class AtpSize extends AtpCommand{
    
    public AtpSize(String... permissionNodes) {
        super(2, true, permissionNodes);
        setShortDescription(": Defines the size of a teleportation area.");
        setUsageDescription(" <AreaName> <size>: Defines the size of <AreaName>. Size must be a single whole number for spherical areas and three whole numbers separated with whitespaces for cuboid areas.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        TeleportationArea area = PluginData.getTeleportationArea(args[0]);
        if(area==null) {
            sendNoAreaErrorMessage(cs);
        }
        else {
            if(area instanceof SphericalTeleportationArea) {
                int radius = parseInt(cs, args[1]);
                if(radius==-1) {
                    return;
                }
                ((SphericalTeleportationArea)area).setRadius(radius);
            }
            else {
                if(args.length<4) {
                    sendMissingArgumentErrorMessage(cs);
                    return;
                }
                Integer xSize = parseInt(cs, args[1]);
                if(xSize==-1) {
                    return;
                }
                Integer ySize = parseInt(cs, args[2]);
                if(ySize==-1) {
                    return;
                }
                Integer zSize = parseInt(cs, args[3]);
                if(zSize==-1) {
                    return;
                }
Logger.getGlobal().info(xSize+" "+ySize+" "+zSize);
                ((CuboidTeleportationArea)area).setSize(xSize,ySize,zSize);
            }
            try {
                PluginData.saveData();
            } catch (IOException ex) {
                sendIOErrorMessage(cs);
                Logger.getLogger(AtpSize.class.getName()).log(Level.SEVERE, null, ex);
            }
            sendSizeSetMessage(cs);
        }
    }

    private int parseInt(CommandSender cs, String arg) {
        try {
            return Integer.parseInt(arg);
        }
        catch(NumberFormatException e) {
            sendNotANumberMessage(cs);
            return -1;
        }
    }

    private void sendSizeSetMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "Size of teleport area set.");
    }

    private void sendNotANumberMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "Invalid argument. Not a whole number.");
    }
}
