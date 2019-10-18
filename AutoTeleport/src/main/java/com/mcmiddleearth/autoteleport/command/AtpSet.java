/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.autoteleport.command;

import com.boydti.fawe.object.FawePlayer;
import com.mcmiddleearth.autoteleport.AutoTeleportPlugin;
import com.mcmiddleearth.autoteleport.conversation.ConfirmationFactory;
import com.mcmiddleearth.autoteleport.conversation.Confirmationable;
import com.mcmiddleearth.autoteleport.data.CuboidTeleportationArea;
import com.mcmiddleearth.autoteleport.data.PluginData;
import com.mcmiddleearth.autoteleport.data.PrismoidTeleportationArea;
import com.mcmiddleearth.autoteleport.data.SphericalTeleportationArea;
import com.mcmiddleearth.autoteleport.data.TeleportationArea;
import com.mcmiddleearth.pluginutil.NumericUtil;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class AtpSet extends AtpCommand implements Confirmationable{
    
    private TeleportationArea area;
    
    private String areaName;
    
    private Location location;
    
    private boolean notSpherical;
    
    private boolean newDynamic;

    private Region region = null;
    
    private int radius;

    public AtpSet(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Defines a teleportation area.");
        setUsageDescription(" <AreaName> [shape]: Location of command sender becomes center of teleportation area with name <AreaName>. Shape can be 'cuboid' or 'sphere'. Defaut is 'cuboid'.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        areaName = args[0];
        area = PluginData.getTeleportationArea(args[0]);
        location = ((Player)cs).getLocation().clone();
        notSpherical = true;
        Player p = (Player) cs;
        if(args.length>1 && args[1].equalsIgnoreCase("sphere")) {
            if(args.length>2) {
                if(NumericUtil.isInt(args[2])) {
                    notSpherical = false;
                    radius = NumericUtil.getInt(args[2]);
                } else {
                    sentInvalidArgumentMessage(cs);
                    return;
                }
            } else {
                sendMissingArgumentErrorMessage(cs);
                return;
            }
        } else {
            //try {
                region = FawePlayer.wrap(p).getSelection();
            //} catch (NullPointerException | IncompleteRegionException ex) {}
            if(!(region instanceof CuboidRegion || region instanceof Polygonal2DRegion) ) {
                sendInvalidSelection(p);
                return;
            }
        }
        if(area==null) {
            if(notSpherical) {
                if(region instanceof CuboidRegion) {
//Logger.getGlobal().info("loc: "+location.getWorld().getName()+" region: "+region.getWorld().getName());
                    area = new CuboidTeleportationArea(location, (CuboidRegion)region);
                } else {
                    area = new PrismoidTeleportationArea(location, (Polygonal2DRegion)region);
                }
            }
            else {
                area = new SphericalTeleportationArea(location, radius);
            }
            PluginData.addTeleportationArea(areaName, area);
//Logger.getGlobal().info("loc: "+area.getLocation().getWorld());
            saveData(cs);
            sendNewAreaMessage(cs);
        }
        else {
            String message = "A teleportation area with this name already exists. "+
                    "Do you want to redefine it?";
            newDynamic = area.isDynamic();
            if(area.isDynamic()
                    && !area.getTarget().getWorld().equals(((Player)cs).getWorld())) {
                newDynamic = false;
                message = message+" Center and target location will be in different worlds. "
                                 +"Teleportation type will changed to static.";
            }
            /*boolean wasCuboid = area instanceof CuboidTeleportationArea;
            if(cuboid != wasCuboid) {*/
            new ConfirmationFactory(AutoTeleportPlugin.getPluginInstance()).start((Player) cs, 
                                    message,this);
                    /*"Specified shape differs from current shape. "+
                    "You will need to define area size again. Do you want to continue?", this);
            /*    return;
            }
            area.setCenter(location);
            area.setDynamic(newDynamic);
            saveData(cs);
            sendCenterSetMessage(cs);*/
        }
    }

    private void saveData(CommandSender cs){
        try {
            PluginData.saveData();
        } catch (IOException ex) {
            sendIOErrorMessage(cs);
            Logger.getLogger(AtpSet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void confirmed(Player player) {
        Location target = area.getTarget();
        boolean keepOrientation = area.isKeepOrientation();
        PluginData.deleteTeleportationArea(areaName);
        if(notSpherical) {
            if(region instanceof CuboidRegion) {
                area = new CuboidTeleportationArea(location, (CuboidRegion)region);
            } else {
                area = new PrismoidTeleportationArea(location, (Polygonal2DRegion)region);
            }
            //area = new CuboidTeleportationArea(location,(CuboidRegion)region);
        }
        else {
            area = new SphericalTeleportationArea(location, radius);
        }
        area.setDynamic(newDynamic);
        area.setTarget(target);
        area.setKeepOrientation(keepOrientation);
        PluginData.addTeleportationArea(areaName, area);
        saveData(player);
        sendCenterSetMessage(player);
    }

    @Override
    public void cancelled(Player player) {
        PluginData.getMessageUtil().sendErrorMessage(player, "You cancelled setting of area. No changes were made.");
    }

    private void sendCenterSetMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Center of teleport area was moved to your location.");
    }

    private void sendNewAreaMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "New teleport area created.");
    }

    private void sendCenterAndShapeMessage(Player player) {
        PluginData.getMessageUtil().sendInfoMessage(player, "Center and shape of teleport area was changed.");
    }
    
    private void sendInvalidSelection(Player player) {
        PluginData.getMessageUtil().sendErrorMessage(player, "For a cuboid area make a valid WorldEdit selection first.");
    }
}
