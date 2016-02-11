/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.autoteleport.command;

import com.mcmiddleearth.autoteleport.data.PluginData;
import com.mcmiddleearth.autoteleport.data.TeleportationArea;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Eriol_Eandur
 */
public class AtpConfig extends AtpCommand{
    
    public AtpConfig(String... permissionNodes) {
        super(3, false, permissionNodes);
        setShortDescription(": ");
        setUsageDescription(": ");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        TeleportationArea area = PluginData.getTeleportationArea(args[0]);
        if(area==null && !args[0].equals("All")) {
            sendNoAreaErrorMessage(cs);
            return;
        }
        if(args[0].equals("All")) {
            if(PluginData.getTeleportAreas().isEmpty()) {
                sendNoAreaErrorMessage(cs);
                return;
            }
            for(TeleportationArea search: PluginData.getTeleportAreas().values()) {
                config(cs, search, args);
            }
            area = PluginData.getTeleportAreas().values().iterator().next();
        }
        else {
            config(cs,area,args);
        }
        cs.sendMessage(ChatColor.YELLOW+"Config Data:");
        cs.sendMessage("View distance "+ area.getViewDistance());
        cs.sendMessage("First Delay "+ area.getFirstDelay());
        cs.sendMessage("Teleport Delay "+ area.getTeleportDelay());
        cs.sendMessage("Velocity Delay "+area.getVelocityDelay());
        cs.sendMessage("Velocity Reps "+ area.getVelocityReps());
        cs.sendMessage("Recalc Target "+ area.isRecalculateTarget());
        cs.sendMessage("Refresh Chunks "+ area.isRefreshChunks());
    }
    
    private void config(CommandSender cs, TeleportationArea area, String... args) {
        switch(args[1].charAt(0)) {
            case 'd':
                area.setViewDistance(Integer.parseInt(args[2]));
                break;
            case 'f':
                area.setFirstDelay(Integer.parseInt(args[2]));
                break;
            case 't':
                area.setTeleportDelay(Integer.parseInt(args[2]));
                break;
            case 'v':
                area.setVelocityDelay(Integer.parseInt(args[2]));
                break;
            case 'r':
                area.setVelocityReps(Integer.parseInt(args[2]));
                break;
            case 'c':
                area.setRecalculateTarget(Boolean.parseBoolean(args[2]));
                break;
            case 'e':
                area.setRefreshChunks(Boolean.parseBoolean(args[2]));
                break;
            default:
                cs.sendMessage("Property not found.");
        }
    }
    
}
/*    @Getter
    @Setter
    int viewDistance = 160, // Blöcke == 10 chunks
        firstDelay = 1,
        teleportDelay = 0,
        velocityDelay = 2,
        velocityReps = 1;
    
    @Getter
    @Setter
    boolean recalculateTarget = false,
            refreshChunks = false;*/
