package ca.wacos;

import org.bukkit.Bukkit;

import ca.wacos.NametagChangeEvent.NametagChangeType;
import ca.wacos.NametagChangeEvent.NametagChangeReason;

/**
 * Written by Levi Webb
 * <p/>
 * Date: 03/05/13
 * Time: 4:50 PM
 */
public class NametagAPI {
    public static void setPrefix(String player, String prefix) {
        NametagChangeEvent e = new NametagChangeEvent(player, getPrefix(player), getSuffix(player), prefix, null, NametagChangeType.SOFT, NametagChangeReason.CUSTOM);
        Bukkit.getServer().getPluginManager().callEvent(e);
        if (!e.isCancelled())
            NametagManager.update(player, prefix, null);
    }
    public static void setSuffix(String player, String suffix) {
        NametagChangeEvent e = new NametagChangeEvent(player, getPrefix(player), getSuffix(player), null, suffix, NametagChangeType.SOFT, NametagChangeReason.CUSTOM);
        Bukkit.getServer().getPluginManager().callEvent(e);
        if (!e.isCancelled())
            NametagManager.update(player, null, suffix);
    }
    public static void setNametag(String player, String prefix, String suffix) {
        setNametagHard(player, prefix, suffix);
    }
    public static void setNametagHard(String player, String prefix, String suffix) {
        NametagChangeEvent e = new NametagChangeEvent(player, getPrefix(player), getSuffix(player), prefix, suffix, NametagChangeType.HARD, NametagChangeReason.CUSTOM);
        Bukkit.getServer().getPluginManager().callEvent(e);
        if (!e.isCancelled())
            NametagManager.overlap(player, prefix, suffix);
    }
    public static void setNametagSoft(String player, String prefix, String suffix) {
        NametagChangeEvent e = new NametagChangeEvent(player, getPrefix(player), getSuffix(player), prefix, suffix, NametagChangeType.SOFT, NametagChangeReason.CUSTOM);
        Bukkit.getServer().getPluginManager().callEvent(e);
        if (!e.isCancelled())
            NametagManager.update(player, prefix, suffix);
    }
    public static String getPrefix(String player) {
        return NametagManager.getPrefix(player);
    }
    public static String getSuffix(String player) {
        return NametagManager.getSuffix(player);
    }
    public static String getNametag(String player) {
        return NametagManager.getFormattedName(player);
    }
    public static String getVersion() {
        return NametagEdit.plugin.getDescription().getVersion();
    }
}
