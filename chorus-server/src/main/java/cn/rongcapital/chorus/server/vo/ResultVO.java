package cn.rongcapital.chorus.server.vo;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by abiton on 21/11/2016.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultVO<T> {
    private String code;
    private String msg;
    private T data;

    public ResultVO(StatusCode sc) {
        code = sc.getCode();
        msg = sc.getDesc();
    }

    public ResultVO(StatusCode sc, T data) {
        code = sc.getCode();
        msg = sc.getDesc();
        this.data = data;
    }

    public static <T> ResultVO<T> error(){
        return new ResultVO<>(StatusCode.SYSTEM_ERR);
    }
    public static <T> ResultVO<T> error(StatusCode statusCode){
        return new ResultVO<>(statusCode);
    }
    
    public static <T> ResultVO<T> error(StatusCode statusCode, T data){
        return new ResultVO<>(statusCode, data);
    }

    public static <T> ResultVO<T> error(ServiceException se) {
        return new ResultVO<>(se.code, se.message, null);
    }

    public static <T> ResultVO<T> success() {
        return new ResultVO<>(StatusCode.SUCCESS);
    }

    public static <T> ResultVO<T> success(T data) {
        return new ResultVO<>(StatusCode.SUCCESS, data);
    }
}
