package cn.rongcapital.chorus.common.exception;

import cn.rongcapital.chorus.common.constant.StatusCode;

/**
 * @author yunzhong
 *
 */
public class HadoopPathException extends ServiceException {

    public HadoopPathException(StatusCode statusCode) {
        super(statusCode);
    }

    private static final long serialVersionUID = 3796544151945956038L;

}
