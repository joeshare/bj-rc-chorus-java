package cn.rongcapital.chorus.common.exception;

/**
 * Created by abiton on 23/11/2016.
 */
public class NoSuchStatusCodeException extends Exception {

    public NoSuchStatusCodeException() {
        super("no such status code");
    }
}