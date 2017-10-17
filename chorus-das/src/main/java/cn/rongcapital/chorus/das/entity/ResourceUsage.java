package cn.rongcapital.chorus.das.entity;

public class ResourceUsage {
    private Integer id;

    private String usageCode;

    private String usageName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsageCode() {
        return usageCode;
    }

    public void setUsageCode(String usageCode) {
        this.usageCode = usageCode == null ? null : usageCode.trim();
    }

    public String getUsageName() {
        return usageName;
    }

    public void setUsageName(String usageName) {
        this.usageName = usageName == null ? null : usageName.trim();
    }
}