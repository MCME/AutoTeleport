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

import com.mcmiddleearth.autoteleport.AutoTeleportPlugin;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Eriol_Eandur
 */
public class PluginData {
    
    @Getter
    private static Map<String, TeleportationArea> teleportAreas = new HashMap<>();
    
    @Getter
    @Setter
    private static boolean stopped = false;
    
    @Getter
    private static final File dataFile = new File(AutoTeleportPlugin.getPluginInstance().getDataFolder(),
                                                  File.separator+"PluginData.yml");
    
    static {
        if(!AutoTeleportPlugin.getPluginInstance().getDataFolder().exists()) {
            AutoTeleportPlugin.getPluginInstance().getDataFolder().mkdirs();
        }
    }
        
    public static TeleportationArea addTeleportationArea(String name, TeleportationArea newArea) {
        return teleportAreas.put(name, newArea);
    }
    
    public static TeleportationArea deleteTeleportationArea(String name) {
        return teleportAreas.remove(name);
    }
    
    public static TeleportationArea getTeleportationArea(String name) {
        return teleportAreas.get(name);
    }
    
    public static boolean hasTeleportationArea(String name) {
        return teleportAreas.get(name)!=null;
    }
    
    public static void saveData() throws IOException {
        FileConfiguration config = new YamlConfiguration();
        for(String areaName : teleportAreas.keySet()) {
            config.set(areaName, teleportAreas.get(areaName).serialize());
        }
        config.save(dataFile);    
    }
    
    public static void loadData() {
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(dataFile);
            for(String areaName : config.getKeys(false)) {
                if(config.getConfigurationSection(areaName).contains("radius")) {
                    teleportAreas.put(areaName, 
                            new SphericalTeleportationArea(config.getConfigurationSection(areaName)));
                }
                else {
                    teleportAreas.put(areaName, 
                            new CuboidTeleportationArea(config.getConfigurationSection(areaName)));
                }
            }
        } catch (IOException | InvalidConfigurationException ex) {
            Logger.getLogger(PluginData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
