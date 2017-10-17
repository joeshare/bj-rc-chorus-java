package cn.rongcapital.chorus.modules.retrydemo;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class LocalShellTool {
//	private static Logger log = Logger.getLogger(LocalShellTool.class);
	public static final String RESULT_KEY = "result";
	public static final String ERROR_KEY = "error";
	/**0=成功；其它=失败*/
	public static final String EXITVALUE_KEY = "exitValue";
    /**
     * 本地shell调用
     * @param shellCommand
     * @return
     * @throws Exception 
     * @throws IOException
     * @throws InterruptedException
     */
    public Map<String, String> exec(String shellCommand) throws Exception {
    	InputStream in = null;
		InputStream errorIn = null;
		String result = "";
		String error = "";
		Map<String, String> map = new HashMap<String, String>();
		Process pid = null;
		try {
			String[] cmd = { "/bin/sh", "-c", shellCommand };
//			log.info("LocalShellCommand:" + shellCommand + " ======================>START.");
			pid = Runtime.getRuntime().exec(cmd);
			in = pid.getInputStream();
			errorIn = pid.getErrorStream();
			pid.waitFor();
			result = this.loadStream(in);
			error = this.loadStream(errorIn);
			map.put(RESULT_KEY, result);
			map.put(ERROR_KEY, error);
			map.put(EXITVALUE_KEY, String.valueOf(pid.exitValue()));
			String outlog = "LocalShellCommand:" + shellCommand + " ======================>END.ExitValue:" + pid.exitValue();			
			if (pid.exitValue() != 0) {
				outlog = outlog + ",RESULT:" + result + ",ERROR:" + error;
			}
//			log.info(outlog);
		} catch (Exception ioe) {
			map.put(ERROR_KEY, "执行shell命令异常:" + ioe.getMessage());
			map.put(EXITVALUE_KEY, String.valueOf(1));
//			log.info("LocalShellCommand:" + shellCommand + "执行shell命令异常:" + ioe.getMessage());
			throw new Exception(ioe);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (errorIn != null) {
				try {
					errorIn.close();
				} catch (IOException e) {
				}
			}
		}
		return map;
	}
    
	private String loadStream(InputStream in) throws IOException {
        int ptr = 0;
        in = new BufferedInputStream(in);
        StringBuffer buffer = new StringBuffer();
        while( (ptr = in.read()) != -1 ) {
            buffer.append((char)ptr);
        }
        return buffer.toString();
    }
}