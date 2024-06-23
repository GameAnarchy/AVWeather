package com.maplecoding.Utilities;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

public class WorldGuardIntegration {

    @SuppressWarnings("null")
    public Map<String, ProtectedRegion> getRegions(Player p) {
        Location loc = p.getLocation();
        World world = loc.getWorld();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

        if (world != null) {
            RegionManager regions = container.get(BukkitAdapter.adapt(world));
            return regions.getRegions();
        }
        return null;
    }

    /*
     * public int getRegionPriority(Player p) {
     * 
     * Map<String, ProtectedRegion> regions = getRegions(p);
     * 
     * if (regions != null && !regions.isEmpty()) {
     * ProtectedRegion currentRegion = null;
     * int currentPriority = 0;
     * for (ProtectedRegion region : regions.values()) {
     * if (region.contains(p.getLocation().getBlockX(), p.getLocation().getBlockY(),
     * p.getLocation().getBlockZ())) {
     * 
     * }
     * }
     * }
     * 
     * }
     */

    public boolean isShelteredTop(Player p, int blockSize, int maxHeight) {
        Location loc = p.getLocation();
        World world = loc.getWorld();
        int halfSize = blockSize / 2;
        // Set world maxheight is value is -1
        maxHeight = maxHeight == -1 ? world.getMaxHeight() : maxHeight;

        // Adjust for even sizes to center around the player
        int startX = loc.getBlockX() - halfSize;
        int startZ = loc.getBlockZ() - halfSize;
        int endX = loc.getBlockX() + halfSize;
        int endZ = loc.getBlockZ() + halfSize;

        // Check if there is a block above the player
        // TO-DO: Configurable max height check
        if (blockSize <= 1) {
            for (int y = loc.getBlockY() + 1; y <= maxHeight; y++) {
                if (!world.getBlockAt(loc.getBlockX(), y, loc.getBlockZ()).isPassable()) {
                    return true;
                }
            }
        } else {
            boolean shelterCheck = false;
            for (int y = loc.getBlockY() + 1; y <= maxHeight; y++) {
                shelterCheck = false; // Reset everytime we check the next y
                for (int x = startX; x <= endX; x++) {
                    for (int z = startZ; x <= endZ; z++) {
                        if (!world.getBlockAt(x, y, z).isPassable()) {
                            shelterCheck = true;
                        } else {
                            shelterCheck = false;
                        }
                    }
                }
                // After we checked the entire range at current y, if sheltercheck is still
                // true, player is sheltered
                if (shelterCheck == true) {
                    return true;
                }
            }
        }
        // Player is not sheltered after checking all y
        return false;
    }
}
