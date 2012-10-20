package de.bangl.wgtff;

import com.mewin.WGCustomFlags.WGCustomFlagsPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author BangL
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

    public static void loadConfig(WGTreeFarmFlagPlugin plugin) {
        plugin.getConfig().addDefault("messages.block.saplingdestroy", "Let them grow!");
        plugin.getConfig().addDefault("messages.block.blockplace", "This is a treefarm. You can't build here.");
        plugin.getConfig().addDefault("messages.block.blockdestroy", "This is a treefarm. You can't destroy this here.");
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();
    }
}