package cn.rongcapital.chorus.governance;

/**
 * @author yimin
 */
public interface KeyValuePairBuilder<T> {
    KeyValuePairBuilder value(T key);
    KeyValuePairBuilder op(Operation or, KeyValuePairBuilder age);
    String where(String typeName);
}
