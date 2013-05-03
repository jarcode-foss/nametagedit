package ca.wacos;

/**
 * Written by Levi Webb
 * <p/>
 * Date: 03/05/13
 * Time: 12:27 AM
 */
public class TeamInfo {
    private String name;
    private String prefix;
    private String suffix;
    public TeamInfo(String name) {
        this.name = name;
    }
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
    public String getPrefix() {
        return prefix;
    }
    public String getSuffix() {
        return suffix;
    }
    public String getName() {
        return name;
    }

}
