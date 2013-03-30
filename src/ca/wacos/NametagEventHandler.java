package ca.wacos;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class NametagEventHandler implements Listener {

	HashMap<String, Location> backs = new HashMap<String, Location>();
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerJoin(final PlayerJoinEvent e) {
		
		ScoreboardManager.clear(e.getPlayer().getName());
		
		boolean back = false;
		
		for (String name : backs.keySet().toArray(new String[backs.keySet().size()])) {
			if (name.equals(e.getPlayer().getName())) {
				back = true;
				break;
			}
		}
		
		boolean setGroup = true;
		
		LinkedHashMap<String, String> playerData = PlayerLoader.getPlayer(e.getPlayer().getName());
		if (playerData != null) {
			String prefix = playerData.get("prefix");
			String suffix = playerData.get("suffix");
			if (prefix != null)
				prefix = NametagUtils.formatColors(prefix);
			if (suffix != null)
				suffix = NametagUtils.formatColors(suffix);
			if (GroupLoader.DEBUG) {
				System.out.println("Setting prefix/suffix for " + e.getPlayer().getName() + ": " + prefix + ", " + suffix + " (user)");
			}
			ScoreboardManager.overlap(e.getPlayer().getName(), prefix, suffix);
			setGroup = false;
		}
		
		if (setGroup) {
			for (String key : NametagEdit.groups.keySet().toArray(new String[NametagEdit.groups.keySet().size()])) {
				if (e.getPlayer().hasPermission(key)) {
					String prefix = NametagEdit.groups.get(key).get("prefix");
					String suffix = NametagEdit.groups.get(key).get("suffix");
					if (prefix != null)
						prefix = NametagUtils.formatColors(prefix);
					if (suffix != null)
						suffix = NametagUtils.formatColors(suffix);
					if (GroupLoader.DEBUG) {
						System.out.println("Setting prefix/suffix for " + e.getPlayer().getName() + ": " + prefix + ", " + suffix + " (node)");
					}
					ScoreboardManager.overlap(e.getPlayer().getName(), prefix, suffix);
					
					break;
				}
			}
		}

		
		if (back) {
			e.getPlayer().teleport(backs.get(e.getPlayer().getName()));
			while (backs.remove(e.getPlayer().getName()) != null) {}
		}
		if (NametagEdit.tabListEnabled) {
			String str = "§f" + e.getPlayer().getName();
			String tab = "";
			for (int t = 0; t < str.length() && t < 16; t++)
				tab += str.charAt(t);
			e.getPlayer().setPlayerListName(tab);
		}
		
		if (e.getPlayer().isOp()) {
			Updater.checkForUpdates(e.getPlayer());
		}
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e) {
		Location l = NametagUtils.getOfflineLoc(e.getPlayer().getName());
		if (l != null && l.getWorld() != Bukkit.getWorlds().get(0)) {

			NametagUtils.setOfflineLoc(e.getPlayer().getName(), Bukkit.getWorlds().get(0).getSpawnLocation());
			
			if (GroupLoader.DEBUG) {
				System.out.println("Transfering player to main world temporarily to set nametags...");
			}
			backs.put(e.getPlayer().getName(), l);
		}
	}
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (NametagEdit.deathMessageEnabled) {
			String formattedName = ScoreboardManager.getFormattedName(e.getEntity().getName());
			if (!formattedName.equals(e.getEntity().getName()))
				e.setDeathMessage(e.getDeathMessage().replace(formattedName, e.getEntity().getName()));
		}
	}
}
