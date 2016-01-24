/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.autoteleport.command;

import com.mcmiddleearth.autoteleport.data.PluginData;
import com.mcmiddleearth.autoteleport.data.TeleportationArea;
import com.mcmiddleearth.autoteleport.util.MessageUtil;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bukkit.ChatColor;
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
        MessageUtil.sendInfoMessage(cs, "Teleportation areas:");
        for(String areaName : PluginData.getTeleportAreas().keySet()) {
            if(args.length==0 || areaName.startsWith(args[0])) {
                TeleportationArea area = PluginData.getTeleportationArea(areaName);
                Location target = area.getTarget();
                Map<String,String> message = new LinkedHashMap<>();
                message.put(ChatColor.AQUA+MessageUtil.getNOPREFIX()+"- ",null);
                message.put(ChatColor.BLUE+areaName,"/atp warp "+areaName);
                message.put(ChatColor.AQUA+": "+area.getType()+" -> ",null);
                if(target!=null) {
                    message.put(ChatColor.BLUE+target.getWorld().getName()+"  ","/atp warp "+areaName+" target");
                }
                else {
                    message.put(ChatColor.AQUA+"NO TARGET",null);
                }
                MessageUtil.sendClickableMessage((Player)cs, message);
            }
        }
        
    }
    
}
