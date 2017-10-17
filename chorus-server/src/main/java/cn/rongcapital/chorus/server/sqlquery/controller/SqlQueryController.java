package cn.rongcapital.chorus.server.sqlquery.controller;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.PermissionDeniedException;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.das.entity.CalculateSQL;
import cn.rongcapital.chorus.das.entity.ColumnInfoV2;
import cn.rongcapital.chorus.das.entity.ExecuteHistory;
import cn.rongcapital.chorus.das.entity.JobPropertyV2;
import cn.rongcapital.chorus.das.entity.JobTreeNode;
import cn.rongcapital.chorus.das.entity.JobTreeNodeState;
import cn.rongcapital.chorus.das.entity.ProjectInfoDO;
import cn.rongcapital.chorus.das.entity.TableAuthorityDOV2;
import cn.rongcapital.chorus.das.entity.TableAuthorityProperty;
import cn.rongcapital.chorus.das.entity.TableInfoV2;
import cn.rongcapital.chorus.das.entity.web.ExecuteHistoryCause;
import cn.rongcapital.chorus.das.service.ColumnInfoServiceV2;
import cn.rongcapital.chorus.das.service.ProjectMemberMappingService;
import cn.rongcapital.chorus.das.service.SqlQueryService;
import cn.rongcapital.chorus.das.service.TableAuthorityServiceV2;
import cn.rongcapital.chorus.das.service.TableInfoServiceV2;
import cn.rongcapital.chorus.server.vo.ResultVO;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 即席查询模块CONTROLLER
 * @author maboxiao
 *
 */
@Slf4j
@RestController
@RequestMapping(value = "/sqlQuery")
public class SqlQueryController {

    @Autowired
	private SqlQueryService service;
	
    @Autowired
    private TableAuthorityServiceV2 tableAuthorityServiceV2;

    @Autowired
    private ProjectMemberMappingService projectMemberMappingService;
    @Autowired
    private TableInfoServiceV2 tableInfoServiceV2;
    @Autowired
    private ColumnInfoServiceV2 columnInfoServiceV2;

    @RequestMapping(value = "/tables/{userId}", method = RequestMethod.GET)
    public ResultVO<List<NodeVO>> authorizedData(@PathVariable @Nonnull String userId, @RequestParam(required = false) String parent, @RequestParam(required = false) String parentType) {
        try {
            if (StringUtils.isBlank(parent) && StringUtils.isBlank(parentType)) {
                List<ProjectInfoDO> authorizedProjects = projectMemberMappingService.getProjectByUser(userId);
                List<Long> authorizedProjectsId = authorizedProjects.parallelStream().map(ProjectInfoDO::getProjectId).collect(Collectors.toList());
                List<ProjectInfoDO> projectsOfAuthorizedTables = tableAuthorityServiceV2.projectsOfAuthorizedTables(userId);
                //如果用户先申请了某个项目的table权限，而后有被加入项目管理员获得所有表的权限，此处project可能出现多个
                //so, do clean
                projectsOfAuthorizedTables = projectsOfAuthorizedTables.parallelStream()
                                                                       .filter(e -> !authorizedProjectsId.contains(e.getProjectId()))
                                                                       .collect(Collectors.toList());
                ArrayList<NodeVO> data2 = Lists.newArrayList();
                data2.addAll(ofProject(authorizedProjects, NodeVO.PROJECT_AUTHORIZED));
                data2.addAll(ofProject(projectsOfAuthorizedTables, NodeVO.PROJECT_TABLE_AUTHORIZED));
                return ResultVO.success(data2);
            }
            if (StringUtils.equals(parentType, NodeVO.PROJECT_AUTHORIZED)) {
                Long projectId = Long.valueOf(parent);
                authorization(userId, projectId);
                List<TableInfoV2> allTables = Lists.newArrayList();
                int pageNum = 1;
                int pageSize = 100;
                while (true) {
                    List<TableInfoV2> tableInfoV2s = tableInfoServiceV2.listAllTableInfo(projectId, pageNum, pageSize);
                    if (CollectionUtils.isNotEmpty(tableInfoV2s)) allTables.addAll(tableInfoV2s);
                    if (CollectionUtils.isEmpty(tableInfoV2s) || tableInfoV2s.size() < pageSize) break;
                    pageNum++;
                }
                return ResultVO.success(ofTable(allTables, NodeVO.AUTHORIZED_TABLE));
            } else if (StringUtils.equals(parentType, NodeVO.PROJECT_TABLE_AUTHORIZED)) {
                Long projectId = Long.valueOf(parent);
                List<TableAuthorityDOV2> authorities = tableAuthorityServiceV2.tablesOfProjectAndUser(projectId, userId);
                return ResultVO.success(ofAppliedTable(authorities, NodeVO.APPLIED_TABLE));
            } else if (StringUtils.equals(parentType, NodeVO.AUTHORIZED_TABLE)) {
                List<ColumnInfoV2> columnInfoV2s = columnInfoServiceV2.selectColumnInfo(parent);
                return ResultVO.success(ofAuthorizedColumn(columnInfoV2s, NodeVO.AUTHORIZED_COLUMNS));
            } else if (StringUtils.equals(parentType, NodeVO.APPLIED_TABLE)) {
                List<TableAuthorityDOV2> authorityDOV2s = tableAuthorityServiceV2.columnsOfTable(userId, parent);
                return ResultVO.success(ofAppliedColumn(authorityDOV2s, NodeVO.APPLIED_COLUMNS));
            }
            log.info("query param not support {}-{},return empty result", parent, parentType);
            return ResultVO.success();
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            return ResultVO.error(new ServiceException(StatusCode.SYSTEM_ERR, e));
        }
    }

    private List<NodeVO> ofAuthorizedColumn(List<ColumnInfoV2> columnInfoV2s, String type) {
        if (CollectionUtils.isEmpty(columnInfoV2s)) {
            log.debug("no columns of {}", type);
            return ImmutableList.of();
        }
        return columnInfoV2s.parallelStream()
                             .map(columnInfoV2 -> NodeVO.builder()
                                                         .code(columnInfoV2.getColumnInfoId())
                                                         .text(columnInfoV2.getColumnName())
                                                         .type(type).isLeaf(true).build())
                             .collect(Collectors.toList());
    }

    private List<NodeVO> ofAppliedColumn(List<TableAuthorityDOV2> authorityDOV2s, String type) {
        if (CollectionUtils.isEmpty(authorityDOV2s)) {
            log.debug("no columns of {}", type);
            return ImmutableList.of();
        }
        return authorityDOV2s.parallelStream()
                          .map(authorityDOV2 -> NodeVO.builder()
                                                      .code(authorityDOV2.getColumnInfoId())
                                                      .text(authorityDOV2.getColumnName())
                                                      .type(type).isLeaf(true).build())
                          .collect(Collectors.toList());
    }

    private List<NodeVO> ofAppliedTable(List<TableAuthorityDOV2> authorities, String type) {
        if (CollectionUtils.isEmpty(authorities)) {
            log.debug("no tables of {}", type);
            return ImmutableList.of();
        }
        return authorities.parallelStream()
                           .map(authorityDOV2 -> NodeVO.builder()
                                                     .code(authorityDOV2.getTableInfoId())
                                                     .text(authorityDOV2.getTableName())
                                                     .type(type).build())
                           .collect(Collectors.toList());
    }

    private List<NodeVO> ofTable(List<TableInfoV2> tableInfoV2s, String type) {
        if (CollectionUtils.isEmpty(tableInfoV2s)) {
            log.debug("no tables of {}", type);
            return ImmutableList.of();
        }
        return tableInfoV2s.parallelStream()
                           .map(tableInfoV2 -> NodeVO.builder()
                                                     .code(tableInfoV2.getTableInfoId())
                                                     .text(tableInfoV2.getTableName())
                                                     .type(type).build())
                           .collect(Collectors.toList());
    }

    private void authorization(String userId, Long projectId) throws PermissionDeniedException {
        ProjectInfoDO project = projectMemberMappingService.getProject(userId, projectId);
        if (project == null) {
            log.warn("user {} don't have the permission of project {}",userId,projectId);
            throw new PermissionDeniedException("Permission Denied");
        }

    }

    private List<NodeVO> ofProject(List<ProjectInfoDO> authorizedProjects, String type) {
        if (CollectionUtils.isEmpty(authorizedProjects)) {
            log.debug("no projects of {}", type);
            return ImmutableList.of();
        }
        return authorizedProjects.parallelStream()
                                 .map(p -> NodeVO.builder()
                                                 .code(String.valueOf(p.getProjectId()))
                                                 .text(String.format("chorus_%s", p.getProjectCode()))
                                                 .type(type).build())
                                 .collect(Collectors.toList());
    }

    /**
	 * 构造项目->表->字段树
	 *
	 * @param userId 用户ID
	 *
	 * @return 项目->表->字段树列表
	 */
	@RequestMapping(value = "/listTable/{userId}", method = RequestMethod.GET)
	public ResultVO<JobTreeNode> listTable(@PathVariable String userId) {
		List<TableAuthorityDOV2> tableAuthorityDOs = tableAuthorityServiceV2.selectByUserId(userId);
		
		// 节点信息详细
		Map<Long, JobTreeNode> projectMap = new HashMap<Long, JobTreeNode>();
		// 如果查询数据存在
		if (tableAuthorityDOs != null) {
			// 获取用户数据
			for (TableAuthorityDOV2 item : tableAuthorityDOs) {
				// 项目节点属性声明
				JobTreeNode projectTreeNode = projectMap.get(item.getProjectId());
				if (projectTreeNode == null) {
					projectTreeNode = new JobTreeNode();
					projectMap.put(item.getProjectId(), projectTreeNode);
					// 节点ID设定
					projectTreeNode.setId(String.valueOf(item.getProjectId()));
					// 节点类型设定
					// projectTreeNode.setNodeType(JobTreeNode.TYPE_PROJECT);
					// 节点父ID设定
					projectTreeNode.setPid(String.valueOf(0));
					// 节点名设定
					projectTreeNode.setText(String.format("chorus_%s", item.getProjectCode()));
					// 自定义属性(jstree ID可能重复)
					JobPropertyV2 data = new JobPropertyV2(item.getProjectId().toString(), "0", TableAuthorityProperty.TYPE_PROJECT, null);
					projectTreeNode.setData(data);
					JobTreeNodeState parentState = new JobTreeNodeState();
					// 是否打开
					parentState.setOpened(false);
					projectTreeNode.setState(parentState);
				}

				// 表节点属性声明
				JobTreeNode tableTreeNode = null;
				String tableId = "tab_" + String.valueOf(item.getTableInfoId());
				List<JobTreeNode> projectChildrenList = projectTreeNode.getChildren();
				if (projectChildrenList != null) {
					for (JobTreeNode tableNode : projectChildrenList) {
						if (tableNode.getId().equals(tableId)) {
							// 已添加
							tableTreeNode = tableNode;
							break;
						}
					}
				}
				// project未添加过
				if (tableTreeNode == null && item.getTableInfoId() != null && item.getProjectId() != null) {
					tableTreeNode = new JobTreeNode();
					// 节点ID设定
					tableTreeNode.setId(tableId);
					// 节点类型设定
					// tableTreeNode.setNodeType(JobTreeNode.TYPE_JOB);
					// 节点父ID设定
					tableTreeNode.setPid(String.valueOf(item.getProjectId()));
					// 节点名设定
					tableTreeNode.setText(item.getTableName());
					// 自定义属性(jstree ID可能重复)
					JobPropertyV2 data = new JobPropertyV2(item.getTableInfoId(), item.getProjectId().toString(), TableAuthorityProperty.TYPE_TABLE, null);
					tableTreeNode.setData(data);
					JobTreeNodeState state = new JobTreeNodeState();
					// 是否打开
					state.setOpened(false);
					tableTreeNode.setState(state);
					// 添加表节点
					projectTreeNode.addChild(tableTreeNode);
				}

				// 字段节点属性声明
				JobTreeNode columnTreeNode = null;
				String columnId = "column_" + String.valueOf(item.getColumnInfoId());
				List<JobTreeNode> columnChildrenList = tableTreeNode.getChildren();
				if (columnChildrenList != null) {
					for (JobTreeNode columnNode : columnChildrenList) {
						if (columnNode.getId().equals(columnId)) {
							// 已添加
							columnTreeNode = columnNode;
							break;
						}
					}
				}
				// project未添加过
				if (columnTreeNode == null && item.getColumnInfoId() != null && item.getTableInfoId() != null) {
					columnTreeNode = new JobTreeNode();

					// 节点ID设定
					columnTreeNode.setId(columnId);
					// 节点类型设定
					// columnTreeNode.setNodeType(JobTreeNode.TYPE_JOB);
					// 节点父ID设定
					columnTreeNode.setPid(tableTreeNode.getId());
					// 节点名设定
					columnTreeNode.setText(item.getColumnName());
					// 自定义属性(jstree ID可能重复)
					JobPropertyV2 data = new JobPropertyV2(item.getColumnInfoId(), item.getTableInfoId(), TableAuthorityProperty.TYPE_COLUMN, null);
					columnTreeNode.setData(data);
					JobTreeNodeState state = new JobTreeNodeState();
					// 是否打开
					state.setOpened(false);
					columnTreeNode.setState(state);
					// 添加字段节点
					tableTreeNode.addChild(columnTreeNode);
				}
			}
		}

		// 节点信息详细
		List<JobTreeNode> rootList = new ArrayList<JobTreeNode>();
		// 根节点内容设置
		JobTreeNode rootTreeNode = new JobTreeNode();
		// 节点ID设定
		rootTreeNode.setId(String.valueOf(0));
		// 节点类型设定
		// rootTreeNode.setNodeType(JobTreeNode.TYPE_ROOT);
		// 节点父ID设定
		rootTreeNode.setPid(null);
		// 节点名设定
		rootTreeNode.setText("数据列表");

		// 自定义属性(jstree ID可能重复)
		JobPropertyV2 data = new JobPropertyV2("0", null, TableAuthorityProperty.TYPE_ROOT, null);
		rootTreeNode.setData(data);
		JobTreeNodeState rootState = new JobTreeNodeState();
		// 是否打开
		rootState.setOpened(true);
		rootTreeNode.setState(rootState);
		rootList.add(rootTreeNode);
		for (JobTreeNode projectTreeNode : projectMap.values()) {
			rootTreeNode.addChild(projectTreeNode);
		}
//		String res = "";
//		try {
//			res = JsonUtils.convet2Json(rootTreeNode);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return ResultVO.success(rootTreeNode);
	}

	/**
	 * 执行查询
	 *
	 * @param userName 用户名
	 * @param pageNum 页数
	 * @param pageSize 每页数据量
	 * @param calculateSQL 查询SQL
	 * @return 查询结果
	 */
	@RequestMapping(value = "/queryResult/{userName}/{pageNum}/{pageSize}", method = RequestMethod.POST)
	public ResultVO<PageInfo> queryResult(@PathVariable String userName,
										  @PathVariable int pageNum,
										  @PathVariable int pageSize,
										  @RequestBody CalculateSQL calculateSQL){
		List<LinkedHashMap<String, String>> result;
		try {
			result = service.calculate(calculateSQL, userName);
		} catch (ServiceException e) {
			return ResultVO.error(e);
		} catch (Exception e1) {
			return ResultVO.error();
		}

		PageInfo page = new PageInfo();
		page.setPageNum(pageNum);
		page.setPageSize(pageSize);
		if (result != null) {
			page.setTotal(result.size());
		} else {
			page.setTotal(0);
		}

		page.setList(result);

		return ResultVO.success(page);
	}
	
	/**
	 * 获取执行历史列表
	 * @param userName 用户名
	 * @param pageNum 页数
	 * @param pageSize 每页数据量
	 * @return 执行历史列表
	 */
	@RequestMapping(value = "/listHistory/{userName}/{pageNum}/{pageSize}", method = RequestMethod.GET)
	public ResultVO<PageInfo> listHistory(@PathVariable String userName,
										  @PathVariable int pageNum,
										  @PathVariable int pageSize) {
		ExecuteHistoryCause cause = new ExecuteHistoryCause();
		cause.setUserId(userName);
		cause.setPage(pageNum);
		cause.setRowCnt(pageSize);
		List<ExecuteHistory> executeHistoryList = service.list(cause);
		int count = service.count(cause);
		PageInfo page = new PageInfo();
		page.setPageNum(pageNum);
		page.setPageSize(pageSize);
		page.setTotal(count);
		page.setList(executeHistoryList);
		return ResultVO.success(page);
	}
}
