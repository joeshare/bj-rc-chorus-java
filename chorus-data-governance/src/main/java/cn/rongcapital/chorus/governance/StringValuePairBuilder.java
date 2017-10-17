package cn.rongcapital.chorus.governance;

/**
 * @author yimin
 */
public class StringValuePairBuilder extends StringKeyXValuePairBuilder<String> {

    public StringValuePairBuilder(String key) {
        super(key);
    }

    @Override
    public KeyValuePairBuilder value(String value) {
        _value.append("'").append(value).append("'");
        return this;
    }
}
