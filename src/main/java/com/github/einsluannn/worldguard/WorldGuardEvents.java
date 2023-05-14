package com.github.einsluannn.worldguard;

import com.github.einsluannn.worldguard.entry.Entry;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class WorldGuardEvents extends JavaPlugin {

    static RegionContainer container;

    @Override
    public void onEnable() {
        // Plugin startup logic

        Logger logger = Bukkit.getLogger();
        PluginManager pluginManager = Bukkit.getPluginManager();

        Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");

        if(plugin == null) {
            logger.severe("[Varilx-WorldGuardEvents] WorldGuard.jar wurde nicht gefunden!");
            pluginManager.disablePlugin(this);
            return;
        }

        String version = WorldGuard.getVersion();
        if(version.isEmpty()) {
            logger.severe("[Varilx-WorldGuardEvents] Die Version von WorldGuard wird nicht erkannt. Sind Sie sicher, dass es richtig installiert ist?");

            pluginManager.disablePlugin(this);
            return;
        }

        if (!version.startsWith("7.")) {
            logger.warning("[WorldGuardEvents] Detected WorldGuard version \"" + version + "\".");
            logger.warning("[WorldGuardEvents] This plugin is meant to work with WorldGuard version \"7.0.0\" or higher,");
            logger.warning("[WorldGuardEvents] and may not work properly with any other major revision.");
            logger.warning("[WorldGuardEvents] Please update WorldGuard if your version is below \"7.0.0\" or wait for");
            logger.warning("[WorldGuardEvents] an update of WorldGuardEvents to support WorldGuard "+version+".");
        }

        if (!WorldGuard.getInstance().getPlatform().getSessionManager().registerHandler(Entry.factory, null)) {
            logger.severe("[WorldGuardEvents] Could not register the entry handler !");
            logger.severe("[WorldGuardEvents] Please report this error. The plugin will now be disabled.");

            pluginManager.disablePlugin(this);
            return;
        }

        container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        container = null;
    }

    @Nonnull
    public static Set<ProtectedRegion> getRegions(UUID playerUUID) {
        Player player  = Bukkit.getPlayer(playerUUID);
        if(player == null || !player.isOnline())
            return Collections.emptySet();

        RegionQuery regionQuery = container.createQuery();
        ApplicableRegionSet applicableRegionSet = regionQuery.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));
        return applicableRegionSet.getRegions();
    }

    @Nonnull
    public static Set<String> getRegionsNames(UUID playerUUID) {
        return getRegions(playerUUID).stream().map(ProtectedRegion::getId).collect(Collectors.toSet());
    }

    public static boolean isPlayerInAllRegions(UUID playerUUID, Set<String> regionNames) {
        Set<String> regions = getRegionsNames(playerUUID);
        if(regionNames.isEmpty()) throw new IllegalArgumentException("You need to check for at least one region !");

        return regions.containsAll(regionNames.stream().map(String::toLowerCase).collect(Collectors.toSet()));
    }

    public static boolean isPlayerInAnyRegion(UUID playerUUID, Set<String> regionNames) {
        Set<String> regions = getRegionsNames(playerUUID);
        if(regionNames.isEmpty()) throw new IllegalArgumentException("You need to check for at least one region !");
        for(String region : regionNames)
        {
            if(regions.contains(region.toLowerCase()))
                return true;
        }
        return false;
    }

    public static boolean isPlayerInAnyRegion(UUID playerUUID, String... regionName) {
        return isPlayerInAnyRegion(playerUUID, new HashSet<>(Arrays.asList(regionName)));
    }

    public static boolean isPlayerInAllRegions(UUID playerUUID, String... regionName) {
        return isPlayerInAllRegions(playerUUID, new HashSet<>(Arrays.asList(regionName)));
    }

}
