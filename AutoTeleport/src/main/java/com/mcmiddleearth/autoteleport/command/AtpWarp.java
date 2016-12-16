/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.autoteleport.command;

import com.mcmiddleearth.autoteleport.data.PluginData;
import com.mcmiddleearth.autoteleport.data.TeleportationArea;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class AtpWarp extends AtpCommand{
    
    public AtpWarp(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Warp to a teleportation area.");
        setUsageDescription(" <AreaName> [where]: Warps the player who issues this command to teleport area <AreaName>. Optional argument [where] can be center or target. Default is center.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        TeleportationArea area = PluginData.getTeleportationArea(args[0]);
        if(area==null) {
            sendNoAreaErrorMessage(cs);
        }
        else {
            if(args.length>1) {
                if(args[1].equalsIgnoreCase("target")) {
                    ((Player)cs).teleport(area.getTarget());
                    sendWelcomeToTarget(cs, args[0]);
                    return;
                }
                else if(!args[1].equalsIgnoreCase("center")) {
                    sentInvalidArgumentMessage(cs);
                    return;
                }
            }
            ((Player)cs).teleport(area.getLocation());
            sendWelcomeToCenter(cs, args[0]);
        }
    }

    private void sendWelcomeToTarget(CommandSender cs, String arg) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "You are now at target of teleport area "+arg+".");
    }

    private void sendWelcomeToCenter(CommandSender cs, String arg) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "You are now at center of teleport area "+arg+".");
    }
    
}
