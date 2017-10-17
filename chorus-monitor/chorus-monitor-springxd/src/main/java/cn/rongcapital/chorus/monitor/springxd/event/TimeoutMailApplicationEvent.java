package cn.rongcapital.chorus.monitor.springxd.event;

import cn.rongcapital.chorus.das.entity.TaskExecutionTimeout;

public class TimeoutMailApplicationEvent extends BasicApplicationEvent<TaskExecutionTimeout> {
    private static final long serialVersionUID = 5355474955863216541L;

    public TimeoutMailApplicationEvent(TaskExecutionTimeout source) {
        super(source);
    }

}
