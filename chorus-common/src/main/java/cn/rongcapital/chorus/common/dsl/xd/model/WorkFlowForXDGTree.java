package cn.rongcapital.chorus.common.dsl.xd.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cn.rongcapital.chorus.common.constant.SystemTaskType;
import cn.rongcapital.chorus.common.util.DSLToXDUtil;
import cn.rongcapital.chorus.common.util.JsonUtils;
/**
 * dsl workflow 语法树
 * @author Lovett
 */
public class WorkFlowForXDGTree {
    private DSLWorkFlow workFlow;
    private String workFlowJson;

    public WorkFlowForXDGTree(String workFlowJsonDsl) throws Exception {
        workFlowJson = workFlowJsonDsl;
        this.workFlow = JsonUtils.Json2Object(workFlowJsonDsl, DSLWorkFlow.class);
    }

    public WorkFlowForXDGTree(DSLWorkFlow workFlow) {
        this.workFlow = workFlow;
    }

    public String parseDSLToXD() {
        List<DSLTask> tasks = workFlow.getTasks();
        List<String> dslList = new LinkedList<>();

        List<String> xdDSL = rParse(dslList, tasks);
        xdDSL = clearExtraFork(xdDSL);
        StringBuilder xdDSLBuilder = new StringBuilder();

        for (String s : xdDSL) {
            xdDSLBuilder.append(s);
            xdDSLBuilder.append(" ");
        }

        return xdDSLBuilder.toString();
    }

    public String parseStreamDSLToXD(List<TaskModel> taskModels) throws Exception{
        StringBuilder dslBuilder = new StringBuilder();
        List<DSLTask> tasks = workFlow.getTasks();
       Iterator<DSLTask> iterator = tasks.iterator();

       while(iterator.hasNext()){
           String taskName = iterator.next().getName();

            for (TaskModel tm : taskModels) {
                if (taskName.equals(tm.getTaskName())) {
                    String moduleName = tm.getModuleName();
                    String taskDSL = tm.getTaskDSL();
                    String definitionToXd = DSLToXDUtil.taskDslDefinitionToXd(moduleName, taskDSL);

                    dslBuilder.append(taskName);
                    dslBuilder.append(":");
                    dslBuilder.append(definitionToXd);

                    break;
                }
            }

           if(iterator.hasNext()){
               dslBuilder.append(" | ");
           }
       }

        return dslBuilder.toString();
    }

    private List<String> rParse(List<String> dslList ,List<DSLTask> tasks){
        Iterator<DSLTask> iterator = tasks.iterator();
        while (iterator.hasNext()) {
            DSLTask dslTask = iterator.next();
            SystemTaskType tt = SystemTaskType.UNDEFINE;
            String type = dslTask.getType();

            if (SystemTaskType.is(type)) {
                tt = SystemTaskType.valueOf(type);
            }

            switch (tt) {
            case FORK:
                dslList.add("<");
                dslList.addAll(rParseName(new ArrayList<String>(), dslTask));
                break;
            case JOIN:
                dslList.add(">");
                break;
            case SIMPLE:
                if (dslList.size() > 0 && !iterator.hasNext() && !dslList.get(dslList.size()-1).equals("||")) {
                    dslList.add("||");
                    dslList.add(dslTask.getName());
                } else if(dslList.size() > 0 && iterator.hasNext() && !dslList.get(dslList.size()-1).equals("||")){
                    dslList.add("||");
                    dslList.add(dslTask.getName());
                    dslList.add("||");
                }else if(dslList.size() == 0 && iterator.hasNext()){
                    dslList.add(dslTask.getName());
                    dslList.add("||");
                }else{
                    dslList.add(dslTask.getName());
                }
                break;
            default:
                break;
            }
        }

        return dslList;
    }

    private List<String> rParseName(List<String> dslList, DSLTask dslTask) {

            SystemTaskType tt = SystemTaskType.UNDEFINE;
            String type = dslTask.getType();
            if (SystemTaskType.is(type)) {
                tt = SystemTaskType.valueOf(type);
            }

            switch (tt) {
            case FORK:
                Iterator<List<DSLTask>> itertorList = dslTask.getForkTasks().iterator();
                while(itertorList.hasNext()){
                    List<DSLTask> taskList = itertorList.next();
                    dslList.addAll(rParse(new ArrayList<String>(), taskList));
                    if(itertorList.hasNext()){
                        dslList.add("&");
                    }
                }
                break;
            case SIMPLE:
                break;
            default:
                break;
            }

        return dslList;
    }

    public String getWorkFlowJson() {
        return workFlowJson;
    }

    public DSLWorkFlow getWorkFlow() {
        return workFlow;
    }
    /**
     * 清除XD部署时Fork形式的多余语法
     */
    private List<String> clearExtraFork(List<String> xdDslList){
        Iterator<String> xdDslOrigin = xdDslList.iterator();
        int extraNum = 0;
        int removedNum = 0;

        while(xdDslOrigin.hasNext()){
            String currentV = xdDslOrigin.next();

            if("&".equals(currentV) && xdDslOrigin.hasNext()){
                do {
                    String nextV = xdDslOrigin.next();

                    if ("<".equals(nextV)) {
                        xdDslOrigin.remove();
                        extraNum++;
                        removedNum--;
                    }else{
                        break;
                    }
                }
                while (xdDslOrigin.hasNext());
            }
            else if(">".equals(currentV) && extraNum != removedNum){
                xdDslOrigin.remove();
                extraNum--;
                removedNum++;
            }
        }

        return xdDslList;
    }

}
