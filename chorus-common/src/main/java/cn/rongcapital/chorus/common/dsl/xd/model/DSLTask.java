package cn.rongcapital.chorus.common.dsl.xd.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.rongcapital.chorus.common.constant.SystemTaskType;

public class DSLTask {
    private String name;
    private String taskReferenceName;
    private String type = SystemTaskType.SIMPLE.name();
    private String workerType;
    private Map<String,String> workerParams;
    private Map<String,String> inputParameters;
    private List<List<DSLTask>> forkTasks = new LinkedList<>();
    private List<String> joinOn = new LinkedList<>();

    private List<List<DSLTask>> children(){
        List<List<DSLTask>> v1 = new LinkedList<>();
        SystemTaskType tt = SystemTaskType.UNDEFINE;
        if(SystemTaskType.is(type)) {
                tt = SystemTaskType.valueOf(type);
        }

        switch(tt){
                case FORK:
                        v1.addAll(forkTasks);
                        break;
                default:
                        break;
        }
        return v1;
}

    public List<DSLTask> all() {
        List<DSLTask> all = new LinkedList<>();
        all.add(this);
        for (List<DSLTask> wfts : children()) {
            for (DSLTask wft : wfts) {
                all.addAll(wft.all());
            }
        }
        return all;
    }

    public boolean has(String taskReferenceName) {

        if (this.getTaskReferenceName().equals(taskReferenceName)) {
            return true;
        }

        SystemTaskType tt = SystemTaskType.SIMPLE;
        if (SystemTaskType.is(type)) {
            tt = SystemTaskType.valueOf(type);
        }

        switch (tt) {
            case FORK:
                for (List<DSLTask> childx : children()) {
                    for (DSLTask child : childx) {
                        if (child.has(taskReferenceName)) {
                            return true;
                        }
                    }
                }
                break;
            default:
                break;
        }

        return false;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getTaskReferenceName() {
        return taskReferenceName;
    }
    public void setTaskReferenceName(String taskReferenceName) {
        this.taskReferenceName = taskReferenceName;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getWorkerType() {
        return workerType;
    }
    public void setWorkerType(String workerType) {
        this.workerType = workerType;
    }
    public Map<String, String> getWorkerParams() {
        return workerParams;
    }
    public void setWorkerParams(Map<String, String> workerParams) {
        this.workerParams = workerParams;
    }
    public Map<String, String> getInputParameters() {
        return inputParameters;
    }
    public void setInputParameters(Map<String, String> inputParameters) {
        this.inputParameters = inputParameters;
    }
    public List<List<DSLTask>> getForkTasks() {
        return forkTasks;
    }
    public void setForkTasks(List<List<DSLTask>> forkTasks) {
        this.forkTasks = forkTasks;
    }
    public List<String> getJoinOn() {
        return joinOn;
    }
    public void setJoinOn(List<String> joinOn) {
        this.joinOn = joinOn;
    }
}
