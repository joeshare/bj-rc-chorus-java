package cn.rongcapital.chorus.authorization.api.data;

/**
 * 数据授权类型
 * Created by shicheng on 2017/3/8.
 */
public enum AuthorizationDataType {

    FS("HDFS"),
    DW("HIVE"),
    RM("YARN"),
    HBASE("HBASE"),
    STORM("STORM"),
    KNOX("KNOX"),
    SOLR("SOLR"),
    KAFKA("KAFKA"),
    NIFI("NIFI");

    AuthorizationDataType(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
