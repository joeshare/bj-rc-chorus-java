package cn.rongcapital.chorus.common.util;
/**
 * 菜单类型
 * @author lipeng
 *
 * 2016年3月29日
 */
public enum MenuType {
	
	MENU("M","菜单"), BTN("B","按钮"), BTN_H("BH","横向按钮"),BTN_V("BV","纵向按钮");
	
	private String typeCode;
	private String type;
	MenuType(String code,String type){
		this.setTypeCode(code);
		this.setType(type);
	}
	/**
	 * 根据菜单类型提取名称
	 * @param index
	 * @return
	 */
	public static String getName(String index) {
        for (MenuType c : MenuType.values()) {
            if (c.getTypeCode().equals(index)) {
                return c.type;
            }
        }
        return "";
    }
	public String getTypeCode() {
		return typeCode;
	}
	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
