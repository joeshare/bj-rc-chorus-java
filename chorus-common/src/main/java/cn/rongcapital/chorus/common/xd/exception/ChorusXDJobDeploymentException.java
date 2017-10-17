package cn.rongcapital.chorus.common.xd.exception;

/**
 * XD任务部署业务异常, 在部署任务具体步骤时捕获该异常以进行任务部署回滚。
 *
 * @author li.hzh
 * @date 2016-11-22 11:18
 */
public class ChorusXDJobDeploymentException extends Exception {

    public ChorusXDJobDeploymentException() {
        super();
    }

    public ChorusXDJobDeploymentException(String msg, Throwable t) {
        super(msg, t);
    }

    public ChorusXDJobDeploymentException(Throwable t) {
        super(t);
    }

    public ChorusXDJobDeploymentException(String msg) {
        super(msg);
    }
}
