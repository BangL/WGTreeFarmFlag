/*
 * Copyright (C) 2012-2013 BangL <henno.rickowski@googlemail.com>
 *                         mewin <mewin001@hotmail.de>
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
package de.bangl.wgtff.listeners;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import de.bangl.wgtff.Utils;
import de.bangl.wgtff.WGTreeFarmFlagPlugin;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author BangL <henno.rickowski@googlemail.com>
 * @author mewin <mewin001@hotmail.de>
 */
public class BlockListener implements Listener {
    private WGTreeFarmFlagPlugin plugin;

    public BlockListener(WGTreeFarmFlagPlugin plugin) {
        this.plugin = plugin;

        // Register events
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        
        final WorldGuardPlugin wgp = plugin.getWGP();
        final Block block = event.getBlock();
        final Player player = event.getPlayer();
        
        //lets block restricter handle it
        if (!plugin.hasBlockRestricter()
                && !wgp.getRegionManager(block.getWorld()).getApplicableRegions(block.getLocation()).allows(plugin.FLAG_TREEFARM)) {
            // treefarm is set to "deny"
            // so let's cancel this placement
            // an op/member/owner can still build, if treefarm is set to "allow".
            final String msg = this.plugin.getConfig().getString("messages.block.blockplace");
            player.sendMessage(ChatColor.RED + msg);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        final WorldGuardPlugin wgp = plugin.getWGP();
        final Location loc = event.getBlock().getLocation();
        final Block block = event.getBlock();
        final World world = block.getWorld();
        final Player player = event.getPlayer();
        final Material material = block.getType();
        final State state = wgp.getRegionManager(world).getApplicableRegions(loc).getFlag(plugin.FLAG_TREEFARM);

        // handle if ((allowed treefarm region
        // and player is not op
        // and can not build)
        // or denied treefarm region)
        if (state != null
                && (state == State.DENY
                || (!player.isOp()
                && !wgp.canBuild(player, block)))) {

            if (material == Material.LOG
                    || material == Material.LOG_2) {
                // --- Log destroyed

                byte data = block.getData();
                BlockState blockState = block.getState();

                // Support for new wood types of MC1.7
                // The new wood types would have 0 and 1 as data value,
                // but their sapling data values are 4 and 5 instead.
                // so simply increase by 4 if its one of the new wood types
                if (material == Material.LOG_2) {
                    data += 4;
                }

                // if player is not an npc and not in creative mode...
                if (!player.hasMetadata("NPC") && !(player instanceof NPC) && player.getGameMode() != GameMode.CREATIVE) {

                    ItemStack heldItem = player.getItemInHand();

                    // Drop Log based on the item in hand
                    if (heldItem == null) {
                        block.breakNaturally();
                    } else {
                        block.breakNaturally(heldItem);
                    }

                    // Add damage to the item in hand
                    Utils.damageItemInHand(player);

                    // mcMMO support
                    if (plugin.hasMcMMO()
                            && plugin.getConfig().getBoolean("settings.mcmmo-leveling")
                            && com.gmail.nossr50.util.Permissions.skillEnabled(player,
                            com.gmail.nossr50.datatypes.skills.SkillType.WOODCUTTING)) {

                        com.gmail.nossr50.skills.woodcutting.WoodcuttingManager woodcuttingManager
                                = com.gmail.nossr50.util.player.UserManager.getPlayer(player).getWoodcuttingManager();

                        if (woodcuttingManager.canUseTreeFeller(heldItem)) {
                            woodcuttingManager.processTreeFeller(blockState);
                        } else {
                            woodcuttingManager.woodcuttingBlockCheck(blockState);
                        }
                    }
                }

                // was this a tree-base?
                final Location locUnder = event.getBlock().getLocation();
                locUnder.setY(block.getY() - 1.0D);
                if ((locUnder.getBlock().getType() == Material.DIRT)
                        || (locUnder.getBlock().getType() == Material.GRASS)) {
                    // Turn log to sapling
                    block.setTypeIdAndData(Material.SAPLING.getId(), data, false);
                } else {
                    // Turn log to air
                    block.setType(Material.AIR);
                }
            } else if (material == Material.LEAVES
                    || material == Material.LEAVES_2) {
                // --- Leaf destroyed
                block.setType(Material.AIR);
                Utils.damageItemInHand(player);
            } else if (material == Material.SAPLING) {
                // --- Sapling destroyed.
                final String msg = this.plugin.getConfig().getString("messages.block.saplingdestroy");
                player.sendMessage(ChatColor.RED + msg);
            } else if (!plugin.hasBlockRestricter()
                    || !com.mewin.WGBlockRestricter.Utils.blockAllowedAtLocation(wgp, material, loc)) {
                // --- Any other block destroyed
                final String msg = this.plugin.getConfig().getString("messages.block.blockdestroy");
                player.sendMessage(ChatColor.RED + msg);
            } else {
                return;
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLeaveDecay(LeavesDecayEvent event) {
        
        final Block block = event.getBlock();
        final Location loc = block.getLocation();
        final World world = block.getWorld();

        // Cancel if treefarm region
        if (plugin.getWGP().getRegionManager(world).getApplicableRegions(loc).getFlag(plugin.FLAG_TREEFARM) != null) {
            // turn leave to air
            block.setType(Material.AIR);
            event.setCancelled(true);
        }
    }
}
