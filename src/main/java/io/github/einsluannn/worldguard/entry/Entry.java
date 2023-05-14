package io.github.einsluannn.worldguard.entry;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;
import io.github.einsluannn.worldguard.events.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.util.Set;

public class Entry extends Handler implements Listener {

    public final PluginManager pluginManager = Bukkit.getPluginManager();
    public static final Factory factory = new Factory();

    public static class Factory extends Handler.Factory<Entry> {
        @Override
        public Entry create(Session session) {
            return new Entry(session);
        }
    }

    public Entry(Session session) {
        super(session);
    }


    @Override
    public boolean onCrossBoundary(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> left, MoveType moveType) {
        RegionsChangedEvent regionsChangedEvent = new RegionsChangedEvent(player.getUniqueId(), left, entered);
        pluginManager.callEvent(regionsChangedEvent);
        if(regionsChangedEvent.isCancelled()) return false;

        RegionsEnteredEvent regionsEnteredEvent = new RegionsEnteredEvent(player.getUniqueId(), entered);
        pluginManager.callEvent(regionsChangedEvent);
        if(regionsChangedEvent.isCancelled()) return false;

        RegionsLeftEvent regionsLeftEvent  = new RegionsLeftEvent(player.getUniqueId(), left);
        pluginManager.callEvent(regionsLeftEvent);
        if(regionsLeftEvent.isCancelled()) return false;

        for(ProtectedRegion region : entered) {
            RegionEnteredEvent regionEntered = new RegionEnteredEvent(player.getUniqueId(), region);
            pluginManager.callEvent(regionEntered);
            if(regionEntered.isCancelled()) return false;
        }

        for(ProtectedRegion region : left) {
            RegionLeftEvent regionLeftEvent = new RegionLeftEvent(player.getUniqueId(), region);
            pluginManager.callEvent(regionLeftEvent);
            if(regionLeftEvent.isCancelled()) return false;
        }
        return true;
    }
}
