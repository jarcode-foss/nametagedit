package ca.wacos;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import net.minecraft.server.v1_5_R2.ScoreboardTeam;
import net.minecraft.server.v1_5_R2.World;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_5_R2.CraftWorld;

public class ScoreboardManager {
	static List<Integer> list = new ArrayList<Integer>();
	
	@SuppressWarnings("unchecked")
	static void load() {
		World mcWorld = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
		for (String str : (String[]) mcWorld.getScoreboard().getTeamNames().toArray(new String[mcWorld.getScoreboard().getTeamNames().size()])) {
			int entry = -1;
			try {
				entry = Integer.parseInt(str);
			}
			catch (Exception e) {};
			if (entry != -1) {
				list.add(entry);
			}
		}
	}
	
	static void update(String player, String prefix, String suffix) {
		
		World mcWorld = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
		
		if (prefix == null || prefix.isEmpty())
			prefix = getPrefix(player, mcWorld);
		if (suffix == null || suffix.isEmpty())
			suffix = getSuffix(player, mcWorld);
		
		ScoreboardTeam s = get(prefix, suffix);
		
		mcWorld.getScoreboard().addPlayerToTeam(player, s);
		
	}
	static void overlap(String player, String prefix, String suffix) {
		
		World mcWorld = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
		
		if (prefix == null)
			prefix = "";
		if (suffix == null)
			suffix = "";
		
		ScoreboardTeam s = get(prefix, suffix);
		
		mcWorld.getScoreboard().addPlayerToTeam(player, s);
		
	}
	static void clear(String player) {
		
		World mcWorld = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
		
		ScoreboardTeam s = getTeam(player, mcWorld);
		
		if (s != null)
			mcWorld.getScoreboard().removePlayerFromTeam(player, s);
		
	}
	
	@SuppressWarnings("unchecked")
	static String getPrefix(String player, World mcWorld) {
		for (ScoreboardTeam team : (ScoreboardTeam[]) mcWorld.getScoreboard().getTeams().toArray(new ScoreboardTeam[mcWorld.getScoreboard().getTeams().size()])) {
			if (team.getPlayerNameSet().contains(player))
				return team.getPrefix();
		}
		return "";
	}
	@SuppressWarnings("unchecked")
	static String getSuffix(String player, World mcWorld) {
		for (ScoreboardTeam team : (ScoreboardTeam[]) mcWorld.getScoreboard().getTeams().toArray(new ScoreboardTeam[mcWorld.getScoreboard().getTeams().size()])) {
			if (team.getPlayerNameSet().contains(player))
				return team.getSuffix();
		}
		return "";
	}
	static String getSuffix(String player) {
		World mcWorld = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
		return getPrefix(player, mcWorld);
	}
	static String getPrefix(String player) {
		World mcWorld = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
		return getPrefix(player, mcWorld);
	}
	static String getFormattedName(String player) {
		World mcWorld = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
		return getPrefix(player, mcWorld) + player + getSuffix(player, mcWorld);
	}
	@SuppressWarnings("unchecked")
	private static ScoreboardTeam getTeam(String player, World mcWorld) {
		for (ScoreboardTeam team : (ScoreboardTeam[]) mcWorld.getScoreboard().getTeams().toArray(new ScoreboardTeam[mcWorld.getScoreboard().getTeams().size()])) {
			if (team.getPlayerNameSet().contains(player))
				return team;
		}
		return null;
	}
	
	private static ScoreboardTeam declareTeam(World mcWorld, String name, String prefix, String suffix) {
		if (mcWorld.getScoreboard().getTeam(name) != null) {
			mcWorld.getScoreboard().removeTeam(mcWorld.getScoreboard().getTeam(name));
		}
		mcWorld.getScoreboard().createTeam(name);
		mcWorld.getScoreboard().getTeam(name).setPrefix(prefix);
		mcWorld.getScoreboard().getTeam(name).setSuffix(suffix);
		return mcWorld.getScoreboard().getTeam(name);
	}
	
	private static ScoreboardTeam get(String prefix, String suffix) {
		
		World mcWorld = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
		
		update(mcWorld);
		
		for (int t : list.toArray(new Integer[list.size()])) {
			
			if (mcWorld.getScoreboard().getTeam("" + t) != null) {
				ScoreboardTeam s = mcWorld.getScoreboard().getTeam("" + t);
				if (s.getSuffix().equals(suffix) && s.getPrefix().equals(prefix)) {
					return s;
				}
			}
		}
		return declareTeam(mcWorld, nextName() + "", prefix, suffix);
		
	}
	private static int nextName() {
		int at = 0;
		boolean cont = true;
		while (cont) {
			cont = false;
			for (int t : list.toArray(new Integer[list.size()])) {
				if (t == at) {
					at++;
					cont = true;
				}
					
			}
		}
		list.add(at);
		return at;
	}
	@SuppressWarnings("unchecked")
	private static void update(World mcWorld) {

		for (ScoreboardTeam team : (ScoreboardTeam[]) mcWorld.getScoreboard().getTeams().toArray(new ScoreboardTeam[mcWorld.getScoreboard().getTeams().size()])) {
			int entry = -1;
			try {
				entry = Integer.parseInt(team.getName());
			}
			catch (Exception e) {};
			if (entry != -1) {
				if (team.getPlayerNameSet().size() == 0) {
					mcWorld.getScoreboard().removeTeam(team);
					list.remove(new Integer(entry));
				}
			}
		}
	}
}
