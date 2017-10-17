package cn.rongcapital.chorus.common.dsl.xd.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DSLWorkFlow {
    private String name;
    private String description;
    private String version;
    private List<DSLTask> tasks;
    private String schemaVersion;

    public List<DSLTask> allTask() {
        List<DSLTask> all = new LinkedList<>();
        for (DSLTask wft : tasks) {
            all.addAll(wft.all());
        }
        return all;
    }

    public DSLTask getTaskByRefName(String taskReferenceName) {
        Optional<DSLTask> found = allTask().stream().filter(wft -> wft.getTaskReferenceName().equals(taskReferenceName)).findFirst();
        if (found.isPresent()) {
            return found.get();
        }
        return null;
    }

    public List<DSLTask> getTaskByWorkerType(String workerType){
        List<DSLTask> found = allTask().stream().filter(wft -> workerType.equals(wft.getWorkerType())).collect(Collectors.toList());
        if (found != null && found.size() > 0 ) {
            return found;
        }
        return null;
    }

    public List<DSLTask> getTaskByType(String type){
        List<DSLTask> found = allTask().stream().filter(wft -> type.equals(wft.getType())).collect(Collectors.toList());
        if (found != null && found.size() > 0 ) {
            return found;
        }
        return null;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public String getSchemaVersion() {
        return schemaVersion;
    }
    public List<DSLTask> getTasks() {
        return tasks;
    }
    public void setTasks(List<DSLTask> tasks) {
        this.tasks = tasks;
    }
    public void setSchemaVersion(String schemaVersion) {
        this.schemaVersion = schemaVersion;
    }
}
