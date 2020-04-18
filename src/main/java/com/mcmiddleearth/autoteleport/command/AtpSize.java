/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.autoteleport.command;

import com.mcmiddleearth.autoteleport.data.CuboidTeleportationArea;
import com.mcmiddleearth.autoteleport.data.PluginData;
import com.mcmiddleearth.autoteleport.data.PrismoidTeleportationArea;
import com.mcmiddleearth.autoteleport.data.SphericalTeleportationArea;
import com.mcmiddleearth.autoteleport.data.TeleportationArea;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;

/**
 *
 * @author Eriol_Eandur
 */
public class AtpSize extends AtpCommand{
    
    public AtpSize(String... permissionNodes) {
        super(2, true, permissionNodes);
        setShortDescription(": Defines the size of a teleportation area.");
        setUsageDescription(" <AreaName> <size>: Defines the size of <AreaName>. Defines the size of <AreaName>. <size> must be: \nFor spherical areas: <radius>\nFor cuboid areas: <x1 y1 z1 x2 y2 z2> (coords of opposite corners)\nFor prism areas: <y1 y2> (Height range)");
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
                    sendNotANumberMessage(cs);
                    return;
                }
                ((SphericalTeleportationArea)area).setRadius(radius);
            }
            else if(area instanceof CuboidTeleportationArea) {
                if(args.length<7) {
                    sendMissingArgumentErrorMessage(cs);
                    return;
                }
                int[] data = new int[6];
                for(int i = 0; i<6; i++) {
                    data[i] = parseInt(cs, args[i+1]);
                    if(data[i]==-1) {
                        sendNotANumberMessage(cs);
                        return;
                    }
                }
                ((CuboidTeleportationArea)area).setCorners(new Vector(data[0],data[1],data[2]),
                                                           new Vector(data[3],data[4],data[5]));
            } else {
                if(args.length<3) {
                    sendMissingArgumentErrorMessage(cs);
                    return;
                }
                int[] data = new int[2];
                for(int i = 0; i<2; i++) {
                    data[i] = parseInt(cs, args[i+1]);
                    if(data[i]==-1) {
                        sendNotANumberMessage(cs);
                        return;
                    }
                }
                ((PrismoidTeleportationArea)area).setHeight(data[0],data[1]);
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
        PluginData.getMessageUtil().sendInfoMessage(cs, "Size of teleport area set.");
    }

    private void sendNotANumberMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Invalid argument. Not a whole number.");
    }
}
