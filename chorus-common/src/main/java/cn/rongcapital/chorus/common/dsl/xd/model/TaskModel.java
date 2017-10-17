package cn.rongcapital.chorus.common.dsl.xd.model;

public class TaskModel {
    private int taskId;
    private String taskName;
    private int moduleType;
    private String moduleName;
    private String taskDSL;
    public int getTaskId() {
        return taskId;
    }
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
    public String getTaskName() {
        return taskName;
    }
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    public int getModuleType() {
        return moduleType;
    }
    public void setModuleType(int moduleType) {
        this.moduleType = moduleType;
    }
    public String getModuleName() {
        return moduleName;
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
    public String getTaskDSL() {
        return taskDSL;
    }
    public void setTaskDSL(String taskDSL) {
        this.taskDSL = taskDSL;
    }
}
