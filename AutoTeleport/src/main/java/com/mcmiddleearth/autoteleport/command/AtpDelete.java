/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.autoteleport.command;

import com.mcmiddleearth.autoteleport.AutoTeleportPlugin;
import com.mcmiddleearth.autoteleport.conversation.ConfirmationFactory;
import com.mcmiddleearth.autoteleport.conversation.Confirmationable;
import com.mcmiddleearth.autoteleport.data.PluginData;
import com.mcmiddleearth.autoteleport.data.TeleportationArea;
import com.mcmiddleearth.pluginutils.message.MessageUtil;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class AtpDelete extends AtpCommand implements Confirmationable{
    
    private String areaName;
    
    public AtpDelete(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Deletes a teleportation area.");
        setUsageDescription(" <AreaName>: Deletes teleportation area with name <AreaName>.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        TeleportationArea area = PluginData.getTeleportationArea(args[0]);
        if(area==null) {
            sendNoAreaErrorMessage(cs);
        }
        else {
            areaName = args[0];
            new ConfirmationFactory(AutoTeleportPlugin.getPluginInstance()).start((Player) cs, 
                        "Do you really want to delete teleportation area "+areaName+"?", this);
        }
    }

    @Override
    public void confirmed(Player player) {
        PluginData.deleteTeleportationArea(areaName);
        try {
            PluginData.saveData();
        } catch (IOException ex) {
            sendIOErrorMessage(player);
            Logger.getLogger(AtpDelete.class.getName()).log(Level.SEVERE, null, ex);
        }
        MessageUtil.sendInfoMessage(player, "Teleportation area was deleted.");
    }

    @Override
    public void cancelled(Player player) {
        MessageUtil.sendErrorMessage(player, "You cancelled deleting of the area.");
    }
    
}
