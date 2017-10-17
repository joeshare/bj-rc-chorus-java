package cn.rongcapital.chorus.das.service;

/**
 * @author Lovett
 */
public interface HdfsSnapshotService {
    /**
     *
     * @param dir 开启快照功能的目录（不支持嵌套目录开启snapshot）
     * @return 成功返回 true 失败返回 false
     */
    Boolean allowSnapshot(String dir);

}
