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

import java.util.Map;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

/**
 *
 * @author Eriol_Eandur
 */
public class CuboidTeleportationArea extends TeleportationArea {
    
    @Getter
    private int sizeX = 1,
                sizeY = 1,
                sizeZ = 1;
    
    public CuboidTeleportationArea(Location center) {
        super(center);
    }
    
    public CuboidTeleportationArea(ConfigurationSection config) {
        super(config);
        sizeX = config.getInt("xSize");
        sizeY = config.getInt("ySize");
        sizeZ = config.getInt("zSize");
    }
    
    public void setSize(int x, int y, int z) {
        sizeX = x;
        sizeY = y;
        sizeZ = z;
    }

    @Override
    public boolean isInside(Location loc) {
        return getCenter().getWorld().equals(loc.getWorld())
            && loc.getBlockX() >= getCenter().getBlockX()-sizeX/2
            && loc.getBlockX() <= getCenter().getBlockX()+sizeX/2
            && loc.getBlockY() >= getCenter().getBlockY()-sizeY/2
            && loc.getBlockY() <= getCenter().getBlockY()+sizeY/2
            && loc.getBlockZ() >= getCenter().getBlockZ()-sizeZ/2
            && loc.getBlockZ() <= getCenter().getBlockZ()+sizeZ/2;
    }
    
    @Override
    public Map<String,Object> serialize() {
        Map<String,Object> result = super.serialize();
        result.put("xSize", sizeX);
        result.put("ySize", sizeY);
        result.put("zSize", sizeZ);
        return result;
    }
}
