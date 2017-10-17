package cn.rongcapital.chorus.das.entity;

import java.util.List;

/**
 * 分页数据
 * @author kevin.gong
 * @Time 2017年8月10日 下午4:40:52
 */
public class PageInfo<T> {

    /**
     * 总数量
     */
    private int totalNum;
    
    /**
     * 当前页数据
     */
    private List<T> data;

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
    
}
