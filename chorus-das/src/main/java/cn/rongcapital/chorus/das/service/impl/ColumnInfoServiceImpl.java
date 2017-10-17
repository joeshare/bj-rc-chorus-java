package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.das.dao.ColumnManageMapper;
import cn.rongcapital.chorus.das.entity.ColumnInfo;
import cn.rongcapital.chorus.das.service.ColumnInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据管理-列基本信息模块SERVICE实现类
 *
 * @author fuxiangli
 */

@Service(value = "ColumnInfoService")

public class ColumnInfoServiceImpl implements ColumnInfoService{
    @Autowired
    private ColumnManageMapper colinfomap;
	/**
	 * 1.根据表ID,列ID,查询字段信息
	 *
	 * @param tableId
	 * @param columnId
	 * @return
	 */
	@Override
	public ColumnInfo selectColumnInfo(Long tableId,Long columnId) {
        ColumnInfo colinfo=colinfomap.selectByPrimaryKey(tableId, columnId);
		return colinfo;
	}

	/**
	 * 2.根据表ID,查询字段信息
	 *
	 * @param tableId
	 * @return
	 */
	@Override
	public List<ColumnInfo> selectColumnInfo(Long tableId) {
		List<ColumnInfo> colinfo=colinfomap.selectByTableId(tableId);
		return colinfo;
	}
	/**
	 * 3.插入列信息
	 *
	 * @param columnInfoList
	 * @return
	 */
	@Override
	public int bulkInsert(List<ColumnInfo> columnInfoList) {
		return colinfomap.bulkInsert(columnInfoList);
	}
}
