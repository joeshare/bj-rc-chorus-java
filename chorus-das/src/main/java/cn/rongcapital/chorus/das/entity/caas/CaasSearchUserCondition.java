package cn.rongcapital.chorus.das.entity.caas;

import lombok.Data;

@Data
public class CaasSearchUserCondition {
    private String pageNo;
    private String pageSize;
    private String code;
    private String name;
    private String email;
}
