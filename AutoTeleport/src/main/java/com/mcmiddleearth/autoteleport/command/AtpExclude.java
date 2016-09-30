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
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class AtpExclude extends AtpCommand{
    
    public AtpExclude(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": Excludes a player from automated teleport.");
        setUsageDescription(" [who]: Excludes a player [who] from automated teleport. With no optional argument the player who issues the command is exluded. You can use 'list' for [who] instead of a player name to see a list of all excluded players.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        Player player;
        if(args.length>0) {
            if(args[0].equalsIgnoreCase("list")) {
                sendExcludedListMessage(cs);
                return;
            }
            player = Bukkit.getPlayer(args[0]);
            if(player == null) {
                sendPlayerNotFoundMessage(cs);
                return;
            }
        }
        else {
            player = (Player)cs;
        }
        PluginData.exclude(player);
        try {
            PluginData.saveData();
        } catch (IOException ex) {
            sendIOErrorMessage(cs);
            Logger.getLogger(AtpExclude.class.getName()).log(Level.SEVERE, null, ex);
        }
        PluginData.getMessageUtil().sendInfoMessage(cs, player.getName()+" is now excluded from automated teleport.");
    }

    private void sendPlayerNotFoundMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Player not found.");
    }
    
    private void sendExcludedListMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs,"Players excluded from automated teleport:");
        for(OfflinePlayer player: PluginData.excludedPlayers()) {
            PluginData.getMessageUtil().sendNoPrefixInfoMessage(cs, "- "+player.getName());
        }
    }
    
}
