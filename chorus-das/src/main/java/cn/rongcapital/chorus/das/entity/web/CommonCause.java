package cn.rongcapital.chorus.das.entity.web;

/**
 * 公用查询条件BEAN(所有查询条件BEAN都继承这个类)
 * 
 * @author HANS
 *
 */
public class CommonCause {
	/**
	 * 查询页数(从1开始)
	 */
	private Integer page;

	/**
	 * 每行显示记录数
	 */
	private static final int rows = 10;

	/**
	 * 页面显示条数(临时对应，全局更改画面显示记录可变时删除)
	 */
	private Integer rowCnt;

	/**
	 * 查询开始index(供LIMIT语句用)
	 */
	private Integer startIndex;

	/**
	 * 查询关键字
	 */
	private String searchKey;

	/**
	 * 查询页数(从1开始)
	 */
	public Integer getPage() {
		return page;
	}

	/**
	 * 查询页数(从1开始)
	 */
	public void setPage(Integer page) {
		this.page = page;
	}

	/**
	 * 每行显示记录数(供LIMIT语句用)
	 */
	public static int getRows() {
		return rows;
	}

	/**
	 * 查询开始index(供LIMIT语句用)
	 */
	public Integer getStartIndex() {
		if (page == null) {
			return null;
		}
		if (rowCnt == null || rowCnt == 0) {
			startIndex = (page - 1) * rows;
		} else {
			startIndex = (page - 1) * rowCnt;
		}
		return startIndex;
	}

	/**
	 * 查询关键字
	 */
	public String getSearchKey() {
		if (searchKey != null && !"".equals(searchKey.trim())) {
			return "%" + searchKey + "%";
		}
		return searchKey;
	}

	/**
	 * 查询关键字
	 */
	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	public Integer getRowCnt() {
		if (rowCnt == null || rowCnt == 0) {
			rowCnt = rows;
		}
		return rowCnt;
	}

	public void setRowCnt(Integer rowCnt) {
		this.rowCnt = rowCnt;
	}

}
