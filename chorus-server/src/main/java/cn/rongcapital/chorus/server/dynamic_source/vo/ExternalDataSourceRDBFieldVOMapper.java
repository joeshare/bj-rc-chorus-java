package cn.rongcapital.chorus.server.dynamic_source.vo;

import cn.rongcapital.chorus.das.entity.ExternalDataSourceRDBField;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abiton on 25/07/2017.
 */
public class ExternalDataSourceRDBFieldVOMapper {
    public static ExternalDataSourceRDBFieldVO of(ExternalDataSourceRDBField po){
        ExternalDataSourceRDBFieldVO vo = null;
        if (po != null){
            vo = new ExternalDataSourceRDBFieldVO();
            vo.setName(po.getName());
            vo.setType(po.getType());
            vo.setLength(po.getLength());
        }
        return vo;
    }

    public static List<ExternalDataSourceRDBFieldVO> of(List<ExternalDataSourceRDBField> pos){
        List<ExternalDataSourceRDBFieldVO> vos = new ArrayList<>();
        if (pos != null && !pos.isEmpty()){
            pos.stream().forEach(
                    po -> vos.add(of(po))
            );
        }
        return vos;
    }
}
