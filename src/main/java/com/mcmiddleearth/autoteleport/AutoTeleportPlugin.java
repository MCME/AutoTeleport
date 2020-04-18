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
package com.mcmiddleearth.autoteleport;

import com.mcmiddleearth.autoteleport.command.AtpCommandExecutor;
import com.mcmiddleearth.autoteleport.data.PluginData;
import com.mcmiddleearth.autoteleport.listener.AutoTeleportListener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Eriol_Eandur
 */
public class AutoTeleportPlugin extends JavaPlugin{
 
    private static AutoTeleportPlugin pluginInstance;

    @Override
    public void onEnable() {
        pluginInstance = this;
        PluginData.getMessageUtil().setPluginName("AutoTeleport");
        PluginData.loadData();
        getServer().getPluginManager().registerEvents(new AutoTeleportListener(), this);
        getCommand("atp").setExecutor(new AtpCommandExecutor());
        getLogger().info("Enabled!");
    }

    public static AutoTeleportPlugin getPluginInstance() {
        return pluginInstance;
    }
}
