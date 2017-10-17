package cn.rongcapital.chorus.das.entity;


public class JobTreeNodeState {
	/** 是否勾选 */
	private boolean selected;
	/** 是否打开 */
	private boolean opened;
	/** 是否打开 */
	private boolean disabled;

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isOpened() {
		return opened;
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
}
