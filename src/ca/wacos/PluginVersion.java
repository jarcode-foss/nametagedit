package ca.wacos;

public class PluginVersion {
	private boolean snapshot;
	private int build = -1;
	private String version;
	PluginVersion(String version, int build) {
		this.version = version;
		this.build = build;
		snapshot = true;
	}
	PluginVersion(String version) {
		this.version = version;
		snapshot = false;
	}
	public int getBuild() {
		return build;
	}
	public boolean isSnapshot() {
		return snapshot;
	}
	public String getVersion() {
		return version;
	}
}
