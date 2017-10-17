package cn.rongcapital.chorus.das.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by alan on 11/22/16.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TreeNode implements Serializable {
    static final long serialVersionUID = 7682929618928390L;
    public String nodeId;
    public String nodeName;
    public Integer type;
    public Integer pathLevel;
    public List<TreeNode> children;

    public static List<TreeNode> NilChildren() {
        return new LinkedList<>();
    }

}
