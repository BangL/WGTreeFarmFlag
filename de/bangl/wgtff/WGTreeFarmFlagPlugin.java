/*
 * Copyright (C) 2012-2013 BangL <henno.rickowski@googlemail.com>
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
package de.bangl.wgtff;

import com.mewin.WGCustomFlags.WGCustomFlagsPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import de.bangl.wgtff.listeners.BlockListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author BangL <henno.rickowski@googlemail.com>
 */
public class WGTreeFarmFlagPlugin extends JavaPlugin {

    // Plugins
    private WGCustomFlagsPlugin pluginWGCustomFlags;
    private WorldGuardPlugin pluginWorldGuard;
    private boolean hasBlockRestricter;
    private boolean hasMcMMO;
    private boolean hasQwickTree;

    // Flags
    public static final StateFlag FLAG_TREEFARM = new StateFlag("treefarm", true);

    // Listeners
    private BlockListener listenerBlock;

    @Override
    public void onEnable() {
        // Load config
        Utils.loadConfig(this);

        // Get plugins
        this.pluginWorldGuard = Utils.getWorldGuard(this);
        this.pluginWGCustomFlags = Utils.getWGCustomFlags(this);
        hasBlockRestricter = this.getServer().getPluginManager().getPlugin("WGBlockRestricter") != null;
        hasMcMMO = this.getServer().getPluginManager().getPlugin("mcMMO") != null;
        hasQwickTree = this.getServer().getPluginManager().getPlugin("QwickTree") != null;

        // Register flag
        this.pluginWGCustomFlags.addCustomFlag(FLAG_TREEFARM);

        // Register listeners
        this.listenerBlock = new BlockListener(this);
    }

    @Override
    public void onDisable() {
        // we nullify all vars, cause it could be a server reload and we don't wanna leave trash in our expensive RAM.
        this.pluginWGCustomFlags = null;
        this.pluginWorldGuard = null;
        HandlerList.unregisterAll(this.listenerBlock);
        this.listenerBlock = null;
        this.hasBlockRestricter = false;
        this.hasMcMMO = false;
        this.hasQwickTree = false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getLabel().equalsIgnoreCase("treefarm")) {
            if (args != null
                    && args.length == 1
                    && args[0].equalsIgnoreCase("reload")) {

                // Load config
                this.reloadConfig();
                Utils.loadConfig(this);

                sender.sendMessage("Tree farm config reloaded.");
                return true;
            }
        }
        return false;
    }

    public boolean hasBlockRestricter() {
        return this.hasBlockRestricter;
    }

    public boolean hasMcMMO() {
        return this.hasMcMMO;
    }

    public boolean hasQwickTree() {
        return this.hasQwickTree;
    }

    public WGCustomFlagsPlugin getWGCFP() {
        return this.pluginWGCustomFlags;
    }

    public WorldGuardPlugin getWGP() {
        return this.pluginWorldGuard;
    }
}
