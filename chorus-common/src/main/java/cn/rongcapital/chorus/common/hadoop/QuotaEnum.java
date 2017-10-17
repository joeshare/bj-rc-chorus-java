package cn.rongcapital.chorus.common.hadoop;

/**
 * @author yunzhong
 *
 */
public enum QuotaEnum {
    B("B", 1L), K("K", 1024L), M("M", 1024L * 1024), G("G", 1024L * 1024 * 1024), T("T", 1024L * 1024 * 1024 * 1024);

    private long value;
    private String key;

    QuotaEnum(String key, long value) {
        this.key = key;
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    /**
     * @param key
     * @return
     * @author yunzhong
     * @time 2017年6月12日下午3:20:30
     */
    public static long getValue(String key) {
        for (QuotaEnum quota : QuotaEnum.values()) {
            if (quota.key.equals(key)) {
                return quota.value;
            }
        }
        return 0;
    }
}
