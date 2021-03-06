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
import org.bukkit.GameMode;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 * @author BangL <henno.rickowski@googlemail.com>
 */
public class Utils {

    public static WGCustomFlagsPlugin getWGCustomFlags(WGTreeFarmFlagPlugin plugin) {
        final Plugin wgcf = plugin.getServer().getPluginManager().getPlugin("WGCustomFlags");
        if (wgcf == null || !(wgcf instanceof WGCustomFlagsPlugin)) {
            return null;
        }
        return (WGCustomFlagsPlugin)wgcf;
    }

    public static WorldGuardPlugin getWorldGuard(WGTreeFarmFlagPlugin plugin) {
        final Plugin wg = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
        if (wg == null || !(wg instanceof WorldGuardPlugin)) {
            return null;
        }
        return (WorldGuardPlugin)wg;
    }

    public static void damageItemInHand(Player player) {
        if (player.hasMetadata("NPC") || player instanceof NPC || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        ItemStack result = new ItemStack(player.getItemInHand());
        short dur = result.getDurability();
        short max = 0;
        switch (result.getType()) {
            case GOLD_AXE:
            case GOLD_HOE:
            case GOLD_SPADE:
            case GOLD_PICKAXE:
            case GOLD_SWORD:
                max = 33;
                dur = Short.valueOf(String.valueOf(dur + 1));
                break;
            case WOOD_AXE:
            case WOOD_HOE:
            case WOOD_SPADE:
            case WOOD_PICKAXE:
            case WOOD_SWORD:
                max = 60;
                dur = Short.valueOf(String.valueOf(dur + 1));
                break;
            case FISHING_ROD:
                max = 65;
                dur = Short.valueOf(String.valueOf(dur + 1));
                break;
            case STONE_AXE:
            case STONE_HOE:
            case STONE_SPADE:
            case STONE_PICKAXE:
            case STONE_SWORD:
                max = 132;
                dur = Short.valueOf(String.valueOf(dur + 1));
                break;
            case SHEARS:
                max = 238;
                dur = Short.valueOf(String.valueOf(dur + 1));
                break;
            case IRON_AXE:
            case IRON_HOE:
            case IRON_SPADE:
            case IRON_PICKAXE:
            case IRON_SWORD:
                max = 251;
                dur = Short.valueOf(String.valueOf(dur + 1));
                break;
            case BOW:
                max = 385;
                dur = Short.valueOf(String.valueOf(dur + 1));
                break;
            case DIAMOND_AXE:
            case DIAMOND_HOE:
            case DIAMOND_SPADE:
            case DIAMOND_PICKAXE:
            case DIAMOND_SWORD:
                max = 1562;
                dur = Short.valueOf(String.valueOf(dur + 1));
                break;
            default:
                max = 0;
                break;
        }
        if (max > 0 ) {
            if (dur >= max) {
                result = null;
            } else {
                result.setDurability(dur);
            }
            player.setItemInHand(result);
        }
    }

    public static void loadConfig(WGTreeFarmFlagPlugin plugin) {
        plugin.getConfig().addDefault("settings.mcmmo-leveling", false);
        plugin.getConfig().addDefault("settings.qwick-tree-chopping", false);
        plugin.getConfig().addDefault("settings.apple-chance", 5);
        plugin.getConfig().addDefault("settings.allow-shears", false);
        plugin.getConfig().addDefault("messages.block.saplingdestroy", "Let them grow!");
        plugin.getConfig().addDefault("messages.block.blockplace", "This is a treefarm. You can't build here.");
        plugin.getConfig().addDefault("messages.block.blockdestroy", "This is a treefarm. You can't destroy this here.");
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();
    }
}
