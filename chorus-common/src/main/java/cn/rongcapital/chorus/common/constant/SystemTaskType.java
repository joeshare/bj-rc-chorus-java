package cn.rongcapital.chorus.common.constant;

import java.util.HashSet;
import java.util.Set;
/**
 * @author Lovett
 */
public enum SystemTaskType {
    FORK("FORK",new String[]{"forkTasks"}),
    JOIN("JOIN",new String[]{"joinOn"}),
    SIMPLE("SIMPLE",new String[]{"inputParameters"}),
    UNDEFINE("UNDEFINE",new String[]{});
    ;

    private String name;
    private String[] params;

    SystemTaskType(String name, String[] params) {
        this.name = name;
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public String[] getParams() {
        return params;
    }

    private static Set<String> systemTasks = new HashSet<>();
    static {
            systemTasks.add(SystemTaskType.SIMPLE.getName());
            systemTasks.add(SystemTaskType.FORK.getName());
            systemTasks.add(SystemTaskType.JOIN.getName());
    }

    public static boolean is(String name) {
            return systemTasks.contains(name);
    }

}
