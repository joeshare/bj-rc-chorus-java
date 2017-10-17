/*************************************************
 * @功能简述: API错误码类
 * @项目名称: marketing cloud
 * @see: 
 * @author: 宋世涛
 * @version: 0.0.1
 * @date: 2016/5/16
 * @复审人: 
*************************************************/

package cn.rongcapital.chorus.common.constant;

public enum ApiErrorCode {
	
	SUCCESS(0,"success"),
	
	PARAMETER_ERROR(1001,"parameter error"),//1001-1999,参数相关错误码
	VALIDATE_ERROR(1002,"validation failed"),//校验失败
	
	FILE_ERROR(1003,"file is null"),
	
	DB_ERROR(2001,"DB error"),//2001-2999,数据库相关的错误码
	DB_ERROR_TABLE_DATA_NOT_EXIST(2002,"table data not exist"),//数据在表里不存在

	REDIS_GET_DATA_ERROR(7004,"redis取数据异常"),
	ID_NOTFOUND_ERROR(7007,"file_id不存在"),
	SYSTEM_ERROR(9001,"system error");//9001-9999,系统错误码
	
    private int code;
     
    private String msg;
     
    private ApiErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
     
    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }
    
}
