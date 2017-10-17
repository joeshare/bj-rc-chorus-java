package cn.rongcapital.chorus.authorization.plugin.ranger.data;

public class RangerHDFS extends RangerBase {
    private String resourceName;
    private boolean isEnabled = true;
    private boolean isRecursive = true;
    
    public String getResourceName() {
        return resourceName;
    }
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }
    public boolean getIsEnabled() {
        return isEnabled;
    }
    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
    public boolean getIsRecursive() {
        return isRecursive;
    }
    public void setIsRecursive(boolean isRecursive) {
        this.isRecursive = isRecursive;
    }
    
    
}
