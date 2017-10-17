package cn.rongcapital.chorus.common.util;

public class HadoopStrConvertor {
	/**
	 * 将输入字符串中的GUID中的HADOOP非法字符转义
	 * @param str
	 * @return
	 */
	public static String convertStrForHadoop(String str) {
		str = str.replaceAll("-", "_");
		return str;
	}
	
//	/**
//	 * 检验输入的字符串中是否存在HADOOP中非法的字符
//	 * @param str
//	 * @return R
//	 */
//	public static R checkSpecialChar(String str) {
//		if (str.length() < str.getBytes().length) {
//			return R.falseState("不能为中文或中文字符");
//		}
//
//		String first = str.substring(0, 1);
//		if (!first.matches("[a-zA-Z]{1}")) {
//			return R.falseState("首字母必须为英文字母");
//		}
//
//		if (str.indexOf(" ") >= 0 || str.indexOf("	") >= 0 || str.indexOf("`") >= 0 || str.indexOf("~") >= 0 || str.indexOf("!") >= 0 || str.indexOf("@") >= 0
//				 || str.indexOf("#") >= 0 || str.indexOf("$") >= 0 || str.indexOf("%") >= 0 || str.indexOf("^") >= 0
//				 || str.indexOf("&") >= 0 || str.indexOf("*") >= 0 || str.indexOf("(") >= 0 || str.indexOf(")") >= 0
//				 || str.indexOf("-") >= 0 || str.indexOf("+") >= 0 || str.indexOf("=") >= 0 || str.indexOf("|") >= 0
//				 || str.indexOf("\\") >= 0 || str.indexOf("}") >= 0 || str.indexOf("]") >= 0 || str.indexOf("{") >= 0
//				 || str.indexOf("[") >= 0 || str.indexOf(";") >= 0 || str.indexOf(":") >= 0 || str.indexOf("'") >= 0
//				 || str.indexOf("\"") >= 0 || str.indexOf(",") >= 0 || str.indexOf(".") >= 0 || str.indexOf("<") >= 0
//				 || str.indexOf(">") >= 0 || str.indexOf("?") >= 0 || str.indexOf("/") >= 0) {
//			return R.falseState("不能包含空格或特殊字符`~!@#$%^&*()-+=|\\}]{[;:'\",.<>?/");
//		}
//		return R.trueState("");
//	}
	
	public static void main(String[] args) {
		String str = "S_sdfsd";
		String first = str.substring(0, 1);
		System.out.println(first);
		System.out.println(first.matches("[a-zA-Z]{1}"));
	}
	
	public static String createLibName(String user_id) {
		return user_id.replaceAll("-", "");
	}
}
