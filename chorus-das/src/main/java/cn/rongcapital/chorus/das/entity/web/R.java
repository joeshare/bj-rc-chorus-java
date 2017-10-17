package cn.rongcapital.chorus.das.entity.web;
/**
 * 公用返回信息类
 * @author HANS
 *
 */
public class R {

	boolean state;

	String message;

	Object result;

	public R() {
	}

	public R(boolean state, String message) {
		this.state = state;
		this.message = message;
	}

	public R(boolean state, String message, Object result) {
		this.state = state;
		this.message = message;
		this.result = result;
	}

	public String toString(){
		return "state:" + state + ", message:" + message + ", result:" + result;
	}
	
	public static R trueState(String message) {
		return new R(true, message);
	}

	public static R trueState(String message, Object result) {
		return new R(true, message, result);
	}

	public static R falseState(String message) {
		return new R(false, message);
	}

	public static R falseState(Throwable t) {
		return new R(false, t.getMessage());
	}

	public static R falseState(Throwable t, Object result) {
		return new R(false, t.getMessage(), result);
	}

	public static R falseState(String message, Object result) {
		return new R(false, message, result);
	}

	/**
	 * @return the state
	 */
	public boolean isState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(boolean state) {
		this.state = state;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the result
	 */
	public Object getResult() {
		return result;
	}

	/**
	 * @param result
	 *            the result to set
	 */
	public void setResult(Object result) {
		this.result = result;
	}

}
