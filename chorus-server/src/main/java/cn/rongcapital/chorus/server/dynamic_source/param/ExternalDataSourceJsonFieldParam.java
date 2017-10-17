package cn.rongcapital.chorus.server.dynamic_source.param;

import lombok.Data;

/**
 * @author kevin.gong
 * @Time 2017年9月4日 下午3:52:08
 */
@Data
public class ExternalDataSourceJsonFieldParam {
    private String filePath;
    private String dwUserName;
    private Integer displayLineCount;
}
