package cn.rongcapital.chorus.common.constant;

public enum HttpMethodType {
    GET("GET"),
    POST("POST"),
    PUT("PUT")
    ;

    public final String type;

    HttpMethodType(String type) {
        this.type = type;
    }
}
