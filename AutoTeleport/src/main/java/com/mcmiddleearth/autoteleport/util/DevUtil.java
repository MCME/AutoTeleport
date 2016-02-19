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
package com.mcmiddleearth.autoteleport.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class DevUtil {
    
    @Getter
    private static List<Player> developer = new ArrayList<>();
    
    private static final String PREFIX = ""+ChatColor.BOLD+ChatColor.GOLD;
    private static final String PLUGIN = "[ATP] ";
    
    @Setter
    @Getter
    private static boolean consoleOutput = false;
    
    @Setter
    @Getter
    private static int level = 1;
    
    public static void log(String message) {
        log(1,message);
    }
    public static void log(int msglevel, String message) {
        if(level<msglevel) {
            return;
        }
        for(Player player:developer) {
            player.sendMessage(PREFIX+PLUGIN+message);
        }
        if(consoleOutput) {
            Logger.getGlobal().info(PLUGIN+message);
        }
    }
    
    public static void add(Player player) {
        for(Player search: developer) {
            if(search.getUniqueId().equals(player.getUniqueId())) {
                return;
            }
        }
        developer.add(player);
    }
    
    public static void remove(Player player) {
        for(Player search: developer) {
            if(search.getUniqueId().equals(player.getUniqueId())) {
                developer.remove(search);
            }
        }
    }
    

}
