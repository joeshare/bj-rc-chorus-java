package cn.rongcapital.chorus.server.dynamic_source.param;

import lombok.Data;

/**
 * Created by maboxiao on 21/08/2017.
 */
@Data
public class ExternalDataSourceCSVFieldParam {
    private String csvFilePath;
    private String dwUserName;
    private String hasTitle;
    private Integer displayLineCount;
}
