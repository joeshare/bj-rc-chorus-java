package cn.rongcapital.chorus.governance;

import org.testng.annotations.Test;

import static cn.rongcapital.chorus.governance.Operation.AND;
import static cn.rongcapital.chorus.governance.Operation.OR;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author yimin
 */
public class KeyValuePairBuilderTest {

    @Test
    public void stringKeyStringValueBuild() throws Exception {
        final String pair = new StringValuePairBuilder("name").value("Alfer").toString();
        assertThat(pair).isEqualTo("name='Alfer'");
    }

    @Test
    public void stringKeyNumberValueBuild() throws Exception {
        final String pair = new NumberValuePairBuilder("age").value(19).toString();
        assertThat(pair).isEqualTo("age=19");
    }

    @Test
    public void testBuild() throws Exception {
        final KeyValuePairBuilder first = new StringValuePairBuilder("name").value("Red");
        first.op(OR, new NumberValuePairBuilder("age").value(10).op(AND,new StringValuePairBuilder("name").value("Yellow")));
        first.op(AND,
                 new StringValuePairBuilder("name").value("Blue")
                                                   .op(OR, new NumberValuePairBuilder("age").value(20))
                                                   .op(OR, new StringValuePairBuilder("name").value("Green")));

        final String actual = first.where("TestType");
        final String expected = "TestType where "
                                + "name='Red'"
                                + " or (age=10 and (name='Yellow'))"
                                + " and (name='Blue' or (age=20) or (name='Green'))";
        assertThat(actual).isEqualTo(expected);

    }
}
