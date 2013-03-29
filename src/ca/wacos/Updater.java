package ca.wacos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Updater {
	public static PluginVersion getVersion() {
		String ver = NametagEdit.plugin.getDescription().getVersion();
		int build = extractSnapshotFile();
		if (build == -1) {
			return new PluginVersion(ver);
		}
		else {
			return new PluginVersion(ver, build);
		}
	}
	private static int extractSnapshotFile() {
		String pluginPath = "plugins/" + NametagEdit.plugin.getPluginFile().getName();
		FileInputStream file = null;
		try {
			file = new FileInputStream(pluginPath);
		} catch (FileNotFoundException e) {
			System.out.println("Could not find \"" + pluginPath + "\".");
			e.printStackTrace();
			return -1;
		}
		try {
			ZipInputStream zis = new ZipInputStream(file);
			ZipEntry entry = zis.getNextEntry();
			while (entry != null) {
				if (entry.getName().equals("snapshot.txt") && !entry.isDirectory()) {
					String buildString;
					int build;
					Scanner read = new Scanner(zis);
					if (read.hasNext())
						read.next();
					else {
						System.out.println("Could not read snapshot.txt from " + pluginPath + ", incorrect formatting!");
						read.close();
						break;
					}
					if (read.hasNext())
						buildString = read.next();
					else {
						System.out.println("Could not read snapshot.txt from " + pluginPath + ", incorrect formatting!");
						read.close();
						break;
					}
					try {
						build = Integer.parseInt(buildString);
					}
					catch (Exception e) {
						System.out.println("Could not read snapshot.txt from " + pluginPath + ", could not parse version number: " + buildString);
						read.close();
						break;
					}
					read.close();

					zis.close();
					
					return build;
				}
				entry = zis.getNextEntry();
			}
			zis.close();
		}
		catch (Exception e) {
			System.out.println("Encountered an error while extracting snapshot file from .jar: ");
			e.printStackTrace();
		}
		return -1;
	}
}