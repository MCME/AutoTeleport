/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.autoteleport.command;

import com.mcmiddleearth.autoteleport.util.MessageUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur, Ivanpl
 */
public abstract class AtpCommand {
    
    private final String[] permissionNodes;
    
    @Getter
    private final int minArgs;
    
    private boolean playerOnly = true;
    
    @Getter
    @Setter
    private String usageDescription, shortDescription;
    
    public AtpCommand(int minArgs, boolean playerOnly, String... permissionNodes) {
        this.minArgs = minArgs;
        this.playerOnly = playerOnly;
        this.permissionNodes = permissionNodes;
    }
    
    public void handle(CommandSender cs, String... args) {
        Player p = null;
        if(cs instanceof Player) {
            p = (Player) cs;
        }
        
        if(p == null && playerOnly) {
            sendPlayerOnlyErrorMessage(cs);
            return;
        }
        
        if(p != null && !hasPermissions(p)) {
            sendNoPermsErrorMessage(p);
            return;
        }
        
        if(args.length < minArgs) {
            sendMissingArgumentErrorMessage(cs);
            return;
        }
        
        execute(cs, args);
    }
    
    protected abstract void execute(CommandSender cs, String... args);
    
    private void sendPlayerOnlyErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You have to be logged in to run this command.");
    }
    
    private void sendNoPermsErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You don't have permission to run this command.");
    }
    
    protected void sendMissingArgumentErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You're missing arguments for this command.");
    }
    
    protected boolean hasPermissions(Player p) {
        if(permissionNodes != null) {
            for(String permission : permissionNodes) {
                if (!p.hasPermission(permission)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    protected void sendNoAreaErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "No teleportation area with that name.");
    }

    protected void sendDynamicToStaticMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, ChatColor.GOLD+"Center and target location will be in different worlds. Teleportation type will be static.");
    }
    
    protected void sentIvalidArgumentMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Invalid shape Argument");
    }

    protected void sendIOErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "There was an error. Teleportation data were NOT saved.");
    }
}
