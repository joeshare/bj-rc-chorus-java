package cn.rongcapital.chorus.das.entity;

import java.util.List;

import lombok.Data;

@Data
public class ProjectMemberCount {

    private Long total = 0L;
    private List<ProjectMemberRoleCount> datas;

    public void add(Long count) {
        total += count;
    }

}
