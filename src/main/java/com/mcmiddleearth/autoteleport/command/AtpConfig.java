/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.autoteleport.command;

import com.mcmiddleearth.autoteleport.data.PluginData;
import com.mcmiddleearth.autoteleport.data.TeleportationArea;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Eriol_Eandur
 */
public class AtpConfig extends AtpCommand{
    
    public AtpConfig(String... permissionNodes) {
        super(3, false, permissionNodes);
        setShortDescription(": configures teleportation details");
        setUsageDescription(": DONT'T USE");
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
        if(area==null) {
            sendNoAreaErrorMessage(cs);
            return;
        }
        try {
            PluginData.saveData();
        } catch (IOException ex) {
            sendIOErrorMessage(cs);
            Logger.getLogger(AtpTarget.class.getName()).log(Level.SEVERE, null, ex);
        }
        cs.sendMessage(ChatColor.YELLOW+"Config Data:");
        cs.sendMessage(ChatColor.BLUE+"P"+ChatColor.AQUA+"reload distance "+ area.getPreloadDistance());
        cs.sendMessage("View "+ChatColor.BLUE+"D"+ChatColor.AQUA+"istance "+ area.getViewDistance());
        cs.sendMessage(ChatColor.BLUE+"F"+ChatColor.AQUA+"irst Delay "+ area.getFirstDelay());
        cs.sendMessage(ChatColor.BLUE+"T"+ChatColor.AQUA+"eleport Delay "+ area.getTeleportDelay());
        cs.sendMessage(ChatColor.BLUE+"V"+ChatColor.AQUA+"elocity Delay "+area.getVelocityDelay());
        cs.sendMessage("Velocity "+ChatColor.BLUE+"R"+ChatColor.AQUA+"eps "+ area.getVelocityReps());
        cs.sendMessage(ChatColor.BLUE+"R"+ChatColor.AQUA+"ecalc Target "+ area.isRecalculateTarget());
    }
    
    private void config(CommandSender cs, TeleportationArea area, String... args) {
        switch(args[1].charAt(0)) {
            case 'p':
                area.setPreloadDistance(Integer.parseInt(args[2]));
                break;
            case 'd':
                Logger.getGlobal().info("call set view Distance");
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
            default:
                cs.sendMessage("Property not found.");
        }
    }
    
}