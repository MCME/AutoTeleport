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

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

/**
 *
 * @author Eriol_Eandur
 */
public abstract class TeleportationArea {
    
    @Getter
    @Setter
    private Location center;
    
    @Getter
    @Setter
    private Location target;
    
    @Getter
    @Setter
    private boolean dynamic;
    
    @Getter
    @Setter
    private boolean keepOrientation;
    
    public TeleportationArea(Location center) {
        this.center = center;
    }
    
    public TeleportationArea(ConfigurationSection config) {
        this.center = deserializeLocation(config.getConfigurationSection("center"));
        this.target = deserializeLocation(config.getConfigurationSection("target"));
        this.dynamic = config.getBoolean("dynamic");
        this.keepOrientation = config.getBoolean("keepOrientation");
    }
    
    public abstract boolean isInside(Location loc);
    
    public String getType() {
        if(dynamic) {
            return "Dynamic";
        }
        else {
            return "Static";
        }
    }
    
    public Map<String, Object> serialize() {
        Map<String,Object> result = new HashMap();
        result.put("center", serializeLocation(this.center));
        if(target!=null) {
            result.put("target", serializeLocation(this.target));
        }
        else {
            result.put("target", null);
        }
        result.put("dynamic", dynamic);
        result.put("keepOrientation", keepOrientation);
        return result;
    }
    
    private static Map<String,Object> serializeLocation(Location loc) {
        Map<String,Object> result = new HashMap<>();
        result.put("x", loc.getX());
        result.put("y", loc.getY());
        result.put("z", loc.getZ());
        result.put("yaw", loc.getYaw());
        result.put("pitch", loc.getPitch());
        result.put("world", loc.getWorld().getName());
        return result;
    }
    
    private static Location deserializeLocation(ConfigurationSection data) {
        World world = Bukkit.getWorld(data.getString("world"));
        if(world == null) {
            return null;
        }
        else {
            return new Location(world, (Double) data.get("x"), 
                                       (Double) data.get("y"), 
                                       (Double) data.get("z"), 
                                       ((Double) data.get("yaw")).floatValue(), 
                                       ((Double) data.get("pitch")).floatValue());
        }
    }
    

}
