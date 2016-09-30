/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.autoteleport.command;

import com.mcmiddleearth.autoteleport.data.PluginData;
import com.mcmiddleearth.pluginutil.message.MessageUtil;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Eriol_Eandur
 */
public class AtpOff extends AtpCommand{
    
    public AtpOff(String... permissionNodes) {
        super(0, false, permissionNodes);
        setShortDescription(": Deactivates teleportation areas.");
        setUsageDescription(": Deactivates teleportation areas.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        PluginData.setStopped(true);
        PluginData.getMessageUtil().sendInfoMessage(cs, "Automated teleport deactivated.");
    }
    
}
