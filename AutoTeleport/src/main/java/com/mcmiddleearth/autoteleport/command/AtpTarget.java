/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.autoteleport.command;

import com.mcmiddleearth.autoteleport.data.PluginData;
import com.mcmiddleearth.autoteleport.data.TeleportationArea;
import com.mcmiddleearth.pluginutil.message.MessageUtil;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class AtpTarget extends AtpCommand{
    
    public AtpTarget(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Defines a teleporation target.");
        setUsageDescription(" <AreaName> [type] [keepOrientation]: Defines the teleportation target for area <AreaName>. [Type] can be 'dynamic' or 'static'. Only 'dynamic' will keep speed of player. Default is 'dynamic' for in world teleport and 'static' for multi world teleport. If [keepOrientation] is false the player orientation will be set to saved orientation, otherwise it will be unchanged.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        TeleportationArea area = PluginData.getTeleportationArea(args[0]);
        if(area==null) {
            sendNoAreaErrorMessage(cs);
        }
        else {
            boolean dynamic = true;
            boolean dynamicByDefault = true;
            boolean keepOrientation = true;
            for(int i = 1; i< args.length;i++) {
                if(args[i].equalsIgnoreCase("static")) {
                    dynamic = false;
                }
                else if(args[i].equalsIgnoreCase("false")) {
                    keepOrientation = false;
                }
                else if(args[i].equalsIgnoreCase("dynamic")) {
                    dynamic = true;
                    dynamicByDefault = false;
                }
                else if(!(args[i].equalsIgnoreCase("true"))) {
                    sentInvalidArgumentMessage(cs);
                    return;
                }
            }
            if(dynamic && !area.getCenter().getWorld().equals(((Player)cs).getWorld())) {
                dynamic = false;
                if(!dynamicByDefault) {
                    sendDynamicToStaticMessage(cs);
                }
            }
            area.setTarget(((Player)cs).getLocation());
            area.setDynamic(dynamic);
            area.setKeepOrientation(keepOrientation);
            try {
                PluginData.saveData();
            } catch (IOException ex) {
                sendIOErrorMessage(cs);
                Logger.getLogger(AtpTarget.class.getName()).log(Level.SEVERE, null, ex);
            }
            sendTargetSetMessage(cs);
        }
    }

    private void sendTargetSetMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Teleport target set.");
    }
    
}
