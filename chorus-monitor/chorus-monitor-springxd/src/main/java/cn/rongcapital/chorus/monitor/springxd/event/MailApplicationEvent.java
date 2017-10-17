package cn.rongcapital.chorus.monitor.springxd.event;

import cn.rongcapital.chorus.das.entity.XDExecution;

public class MailApplicationEvent extends BasicApplicationEvent<XDExecution> {
    private static final long serialVersionUID = 5355474955863216541L;

    public MailApplicationEvent(XDExecution source) {
        super(source);
    }

}
