/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.autoteleport.command;

import com.mcmiddleearth.autoteleport.data.PluginData;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Eriol_Eandur
 */
public class AtpReload extends AtpCommand{
    
    public AtpReload(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": Reloads all teleportation areas.");
        setUsageDescription(": Reloads all teleportation areas.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        PluginData.loadData();
        PluginData.getMessageUtil().sendInfoMessage(cs, "Teleportation areas reloaded.");
    }
    
}
