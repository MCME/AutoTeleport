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
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

/**
 *
 * @author Eriol_Eandur
 */
public class SphericalTeleportationArea extends TeleportationArea {
 
    @Getter
    @Setter
    private int radius;
    
    public SphericalTeleportationArea(Location center) {
        super(center);
    }
    
    public SphericalTeleportationArea(ConfigurationSection config) {
        super(config);
        radius = config.getInt("radius");
    }
    
    public void setSize(int radius) {
        this.radius = radius;
    }

    @Override
    public boolean isInside(Location loc) {
        return getCenter().getWorld().equals(loc.getWorld())
            && getCenter().distance(loc) <= radius;
    }
    
    @Override
    public Map<String,Object> serialize() {
        Map<String,Object> result = super.serialize();
        result.put("radius", radius);
        return result;
    }
}
