package cn.rongcapital.chorus.server.metadata.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by alan on 11/29/16.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SampleDataVo {
    private Collection<String> headerSet;
    private List<Map<String, Object>> data;
}
