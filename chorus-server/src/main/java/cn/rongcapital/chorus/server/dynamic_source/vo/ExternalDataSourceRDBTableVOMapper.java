package cn.rongcapital.chorus.server.dynamic_source.vo;

import cn.rongcapital.chorus.das.entity.ExternalDataSourceRDBTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abiton on 25/07/2017.
 */
public class ExternalDataSourceRDBTableVOMapper {
    public static ExternalDataSourceRDBTableVO of(ExternalDataSourceRDBTable po) {
        ExternalDataSourceRDBTableVO vo = null;
        if (po != null) {
            vo = new ExternalDataSourceRDBTableVO();
            vo.setName(po.getName());
        }
        return vo;
    }

    public static List<ExternalDataSourceRDBTableVO> of(List<ExternalDataSourceRDBTable> pos) {
        List<ExternalDataSourceRDBTableVO> vos = new ArrayList<>();
        if (pos != null && !pos.isEmpty()) {
            pos.stream().forEach(
                    po -> vos.add(of(po))
            );
        }
        return vos;
    }
}
