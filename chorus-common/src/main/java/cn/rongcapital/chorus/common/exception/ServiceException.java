package cn.rongcapital.chorus.common.exception;

import cn.rongcapital.chorus.common.constant.StatusCode;
import lombok.Data;

/**
 * Created by alan on 11/23/16.
 */
@Data
public class ServiceException extends RuntimeException {

    /**
     * 异常错误码
     */
    public final String code;

    public final String message;

    public ServiceException(StatusCode statusCode) {
        super();
        this.code = statusCode.getCode();
        this.message = statusCode.getDesc();
    }

    public ServiceException(StatusCode statusCode, Throwable t) {
        super(t);
        this.code = statusCode.getCode();
        this.message = statusCode.getDesc();
    }

    public ServiceException(Integer respCode, String message) {
        super();
        this.code = respCode.toString();
        this.message = message;
    }
    public ServiceException(String respCode, String message) {
        super();
        this.code = respCode;
        this.message = message;
    }
}
