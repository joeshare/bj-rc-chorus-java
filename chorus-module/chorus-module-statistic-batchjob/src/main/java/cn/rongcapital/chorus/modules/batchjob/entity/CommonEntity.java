package cn.rongcapital.chorus.modules.batchjob.entity;

import java.util.Date;

public class CommonEntity {

	public static final String HIVE_TABLE_CODE_DEFAULT = "t";

	/**
	 * 创建用户ID
	 */
	private String createUser;

	/**
	 * 数据插入时间
	 */
	private Date createTime;

	/**
	 * 更新者用户ID
	 */
	private String updateUser;

	/**
	 * 数据更新时间
	 */
	private Date updateTime;

	/**
	 * 描述
	 */
	private String remark;

	/**
	 * 是否可用（Y/N）
	 */
	private String useYn;

	/**
	 * 数据插入时间显示值
	 */
	private String createTimeLabel;

	/**
	 * 数据插入时间显示值(显示到秒)
	 */
	private String createTimeLabelSecond;

	/**
	 * 创建用户ID
	 */
	public String getCreateUser() {
		return createUser;
	}

	/**
	 * 创建用户ID
	 */
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	/**
	 * 更新者用户ID
	 */
	public String getUpdateUser() {
		return updateUser;
	}

	/**
	 * 更新者用户ID
	 */
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	/**
	 * 描述
	 */
	public String getRemark() {
		if (remark == null) {
			remark = "";
		}
		return remark;
	}

	/**
	 * 描述
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * 是否可用（Y/N）
	 */
	public String getUseYn() {
		return useYn;
	}

	/**
	 * 是否可用（Y/N）
	 */
	public void setUseYn(String useYn) {
		this.useYn = useYn;
	}

	/**
	 * 数据插入时间
	 */
	public Date getCreateTime() {
		if (createTime == null) {
			createTime = new Date();
		}
		return createTime;
	}

	/**
	 * 数据插入时间
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * 数据更新时间
	 */
	public Date getUpdateTime() {
		if (updateTime == null) {
			updateTime = new Date();
		}
		return updateTime;
	}

	/**
	 * 数据更新时间
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getCreateTimeLabel() {
		return createTimeLabel;
	}

	public void setCreateTimeLabel(String createTimeLabel) {
		this.createTimeLabel = createTimeLabel;
	}

	public String getCreateTimeLabelSecond() {
		return createTimeLabelSecond;
	}

	public void setCreateTimeLabelSecond(String createTimeLabelSecond) {
		this.createTimeLabelSecond = createTimeLabelSecond;
	}

}
