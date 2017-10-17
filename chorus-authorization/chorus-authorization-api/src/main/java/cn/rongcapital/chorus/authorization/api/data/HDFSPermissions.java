package cn.rongcapital.chorus.authorization.api.data;

import java.util.List;

import org.springframework.util.CollectionUtils;

public enum HDFSPermissions {

    Read("Read"), Write("Write"), Execute("Execute");

    HDFSPermissions(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static int encode(List<Object> perms) {
        int result = 0;
        if (CollectionUtils.isEmpty(perms)) {
            return result;
        }
        if (perms.contains(Read.getValue())) {
            result = result | 4;
        }
        if (perms.contains(Write.getValue())) {
            result = result | 2;
        }
        if (perms.contains(Execute.getValue())) {
            result = result | 1;
        }
        return result;
    }
}
