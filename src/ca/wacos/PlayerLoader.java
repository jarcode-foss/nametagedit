package ca.wacos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;

import org.bukkit.plugin.java.JavaPlugin;

public class PlayerLoader {
	
	private static final String PREFIX = "[NAMETAG CONFIG] ";
	private static String path = null;
	
	public static LinkedHashMap<String, LinkedHashMap<String, String>> load(JavaPlugin plugin) {
		String folder = "plugins/" + plugin.getName();
		File folderFile = new File(folder);
		if (!folderFile.exists()) {
			folderFile.mkdir();
		}
		path = "plugins/" + plugin.getName() + "/players.txt";
		File source = new File(path);
		if (source.exists()) {
			return loadConfig();
		}
		else {
			try {
				source.createNewFile();
			} catch (IOException e) {
				print("Failed to create config file: ");
				e.printStackTrace();
			}
			return generateConfig(source, plugin);
		}
	}
	public static void addPlayer(String name, String operation, String value) {
		ArrayList<String> buffer = new ArrayList<String>();
		File file = new File(path);
		Scanner in = null;
		PrintWriter out = null;
		value = value.replace("§", "&");
		try {
			in = new Scanner(new File(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while(in.hasNext()) {
			buffer.add(in.nextLine());
		}
		in.close();
		try {
			out = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (String line : buffer.toArray(new String[buffer.size()])) {
			out.println(line);
		}
		out.println(name + " " + operation + " = \"" + value + "\"");
		out.close();
		
	}
	public static void update(String name, String prefix, String suffix) {
		LinkedHashMap<String, String> player = getPlayer(name);
		prefix = prefix.replace("§", "&");
		suffix = suffix.replace("§", "&");
		removePlayer(name, null);
		if (prefix != null && !prefix.isEmpty())
			addPlayer(name, "prefix", prefix);
		else if (player != null) {
			if (player.get("prefix") != null)
				addPlayer(name, "prefix", player.get("prefix"));
		}
		if (suffix != null && !suffix.isEmpty())
			addPlayer(name, "suffix", suffix);
		else if (player != null) {
			if (player.get("suffix") != null)
				addPlayer(name, "suffix", player.get("suffix"));
		}
	}
	public static void removePlayer(String name, String operation) {
		ArrayList<String> buffer = new ArrayList<String>();
		File file = new File(path);
		Scanner in = null;
		PrintWriter out = null;
		try {
			in = new Scanner(new File(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while(in.hasNext()) {
			buffer.add(in.nextLine());
		}
		in.close();
		try {
			out = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (String line : buffer.toArray(new String[buffer.size()])) {
			Scanner lineScanner = new Scanner(line);
			
			String lName = lineScanner.next();
			String lOperation = lineScanner.next();
			
			lineScanner.close();
			
			boolean skip = false;
			
			if (name.equals(lName)) {
				if (operation != null && operation.equals(lOperation))
					skip = true;
				else if (operation == null)
					skip = true;
			}
			if (!skip)
				out.println(line);
		}
		out.close();
	}
	public static LinkedHashMap<String, String> getPlayer(String name) {
		LinkedHashMap<String, LinkedHashMap<String, String>> playerMap = loadConfig();
		for (String key : playerMap.keySet().toArray(new String[playerMap.keySet().size()])) {
			if (key.equals(name)) {
				return playerMap.get(key);
			}
		}
		return null;
	}
	private static LinkedHashMap<String, LinkedHashMap<String, String>> generateConfig(File target, JavaPlugin plugin) {
		PrintWriter out = null;
		try {
			out = new PrintWriter(target);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		out.println("// This file declares any user specific prefixes and suffixes set with the /ne command.");
		out.println("Jarcode prefix = \"[&bCreator&f] \"");
		out.println("Notch prefix = \"&b< &a\"");
		out.println("Notch suffix = \" &b>\"");
		
		out.close();
		
		return loadConfig();
	}
	private static LinkedHashMap<String, LinkedHashMap<String, String>> loadConfig() {
		File source = new File(path);
		Scanner in = null;
		try {
			in = new Scanner(source);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		LinkedHashMap<String, LinkedHashMap<String, String>> map = new LinkedHashMap<String, LinkedHashMap<String, String>>();

		
		boolean syntaxError = false;
		
		while (in.hasNext()) {
			String line = in.nextLine();
			if (!line.trim().startsWith("//")) {
				
				syntaxError = checkWords(line);
				if (syntaxError) {
					print("Error in syntax, not enough elements on line!");
				}
				
				Scanner lineScanner = new Scanner(line);
				
				String node = lineScanner.next();
				String operation = lineScanner.next();
				String equals = lineScanner.next();
				if (!equals.trim().equals("=")) {
					print("Error in syntax, \"=\" expected at third element, \"" + equals + "\" given.");
					syntaxError = true;
					break;
				}
				String rawValue = lineScanner.nextLine();
				syntaxError = checkValue(rawValue);
				if (syntaxError) {
					print("Error in syntax, value not encased in quotation marks!");
					break;
				}
				String value = getValue(rawValue);
				
				LinkedHashMap<String, String> entry = new LinkedHashMap<String,String>();
				
				if (map.get(node) != null) {
					entry = map.get(node);
				}
				
				entry.put(operation.toLowerCase(), value);
				
				if (map.get(node) == null) {
					map.put(node, entry);
				}
				lineScanner.close();
			}
		}
		in.close();
		
		if (syntaxError)
			return null;
		return map;
	}
	private static void print(String p) {
		System.out.println(PREFIX + p);
	}
	private static void printDebug(String p) {
		if (ConfigLoader.DEBUG)
			System.out.println(PREFIX + p);
	}
	private static boolean checkWords(String line) {
		int count = 0;
		Scanner reader = new Scanner(line);
		while (reader.hasNext()) {
			count++;
			reader.next();
		}
		reader.close();
		if (count >= 4)
			return false;
		else return true;
	}
	private static boolean checkValue(String rawValue) {
		rawValue = rawValue.trim();
		if (!rawValue.startsWith("\""))
			return true;
		if (!rawValue.endsWith("\""))
			return true;
		return false;
		
	}
	private static String getValue(String rawValue) {
		rawValue = rawValue.trim();
		String f1 = "";
		String f2 = "";
		for (int t = 1; t < rawValue.length() - 1; t++) {
			f1 += rawValue.charAt(t);
		}
		for (int t = 0; t < f1.length() && t < 16; t++) {
			f2 += f1.charAt(t);
		}
		return f2;
	}
}
