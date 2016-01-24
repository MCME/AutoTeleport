/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.autoteleport.command;

import com.mcmiddleearth.autoteleport.data.PluginData;
import com.mcmiddleearth.autoteleport.data.TeleportationArea;
import com.mcmiddleearth.autoteleport.util.MessageUtil;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Eriol_Eandur
 */
public class AtpList extends AtpCommand{
    
    public AtpList(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": Lists all teleportation areas.");
        setUsageDescription(" [selection]: Lists all teleportation areas which names start with [selection].");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        MessageUtil.sendInfoMessage(cs, "Teleportation areas:");
        for(String areaName : PluginData.getTeleportAreas().keySet()) {
            if(args.length==0 || areaName.startsWith(args[0])) {
                TeleportationArea area = PluginData.getTeleportationArea(areaName);
                Location target = area.getTarget();
                MessageUtil.sendNoPrefixInfoMessage(cs, "- "+areaName+": "+area.getType()+" -> "
                                                    +(target!=null?target.getWorld().getName():"NULL"));
            }
        }
        
    }
    
}
