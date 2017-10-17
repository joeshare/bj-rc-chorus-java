
package cn.rongcapital.chorus.common.solr.query;


import java.util.ArrayList;
import java.util.List;

public class SearchGroup {
    public enum CONDITION {
        AND, OR
    }

    CONDITION condition = CONDITION.AND;


    List<SearchGroup> searchGroups = new ArrayList<SearchGroup>();

    /**
     * @param condition
     */
    public SearchGroup(CONDITION condition) {
        this.condition = condition;
    }

    public String getWhereClause(String prefix) {
        return "";
    }

    /**
     * @param query
     */
    public void resolveValues(String prefix) {

    }
}
