/*
 * Copyright (C) 2016 MCME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mcmiddleearth.autoteleport.data;

import static com.mcmiddleearth.autoteleport.data.TeleportationArea.deserializeLocation;
import com.mcmiddleearth.pluginutil.region.CuboidRegion;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

/**
 *
 * @author Eriol_Eandur
 */
public class CuboidTeleportationArea extends TeleportationArea {
    
    /*@Getter
    private int sizeX = 1,
                sizeY = 1,
                sizeZ = 1;*/
    
    public CuboidTeleportationArea(Location center, com.sk89q.worldedit.regions.CuboidRegion weRegion) {
       region = new CuboidRegion(center, weRegion);
    }
    
    public CuboidTeleportationArea(ConfigurationSection config) {
        super(config);
        if(config.contains("center")) {
            Location center = deserializeLocation(config.getConfigurationSection("center"));
            int sizeX = config.getInt("xSize");
            int sizeY = config.getInt("ySize");
            int sizeZ = config.getInt("zSize");
            Vector minPos = new Vector(center.getBlockX()-sizeX/2,
                                       center.getBlockY()-sizeY/2,
                                       center.getBlockZ()-sizeZ/2);
            Vector maxPos = new Vector(center.getBlockX()+sizeX/2,
                                       center.getBlockY()+sizeY/2,
                                       center.getBlockZ()+sizeZ/2);
            region = new CuboidRegion(center,minPos, maxPos);
        } else {
            region = CuboidRegion.load(config);
        }
        /*sizeX = config.getInt("xSize");
        sizeY = config.getInt("ySize");
        sizeZ = config.getInt("zSize");*/
        //region = CuboidRegion.load(config);
    }
    
    public void setCorners(Vector pos1, Vector pos2) {
        ((CuboidRegion)region).setCorners(pos1, pos2);
        /*sizeX = x;
        sizeY = y;
        sizeZ = z;*/
    }

    public Vector getMinPos() {
        return ((CuboidRegion)region).getMinCorner();
    }
    
    public Vector getMaxPos() {
        return ((CuboidRegion)region).getMaxCorner();
    }
    
    /*@Override
    public boolean isInside(Location loc) {
        return region.isInside(loc);
                /*getCenter().getWorld().equals(loc.getWorld())
            && loc.getBlockX() >= getCenter().getBlockX()-sizeX/2
            && loc.getBlockX() <= getCenter().getBlockX()+sizeX/2
            && loc.getBlockY() >= getCenter().getBlockY()-sizeY/2
            && loc.getBlockY() <= getCenter().getBlockY()+sizeY/2
            && loc.getBlockZ() >= getCenter().getBlockZ()-sizeZ/2
            && loc.getBlockZ() <= getCenter().getBlockZ()+sizeZ/2;*/
    /*}
    
    @Override
    public boolean isNear(Location loc) {
//Logger.getGlobal().info("cuboid isNear "+getPreloadDistance());
        return region.isNear(loc, getPreloadDistance());
                /*getCenter().getWorld().equals(loc.getWorld())
            && loc.getBlockX() >= getCenter().getBlockX()-(sizeX+getPreloadDistance())/2
            && loc.getBlockX() <= getCenter().getBlockX()+(sizeX+getPreloadDistance())/2
            && loc.getBlockY() >= getCenter().getBlockY()-(sizeY+getPreloadDistance())/2
            && loc.getBlockY() <= getCenter().getBlockY()+(sizeY+getPreloadDistance())/2
            && loc.getBlockZ() >= getCenter().getBlockZ()-(sizeZ+getPreloadDistance())/2
            && loc.getBlockZ() <= getCenter().getBlockZ()+(sizeZ+getPreloadDistance())/2;*/
    //}
    
    /*@Override
    public Map<String,Object> serialize() {
        Map<String,Object> result = super.serialize();
        result.put("xSize", sizeX);
        result.put("ySize", sizeY);
        result.put("zSize", sizeZ);
        return result;
    }*/
}
