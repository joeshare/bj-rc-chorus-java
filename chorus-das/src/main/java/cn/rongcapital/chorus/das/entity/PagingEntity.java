package cn.rongcapital.chorus.das.entity;

/**
 * DataTables分页BEAN
 * @author maboxiao
 *
 */
public class PagingEntity extends CommonEntity {
	private int iTotalDisplayRecords;
	private Object aaData;

	public int getiTotalDisplayRecords() {
		return iTotalDisplayRecords;
	}
	public void setiTotalDisplayRecords(int iTotalDisplayRecords) {
		this.iTotalDisplayRecords = iTotalDisplayRecords;
	}
	public Object getAaData() {
		return aaData;
	}
	public void setAaData(Object aaData) {
		this.aaData = aaData;
	}
}
