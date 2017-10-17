package cn.rongcapital.chorus.server.resourceout.vo;

import lombok.Data;
import lombok.ToString;

/**
 * 查询条件信息
 *
 * @author shicheng
 * @version 1.0
 * @since <pre>十一月 24, 2016</pre>
 */
@Data
@ToString
public class QueryVo {

    private Long projectId; // 项目 id
    private String data; // 查询数据
    private Boolean isAccurate; // 是否精确查询

}
