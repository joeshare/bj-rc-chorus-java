package cn.rongcapital.chorus.monitor.springxd.event;

import org.springframework.context.ApplicationEvent;

public class BasicApplicationEvent<T> extends ApplicationEvent {
    private static final long serialVersionUID = 5355474955863216541L;

    private T target;

    public BasicApplicationEvent(T source) {
        super(source);
        this.target = source;
    }

    public T getTarget() {
        return target;
    }
}
