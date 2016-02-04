/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.autoteleport.command;

import com.mcmiddleearth.autoteleport.util.MessageUtil;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Eriol_Eandur
 */
public class AtpHelp extends AtpCommand{
    
    public AtpHelp(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": Help for AutoTeleportation.");
        setUsageDescription(" [subcommand]: Detailed help for [subcommand].");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        sendHelpStartMessage(cs);
        Map <String, AtpCommand> commands = ((AtpCommandExecutor)Bukkit.getPluginCommand("atp").getExecutor())
                                                           .getCommands();
        if(args.length>0){
            AtpCommand command = commands.get(args[0]);
            if(command==null) {
                sendNoSuchCommandMessage(cs, args[0]);
            }
            else {
                String description = command.getUsageDescription();
                if(description==null){
                    description = command.getShortDescription();
                }
                if(description!=null){
                    sendDescriptionMessage(cs, args[0], description);
                }
                else {
                    sendNoDescriptionMessage(cs, args[0]);
                }
            }
        }
        else {
            Set<String> keys = commands.keySet();
            for(String key : keys) {
                String description = commands.get(key).getShortDescription();
                if(description!=null){
                    sendDescriptionMessage(cs, key, description);
                }
                else {
                    sendNoDescriptionMessage(cs, key);
                }
            }
        }
        sendManualMessage(cs);
    }

    private void sendHelpStartMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "Help for AutoTeleport plugin.");
    }

    private void sendNoSuchCommandMessage(CommandSender cs, String arg) {
        MessageUtil.sendNoPrefixInfoMessage(cs, "/atp "+arg+": There is no such command.");    
    }

    private void sendDescriptionMessage(CommandSender cs, String arg, String description) {
        MessageUtil.sendNoPrefixInfoMessage(cs, "/game "+arg+description);
    }

    private void sendNoDescriptionMessage(CommandSender cs, String arg) {
        MessageUtil.sendNoPrefixInfoMessage(cs, "/game "+arg+": There is no help for this command.");
    }

   private void sendManualMessage(CommandSender cs) {
        MessageUtil.sendNoPrefixInfoMessage(cs, "Manual: https://docs.google.com/document/d/1f6pMusnowg2usxeSyNXQ0X1bRnNnMPxocmU1u5FZDAE/edit.");
    }

}