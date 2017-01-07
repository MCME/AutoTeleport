/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.autoteleport.command;

import com.mcmiddleearth.autoteleport.data.PluginData;
import com.mcmiddleearth.autoteleport.data.TeleportationArea;
import com.mcmiddleearth.pluginutil.NumericUtil;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import com.mcmiddleearth.pluginutil.message.MessageType;
import java.util.ArrayList;
import java.util.List;
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
        setUsageDescription(" [selection]: Lists all teleportation areas which names contains [selection].");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        int pageIndex = 0;
        String selection = "";
        if(args.length>0 && (!NumericUtil.isInt(args[0]))) {
            selection = args[0];
            pageIndex = 1;
        }
        int page = 1;
        if(args.length>pageIndex && NumericUtil.isInt(args[pageIndex])) {
            page = NumericUtil.getInt(args[pageIndex]);
        }
        FancyMessage header = new FancyMessage(MessageType.INFO,PluginData.getMessageUtil())
                                    .addSimple("Teleport areas");
        List<FancyMessage> messages = new ArrayList<>();
        for(String areaName : PluginData.getTeleportAreas().keySet()) {
            if(selection.equals("") || areaName.contains(selection)) {
                TeleportationArea area = PluginData.getTeleportationArea(areaName);
                Location target = area.getTarget();
                FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX,PluginData.getMessageUtil());
                message.addSimple("- ")
                            .addFancy(PluginData.getMessageUtil().STRESSED+areaName,"/atp warp "+areaName,"Click to warp there.")
                            .addSimple(PluginData.getMessageUtil().INFO+": "+area.getType()+" -> ");
                if(target!=null) {
                    message.addFancy(PluginData.getMessageUtil().STRESSED+target.getWorld().getName()+"  ",
                                          "/atp warp "+areaName+" target",
                                          "Click to warp there.");
                }
                else {
                    message.addSimple(PluginData.getMessageUtil().INFO+"NO TARGET");
                }
                messages.add(message);
            }
        }
        PluginData.getMessageUtil().sendFancyListMessage((Player)cs, header, messages, "/atp list "+selection, page);
    }
    
}
