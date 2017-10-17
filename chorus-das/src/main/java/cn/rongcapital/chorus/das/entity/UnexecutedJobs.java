package cn.rongcapital.chorus.das.entity;

import java.util.List;

/**
 * 未执行任务信息
 * @author kevin.gong
 * @Time 2017年9月21日 上午9:00:41
 */
public class UnexecutedJobs {
   
    /**
     * 总数
     */
    private int total;
    
    /**
     * 未执行任务列表
     */
    private List<UnexecutedJob> list;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<UnexecutedJob> getList() {
        return list;
    }

    public void setList(List<UnexecutedJob> list) {
        this.list = list;
    }
}