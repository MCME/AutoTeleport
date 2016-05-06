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
package com.mcmiddleearth.autoteleport.command;

import com.mcmiddleearth.autoteleport.AutoTeleportPlugin;
import com.mcmiddleearth.pluginutils.message.MessageUtil;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

/**
 *
 * @author Eriol_Eandur
 */
public class AtpCommandExecutor implements CommandExecutor {

    @Getter
    private final Map <String, AtpCommand> commands = new LinkedHashMap <>();
    
    private final String permission = "autoteleport.user";
    public AtpCommandExecutor() {
        addCommandHandler("config", new AtpConfig(permission));
        addCommandHandler("delete", new AtpDelete(permission));
        addCommandHandler("details", new AtpDetails(permission));
        addCommandHandler("dev", new AtpDev(permission));
        addCommandHandler("exclude", new AtpExclude(permission));
        addCommandHandler("help", new AtpHelp(permission));
        addCommandHandler("include", new AtpInclude(permission));
        addCommandHandler("list", new AtpList(permission));
        addCommandHandler("off", new AtpOff(permission));
        addCommandHandler("on", new AtpOn(permission));
        addCommandHandler("set", new AtpSet(permission));
        addCommandHandler("size", new AtpSize(permission));
        addCommandHandler("target", new AtpTarget(permission));
        addCommandHandler("warp", new AtpWarp(permission));
    }
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if(!string.equalsIgnoreCase("atp")) {
            return false;
        }
        if(strings == null || strings.length == 0) {
            sendNoSubcommandErrorMessage(cs);
            return true;
        }
        if(commands.containsKey(strings[0].toLowerCase())) {
            commands.get(strings[0].toLowerCase()).handle(cs, Arrays.copyOfRange(strings, 1, strings.length));
        } else {
            sendSubcommandNotFoundErrorMessage(cs);
        }
        return true;
    }
    
    private void sendNoSubcommandErrorMessage(CommandSender cs) {
        //MessageUtil.sendErrorMessage(cs, "You're missing subcommand name for this command.");
        PluginDescriptionFile descr = AutoTeleportPlugin.getPluginInstance().getDescription();
        MessageUtil.sendErrorMessage(cs, descr.getName()+" - version "+descr.getVersion());
    }
    
    private void sendSubcommandNotFoundErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Subcommand not found.");
    }
    
    private void addCommandHandler(String name, AtpCommand handler) {
        commands.put(name, handler);
    }
    
}
