package cn.rongcapital.chorus.das.entity.xd;

/**
 * Spring xd module page info.
 *
 * @author lengyang
 */
public class Page {

	private Integer size = 9999;

	private Integer totalElements;

	private Integer totalPages = 1;
	private Integer number = 0;

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Integer getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(Integer totalElements) {
		this.totalElements = totalElements;
	}

	public Integer getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

}
