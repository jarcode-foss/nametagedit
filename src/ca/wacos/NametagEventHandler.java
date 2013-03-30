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

/**
 * This class is responsible for handling various events in the server.
 * 
 * @author Levi Webb
 *
 */
public class NametagEventHandler implements Listener {

	private HashMap<String, Location> backs = new HashMap<String, Location>();
	
	/**
	 * Called when a player joins the server. This event is set to <i>HIGHEST</i> priority to address a conflict
	 * created with plugins that read player information in this event.<br><br>
	 * 
	 * This event updates nametag information, and the tab list (if enabled). This method also restores
	 * information modified by the {@link #onPlayerLogin(PlayerLoginEvent)} event so that this plugin,
	 * along with any others, can read player information as it was when the player last left the server.
	 * The data is restored by grabbing location information from a {@link HashMap} that was put into it originally
	 * by the login event, and setting the player location with it.
	 * 
	 * @param e  the {@link PlayerJoinEvent} associated with this listener.
	 * @see #onPlayerLogin(PlayerLoginEvent)
	 */
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
	
	/**
	 * Called when a player makes an attempt to log into the server.<br><br>
	 * 
	 * This event makes an edit to the <b>player .dat file</b> in the "players" folder in the main world save,
	 * editing the player's location to be in the main world to properly assign nametag prefixes and suffixes
	 * when the player joins the server.<br><br>
	 * 
	 * The original player location is stored in a {@link HashMap} as a {@link Location}, then is restored and
	 * removed when the player joins into the server.
	 * 
	 * @param e  the {@link PlayerLoginEvent} associated with this listener.
	 * @see #onPlayerJoin(PlayerJoinEvent)
	 */
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
	/**
	 * Called when a player dies in the server. If enabled, this plugin will parse through the death message
	 * and remove any formatting created by the player's nametag.
	 * 
	 * @param e  the {@link PlayerDeathEvent} associated with this listener.
	 */
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (NametagEdit.deathMessageEnabled) {
			String formattedName = ScoreboardManager.getFormattedName(e.getEntity().getName());
			if (!formattedName.equals(e.getEntity().getName()))
				e.setDeathMessage(e.getDeathMessage().replace(formattedName, e.getEntity().getName()));
		}
	}
}
