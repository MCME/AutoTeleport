/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.autoteleport.command;

import com.mcmiddleearth.autoteleport.data.PluginData;
import com.mcmiddleearth.pluginutil.message.MessageUtil;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class AtpInclude extends AtpCommand{
    
    public AtpInclude(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": Include a player to automated teleport.");
        setUsageDescription(" [who]: Includes a player with name [who] to automated teleport. You can use 'all' for [who] to include all excluded players. With no optional argument the player who issues the command is included.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        Player player;
        if(args.length>0) {
            if(args[0].equalsIgnoreCase("all")) {
                PluginData.includeAll();
                try {
                    PluginData.saveData();
                } catch (IOException ex) {
                    sendIOErrorMessage(cs);
                    Logger.getLogger(AtpInclude.class.getName()).log(Level.SEVERE, null, ex);
                }
                sendAllIncludedMessage(cs);
                return;
            }
            else {
                player = Bukkit.getPlayer(args[0]);
                if(player == null) {
                    sendPlayerNotFoundMessage(cs);
                    return;
                }
            }
        }
        else {
            player = (Player)cs;
        }
        PluginData.include(player);
        try {
            PluginData.saveData();
        } catch (IOException ex) {
            sendIOErrorMessage(cs);
            Logger.getLogger(AtpInclude.class.getName()).log(Level.SEVERE, null, ex);
        }
        PluginData.getMessageUtil().sendInfoMessage(cs, player.getName()+" is now included to automated teleport.");
    }

    private void sendPlayerNotFoundMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Player not found.");
    }

    private void sendAllIncludedMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "All players are included to automated teleport now.");
    }

}
