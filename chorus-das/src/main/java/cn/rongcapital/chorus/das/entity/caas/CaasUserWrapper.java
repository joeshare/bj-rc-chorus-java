package cn.rongcapital.chorus.das.entity.caas;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Data
public class CaasUserWrapper {

    private int pageNo;
    private int pageSize;
    private int total;
    private List<CaasUser> records;

    /**
     * @return
     * @author yunzhong
     * @version
     * @since 2017年5月22日
     */
    public CaasUser getOne() {
        if (CollectionUtils.isEmpty(this.records)) {
            return null;
        }
        return records.get(0);
    }
}
