package cn.rongcapital.chorus.server.dynamic_source.vo;

import java.util.ArrayList;
import java.util.List;

import cn.rongcapital.chorus.das.entity.ExternalDataSourceJsonField;

/**
 * @author kevin.gong
 * @Time 2017年9月4日 下午3:55:25
 */
public class ExternalDataSourceJsonFieldVOMapper {
    public static ExternalDataSourceJsonFieldVO of(ExternalDataSourceJsonField po){
        ExternalDataSourceJsonFieldVO vo = null;
        if (po != null){
            vo = new ExternalDataSourceJsonFieldVO();
            vo.setName(po.getName());
            vo.setType(po.getType());
            vo.setLength(po.getLength());
        }
        return vo;
    }

    public static List<ExternalDataSourceJsonFieldVO> of(List<ExternalDataSourceJsonField> pos){
        List<ExternalDataSourceJsonFieldVO> vos = new ArrayList<>();
        if (pos != null && !pos.isEmpty()){
            pos.stream().forEach(
                    po -> vos.add(of(po))
            );
        }
        return vos;
    }
}
