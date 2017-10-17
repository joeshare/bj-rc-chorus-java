package cn.rongcapital.chorus.springxd.processor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class UppercaseTransformer {
	public String transform(String payload) {
		
		String result = "";
	    if (payload != null) {
//	    	String[] arrs = payload.split(",");
//	    	byte[] bytes = new byte[arrs.length];
//	    	for (int i = 0;i < arrs.length;i++) {
//	    		bytes[i] = (byte)Integer.parseInt(arrs[i]);
//	    	}
//	    	result = new String(bytes).toUpperCase();
	    	result = payload.toUpperCase();
	    }
//	    File file = new File("/tmp/xd/output/uppercaseFile");
//	    FileWriter fw = null;
//	    try {
//			fw = new FileWriter(file);
//			fw.write(result);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			if (fw != null) {
//				try {
//					fw.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}			
//		}
		return result;
	}
}
