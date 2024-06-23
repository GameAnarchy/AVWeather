package com.maplecoding.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import de.netzkronehd.wgregionevents.api.SimpleWorldGuardAPI;
import de.netzkronehd.wgregionevents.events.RegionEnterEvent;
import de.netzkronehd.wgregionevents.events.RegionLeaveEvent;

public class WorldGuardListener implements Listener {

    private final SimpleWorldGuardAPI swg = new SimpleWorldGuardAPI();

    // Player joins server
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {

        // Get All regions player is in
        Player p = e.getPlayer();
        ApplicableRegionSet regions = swg.getRegions(p.getLocation());

        // Get current region & its priority
        ProtectedRegion currentRegion = null;
        int currentPriority = 0;
        if (regions.size() != 0) {
            for (ProtectedRegion region : regions) {
                if (region.getPriority() >= currentPriority) {
                    currentPriority = region.getPriority();
                    currentRegion = region;
                }
            }
        }

        p.sendMessage("You are in region " + ChatColor.GOLD + currentRegion + ChatColor.WHITE + " with priority "
                + ChatColor.GOLD + currentPriority);

        // TO-DO: Handle Weather based on current region and apply it to player

    }

    // Player leaves a region
    @EventHandler
    public void onRegionLeave(RegionLeaveEvent e) {
        // Get Region Priority
        Player p = e.getPlayer();
        ProtectedRegion region = e.getRegion();
        int priority = region.getPriority();

        p.sendMessage("You left region " + ChatColor.GOLD + region + ChatColor.WHITE + " with priority "
                + ChatColor.GOLD + priority);

        // TO-DO: Handle Weather based on current region and apply it to player
    }

    // Player enters a region
    @EventHandler
    public void onRegionEnter(RegionEnterEvent e) {

        // Get Region Priority
        Player p = e.getPlayer();
        ProtectedRegion region = e.getRegion();
        int priority = region.getPriority();

        p.sendMessage("You entered region " + ChatColor.GOLD + region + ChatColor.WHITE + " with priority "
                + ChatColor.GOLD + priority);

        // TO-DO: Handle Weather based on current region and apply it to player
    }
}
