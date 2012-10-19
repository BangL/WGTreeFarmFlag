package de.bangl.wgtff;

import com.mewin.WGCustomFlags.WGCustomFlagsPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import de.bangl.wgtff.listeners.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author BangL
 */
public class WGTreeFarmFlagPlugin extends JavaPlugin {

    // Plugins
    private WGCustomFlagsPlugin pluginWGCustomFlags;
    private WorldGuardPlugin pluginWorldGuard;

    // Listeners
    private PlayerListener listenerPlayer;

    public WGCustomFlagsPlugin getWGCFP() {
        return pluginWGCustomFlags;
    }

    public WorldGuardPlugin getWGP() {
        return pluginWorldGuard;
    }

    @Override
    public void onEnable() {

        // Load config
        Utils.loadConfig(this);

        // Init WorldGuard
        this.pluginWorldGuard = Utils.getWorldGuard(this);

        // Init and register custom flags
        this.pluginWGCustomFlags = Utils.getWGCustomFlags(this);

        // Register all listeners
        this.listenerPlayer = new PlayerListener(this);
        
    }

    @Override
    public void onDisable() {

        // we nullify all vars, cause it could be a server reload and we don't wanna leave trash in our expensive RAM.
        this.pluginWGCustomFlags = null;
        this.pluginWorldGuard = null;
        this.listenerPlayer = null;

        saveConfig();
    }

}
