package ca.wacos;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Written by Levi Webb
 * <p/>
 * Date: 03/05/13
 * Time: 5:05 PM
 */
public class NametagChangeEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private String player;
    private String oldPrefix;
    private String oldSuffix;
    private String newPrefix;
    private String newSuffix;

    private NametagChangeType type;
    private NametagChangeReason reason;

    private boolean cancelled = false;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public NametagChangeEvent(String player, String oldPrefix, String oldSuffix, String newPrefix, String newSuffix, NametagChangeType type, NametagChangeReason reason) {
        this.player = player;
        this.oldPrefix = oldPrefix;
        this.oldSuffix = oldSuffix;
        this.newPrefix = newPrefix;
        this.newSuffix = newSuffix;
        this.type = type;
    }
    public String getPlayerName() {
        return player;
    }
    public String getCurrentPrefix() {
        return oldPrefix;
    }
    public String getCurrentSuffix() {
        return oldSuffix;
    }
    public String getPrefix() {
        return newPrefix;
    }
    public String getSuffix() {
        return newSuffix;
    }
    public NametagChangeType getType() {
        return type;
    }
    public NametagChangeReason getReason() {
        return reason;
    }
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    public boolean isCancelled() {
        return cancelled;
    }
    public enum NametagChangeType {
        HARD, SOFT;
    }
    public enum NametagChangeReason {
        RESET, SET_PREFIX, SET_SUFFIX, CUSTOM
    }
}
