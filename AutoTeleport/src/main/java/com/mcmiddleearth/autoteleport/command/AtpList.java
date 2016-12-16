/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.autoteleport.command;

import com.mcmiddleearth.autoteleport.data.PluginData;
import com.mcmiddleearth.autoteleport.data.TeleportationArea;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import com.mcmiddleearth.pluginutil.message.MessageType;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        PluginData.getMessageUtil().sendInfoMessage(cs, "Teleportation areas:");
        for(String areaName : PluginData.getTeleportAreas().keySet()) {
            if(args.length==0 || areaName.startsWith(args[0])) {
                TeleportationArea area = PluginData.getTeleportationArea(areaName);
                Location target = area.getTarget();
                FancyMessage fancyMessage = new FancyMessage(MessageType.INFO_INDENTED,PluginData.getMessageUtil());
                fancyMessage.addSimple("- ")
                            .addFancy(PluginData.getMessageUtil().STRESSED+areaName,"/atp warp "+areaName,"Click to warp there.")
                            .addSimple(PluginData.getMessageUtil().INFO+": "+area.getType()+" -> ");
                if(target!=null) {
                    fancyMessage.addFancy(PluginData.getMessageUtil().STRESSED+target.getWorld().getName()+"  ",
                                          "/atp warp "+areaName+" target",
                                          "Click to warp there.");
                }
                else {
                    fancyMessage.addSimple(PluginData.getMessageUtil().INFO+"NO TARGET");
                }
                fancyMessage.send((Player)cs);
            }
        }
        
    }
    
}
