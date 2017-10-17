package cn.rongcapital.chorus.common.xd.service;

/**
 * DSL和图形定义转换层
 *
 * @author li.hzh
 * @date 2016-11-15 15:18
 */
public interface DslGraphService {

    /**
     * 从DSL定义转换成Graph定义
     *
     * @param graphText
     * @return Graph定义, 出错时可能为空
     */
    String fromDSLToGraph(String graphText);

    /**
     * 从Graph定义转换成DSL定义
     *
     * @param dslText
     * @return DSL定义, 出错时可能为空
     */
    String fromGraphToDSL(String dslText);


}
