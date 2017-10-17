package cn.rongcapital.chorus.das.entity.caas;

import lombok.Data;

import java.util.List;

@Data
public class CaasRoleWrapper {

    private int pageNo;
    private int pageSize;
    private int total;
    private List<CaasRole> records;
}
