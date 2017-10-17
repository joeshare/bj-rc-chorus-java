package cn.rongcapital.chorus.governance;

/**
 * @author yimin
 */
public class NumberValuePairBuilder extends StringKeyXValuePairBuilder<Number> {

    public NumberValuePairBuilder(String key) {
        super(key);
    }

    @Override
    public KeyValuePairBuilder value(Number key) {
        _value.append(key);
        return this;
    }
}
