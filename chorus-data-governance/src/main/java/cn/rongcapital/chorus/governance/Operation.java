package cn.rongcapital.chorus.governance;

/**
 * @author yimin
 */
public enum Operation {

    AND(" and "),
    OR(" or "),;

    public final String op;

    Operation(String op) {

        this.op = op;
    }
}
