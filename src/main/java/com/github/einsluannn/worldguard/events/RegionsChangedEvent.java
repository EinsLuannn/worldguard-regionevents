package com.github.einsluannn.worldguard.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RegionsChangedEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled = false;

    private final UUID uuid;
    private final Set<ProtectedRegion> previousRegions = new HashSet<>();
    private final Set<ProtectedRegion> currentRegions = new HashSet<>();
    private final Set<String> previousRegionsNames = new HashSet<>();
    private final Set<String> currentRegionsNames = new HashSet<>();

    public RegionsChangedEvent(UUID playerUUID, @NotNull Set<ProtectedRegion> previous, @NotNull Set<ProtectedRegion> current) {
        this.uuid = playerUUID;
        previousRegions.addAll(previous);
        currentRegions.addAll(current);

        for(ProtectedRegion region : current) {
            currentRegionsNames.add(region.getId());
        }

        for(ProtectedRegion region : previous) {
            previousRegionsNames.add(region.getId());
        }

    }

    @Contract(pure = true)
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public UUID getUUID() {
        return uuid;
    }

    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    @NotNull
    public Set<String> getCurrentRegionsNames() {
        return currentRegionsNames;
    }

    @NotNull
    public Set<String> getPreviousRegionsNames() {
        return previousRegionsNames;
    }

    @NotNull
    public Set<ProtectedRegion> getCurrentRegions() {
        return currentRegions;
    }

    @NotNull
    public Set<ProtectedRegion> getPreviousRegions() {
        return previousRegions;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled=cancelled;
    }

}
