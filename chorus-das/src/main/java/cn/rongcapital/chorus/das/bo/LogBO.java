package cn.rongcapital.chorus.das.bo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author yimin
 */
@Builder
@Getter
@Setter
public class LogBO implements Serializable {
    private static final long serialVersionUID = 2279733503774875414L;
    private Long start;
    private Long end;
    private int total = 0;
    private List<String> lines;

    public List<String> getLines() {
        if (CollectionUtils.isEmpty(this.lines)) return lines;
        List<String> reverse = new ArrayList<>(lines.size());
        reverse.addAll(lines);
        Collections.reverse(reverse);
        return reverse;
    }
}
