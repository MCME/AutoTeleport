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
import com.mcmiddleearth.autoteleport.listener.TeleportationHandler;
import com.mcmiddleearth.pluginutil.message.MessageUtil;
import com.mcmiddleearth.pluginutil.region.CuboidRegion;
import com.mcmiddleearth.pluginutil.region.PrismoidRegion;
import com.mcmiddleearth.pluginutil.region.SphericalRegion;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class PluginData {
    
    @Getter
    private static final MessageUtil messageUtil = new MessageUtil();
    
    @Getter
    private final static Map<String, TeleportationArea> teleportAreas = new HashMap<>();
    
    private final static List<UUID> excludedPlayers = new ArrayList<>();
    
    private final static Map<UUID,TeleportationHandler> currentTeleportations = new HashMap<>();
    
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
    
    public static boolean isInTeleportation(Player player) {
        return currentTeleportations.containsKey(player.getUniqueId());
    }
    
    public static void registerTeleportation(Player player, TeleportationHandler handler) {
        currentTeleportations.put(player.getUniqueId(), handler);
    }
    
    public static void ungegisterTeleportation(Player player) {
        currentTeleportations.remove(player.getUniqueId());
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
    
    public static void exclude(Player player) {
        excludedPlayers.add(player.getUniqueId());
    }
    
    public static void include(Player player) {
        excludedPlayers.remove(player.getUniqueId());
    }
    
    public static void includeAll() {
        excludedPlayers.clear();
    }
    
    public static boolean isExcluded(Player player) {
        return excludedPlayers.contains(player.getUniqueId());
    }
    public static List<OfflinePlayer> excludedPlayers() {
        List<OfflinePlayer> result = new ArrayList<>();
        for(UUID id:excludedPlayers) {
            result.add(Bukkit.getOfflinePlayer(id));
        }
        return result;
    }
    
    public static void saveData() throws IOException {
        FileConfiguration config = new YamlConfiguration();
        for(String areaName : teleportAreas.keySet()) {
            ConfigurationSection section = config.createSection(areaName);
            teleportAreas.get(areaName).save(section);
            //config.set(areaName, teleportAreas.get(areaName).serialize());
        }
        config.save(dataFile);    
    }
    
    public static void loadData() {
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(dataFile);
            for(String areaName : config.getKeys(false)) {
                ConfigurationSection section = config.getConfigurationSection(areaName);
                if(SphericalRegion.isValidConfig(section)) {
                    teleportAreas.put(areaName, 
                            new SphericalTeleportationArea(section));
                }
                else if(PrismoidRegion.isValidConfig(section)) {
                    teleportAreas.put(areaName, 
                            new PrismoidTeleportationArea(section));
                }
                else if(CuboidRegion.isValidConfig(section) || section.contains("xSize")) { // xSize is to notice old data format
                    teleportAreas.put(areaName, 
                            new CuboidTeleportationArea(section));
                }
            }
        } catch (IOException | InvalidConfigurationException ex) {
            //Logger.getLogger(PluginData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
