package cn.rongcapital.chorus.governance;

/**
 * @author yimin
 */
public abstract class StringKeyXValuePairBuilder<T> implements KeyValuePairBuilder<T> {
    protected StringBuilder _value = new StringBuilder();

    protected StringKeyXValuePairBuilder(String key) {
        _value.append(key).append("=");
    }

    @Override
    public KeyValuePairBuilder op(Operation operation, KeyValuePairBuilder keyValuePairBuilder) {
        this._value.append(operation.op).append("(" + keyValuePairBuilder + ")");
        return this;
    }

    @Override
    public String toString() {
        return _value.toString();
    }

    @Override
    public String where(String typeName) {
        return typeName + " where " + this.toString();
    }
}
