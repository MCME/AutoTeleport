/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.autoteleport.command;

import com.mcmiddleearth.autoteleport.data.PluginData;
import com.mcmiddleearth.autoteleport.data.TeleportationArea;
import com.mcmiddleearth.autoteleport.util.MessageUtil;
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
        super(2, true, permissionNodes);
        setShortDescription(": Defines a teleporation target.");
        setUsageDescription(" <AreaName> <type> [keepOrientation]: Defines the teleportation target for area <AreaName>. Type can be 'dynamic' or 'static'. Only 'dynamic' will keep speed of player. If keepOrientation is false the players orientation will be set to saved orientation, otherwise it will be unchanged.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        TeleportationArea area = PluginData.getTeleportationArea(args[0]);
        if(area==null) {
            sendNoAreaErrorMessage(cs);
        }
        else {
            boolean dynamic = args[1].equalsIgnoreCase("dynamic");
            if(dynamic && !area.getCenter().getWorld().equals(((Player)cs).getWorld())) {
                dynamic = false;
                sendDynamicToStaticMessage(cs);
            }
            area.setTarget(((Player)cs).getLocation());
            area.setDynamic(dynamic);
            boolean keepOrientation = true;
            if(args.length>2) {
                if(args[2].equalsIgnoreCase("false")) {
                    keepOrientation = false;
                }
                else if(!args[2].equalsIgnoreCase("true")) {
                    sentIvalidArgumentMessage(cs);
                }
            }
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
        MessageUtil.sendInfoMessage(cs, "Teleport target set.");
    }
    
}
