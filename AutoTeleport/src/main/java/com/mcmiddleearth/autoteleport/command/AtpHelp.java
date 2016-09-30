/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.autoteleport.command;

import com.mcmiddleearth.autoteleport.data.PluginData;
import com.mcmiddleearth.pluginutil.NumericUtil;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import com.mcmiddleearth.pluginutil.message.MessageType;
import com.mcmiddleearth.pluginutil.message.MessageUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        Map <String, AtpCommand> commands = ((AtpCommandExecutor)Bukkit.getPluginCommand("atp").getExecutor())
                                                           .getCommands();
        if(args.length<1 || NumericUtil.isInt(args[0])){
            int page = 1;
            if(args.length>0 && NumericUtil.isInt(args[0])) {
                page = NumericUtil.getInt(args[0]);
            }
            FancyMessage header = new FancyMessage(MessageType.INFO,PluginData.getMessageUtil())
                                            .addSimple("Help for "
                                                        +PluginData.getMessageUtil().STRESSED+"AutoTeleport"
                                                        +PluginData.getMessageUtil().INFO+" commands.");
            Set<String> keys = commands.keySet();
            List<FancyMessage> list = new ArrayList<>();
            for(String key : keys) {
                String shortDescription = commands.get(key).getShortDescription();
                String usageDescription = commands.get(key).getUsageDescription();
                if(shortDescription!=null){
                }
                else {
                    shortDescription = ": Sorry, there is no help about this command.";
                }
                if(usageDescription==null){
                    usageDescription = ": Sorry, no help here.";
                }
                int separator = shortDescription.indexOf(":");
                if(separator < 0) {
                    separator = shortDescription.length();
                }
                FancyMessage message = new FancyMessage(MessageType.WHITE,PluginData.getMessageUtil())
                        .addFancy(ChatColor.DARK_AQUA+"/atp "+key+ChatColor.WHITE
                                    +shortDescription.substring(0,separator), 
                                      "/atp "+key, hoverFormat("/atp "+key+usageDescription));
                if(separator<shortDescription.length()) {
                    message.addSimple(shortDescription.substring(separator));
                }
                list.add(message);
            }
            PluginData.getMessageUtil().sendFancyListMessage((Player) cs, header, list, "/atp help ", page);
        }
        else {
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
                    sendHelpStartMessage(cs);
                    int separator = description.indexOf(":");
                    new FancyMessage(MessageType.WHITE,PluginData.getMessageUtil())
                                .addClickable(ChatColor.DARK_AQUA+"/atp "+args[0]
                                                        +(separator>0?description.substring(0, separator):"")
                                                        +ChatColor.WHITE+description.substring(separator), 
                                                     "/atp "+args[0])
                                .send((Player)cs);
                }
                else {
                    sendNoDescriptionMessage(cs, args[0]);
                }
            }
        }
        sendManualMessage(cs);
    }

    private String hoverFormat(String hoverMessage) {
        class MyScanner {
            private final Scanner scanner;
            public String currentToken=null;
            public MyScanner(String string) {
                scanner = new Scanner(string);
                scanner.useDelimiter(" ");
                if(scanner.hasNext()) {
                    currentToken = scanner.next();
                }
            }
            public String next() {
                if(scanner.hasNext()) {
                    currentToken = scanner.next();
                } else {
                    currentToken = null;
                }
                return currentToken;
            }
            public boolean hasCurrent() {
                return currentToken != null;
            }
            public boolean hasNext() {
                return scanner.hasNext();
            }
        }
        int LENGTH_OF_LINE = 40;
        String result = ChatColor.GOLD+"";
        int separator = hoverMessage.indexOf(":");
        result = result.concat(hoverMessage.substring(0,separator+1)+"\n");
        MyScanner scanner = new MyScanner(hoverMessage.substring(separator+1));
        while (scanner.hasCurrent()) {
            String line = ChatColor.YELLOW+scanner.currentToken+" ";
            scanner.next();
            while(scanner.hasCurrent() && line.length()+scanner.currentToken.length()<LENGTH_OF_LINE) {
                line = line.concat(scanner.currentToken+" ");
                scanner.next();
            }
            if(scanner.hasCurrent()) {
                line = line.concat("\n");
            }
            result = result.concat(line);
        }
        return result;
    }
    
    private void sendHelpStartMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Help for "+PluginData.getMessageUtil().STRESSED+"AutoTeleport "+PluginData.getMessageUtil().INFO+"plugin.");
    }

    private void sendNoSuchCommandMessage(CommandSender cs, String arg) {
        PluginData.getMessageUtil().sendNoPrefixInfoMessage(cs, "/atp "+arg+": There is no such command.");    
    }

    private void sendNoDescriptionMessage(CommandSender cs, String arg) {
        PluginData.getMessageUtil().sendNoPrefixInfoMessage(cs, "/atp "+arg+": There is no help for this command.");
    }

   private void sendManualMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendNoPrefixInfoMessage(cs, "Manual: https://docs.google.com/document/d/1f6pMusnowg2usxeSyNXQ0X1bRnNnMPxocmU1u5FZDAE/edit.");
    }

}