package cn.rongcapital.chorus.server.dynamic_source.vo;

import cn.rongcapital.chorus.das.entity.ExternalDataSourceCSVField;
import cn.rongcapital.chorus.das.entity.ExternalDataSourceRDBField;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maboxiao on 21/08/2017.
 */
public class ExternalDataSourceCSVFieldVOMapper {
    public static ExternalDataSourceCSVFieldVO of(ExternalDataSourceCSVField po){
        ExternalDataSourceCSVFieldVO vo = null;
        if (po != null){
            vo = new ExternalDataSourceCSVFieldVO();
            vo.setName(po.getName());
            vo.setType(po.getType());
            vo.setLength(po.getLength());
        }
        return vo;
    }

    public static List<ExternalDataSourceCSVFieldVO> of(List<ExternalDataSourceCSVField> pos){
        List<ExternalDataSourceCSVFieldVO> vos = new ArrayList<>();
        if (pos != null && !pos.isEmpty()){
            pos.stream().forEach(
                    po -> vos.add(of(po))
            );
        }
        return vos;
    }
}
