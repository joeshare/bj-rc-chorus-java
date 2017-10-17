package cn.rongcapital.chorus.modules.import_data;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;

public class LocalShellTool {
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
			pid = Runtime.getRuntime().exec(cmd);
//			in = pid.getInputStream();
//			errorIn = pid.getErrorStream();

			StreamGobbler errorGobbler = new StreamGobbler(pid.getErrorStream(), "Error", map);
			StreamGobbler outputGobbler = new StreamGobbler(pid.getInputStream(), "Output", map);
			errorGobbler.start();
			outputGobbler.start();

//			result = this.loadStream(in);
//			error = this.loadStream(errorIn);
//			map.put(RESULT_KEY, result);
//			map.put(ERROR_KEY, error);

			int exitValue = pid.waitFor();

			map.put(EXITVALUE_KEY, String.valueOf(exitValue));

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
    
//	private String loadStream(InputStream in) throws IOException {
//        int ptr = 0;
//        in = new BufferedInputStream(in);
//        StringBuffer buffer = new StringBuffer();
//        while( (ptr = in.read()) != -1 ) {
//            buffer.append((char)ptr);
//        }
//        return buffer.toString();
//    }

	private class StreamGobbler extends Thread {

		InputStream is;
		String type;
		Map<String, String> map;

		public StreamGobbler(InputStream is, String type, Map<String, String> map) {
			this.is = is;
			this.type = type;
			this.map = map;
		}

		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				StringBuffer buffer = new StringBuffer();
				while ((line = br.readLine()) != null) {
					buffer.append(line);
					buffer.append(System.getProperty("line.separator"));
				}
				if (type.equals("Error")) {
					map.put(ERROR_KEY, buffer.toString());
				} else {
					map.put(RESULT_KEY, buffer.toString());
				}
			} catch (IOException ioe) {
			}
		}
	}
}