/**
 *
 */
package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.ColumnInfoV2;
import cn.rongcapital.chorus.das.entity.TableInfoDOV2;
import cn.rongcapital.chorus.das.entity.TableInfoV2;
import org.apache.atlas.AtlasServiceException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * @author fuxiangli
 */
public interface TableInfoServiceV2 {
    
    /**
     * 1.根据表ID,查询表信息
     *
     * @param tableEntityGuid
     * @return
     */
    @Nullable
    TableInfoV2 selectByID(String tableEntityGuid);
    
    /**
     * 3.根据项目ID,查询所有表信息
     *
     * @param projectId
     * @return
     */
    List<TableInfoV2> listAllTableInfo(Long projectId, int pageNum, int pageSize);
    
    /**
     * 创建表
     *
     * @param tableInfo      表定义
     * @param columnInfoList 列定义列表
     */
    void createTable(TableInfoV2 tableInfo, List<ColumnInfoV2> columnInfoList);
    
    /**
     * 分页查询所有的hive表信息
     *
     * @param pageNum
     * @param pageSize
     * @return
     * @author yunzhong
     * @time 2017年6月21日上午11:20:22
     */
    List<TableInfoDOV2> listAllTables(int pageNum, int pageSize);
    
    /**
     * 1.内部表清单(job输入输出接口用,库名.表名)
     */
    List<TableInfoDOV2> listAllTableInfo(Long projectId);
    
    /**
     * 2.内部表清单(job输入输出接口用，表名)
     */
    List<String> listAllTableName(Long projectId);
    
    /**
     * count valid tables of project {@code projectId}
     *
     * @param projectId
     * @return
     */
    long countTables(Long projectId);
    
    /**
     * 2.根据表名称,列名称，模糊查询表信息
     * @param matchText
     * @param pageNum
     * @param pageSize        @return
     */
    List<TableInfoDOV2> searchByTableNameAndProjectNameAndProjectCode(String matchText, int pageNum, int pageSize) throws AtlasServiceException;
    
    /**
     * 根据guid批量查询表信息
     *
     * @param tableEntitiesGuid
     * @return
     */
    Map<String, TableInfoV2> selectByIds(List<String> tableEntitiesGuid);
    
    
    /***
     *根据tableName ,projectCode 作为unique
     *
     *
     * @param tableName
     * @param projectId
     * @return
     * @throws AtlasServiceException
     */
    public TableInfoV2 getAtlasByUnique(String tableName, Long projectId) throws AtlasServiceException;
    
    /**
     * @param projectIds
     * @return
     */
    @Nullable
    Map<TableInfoV2, List<ColumnInfoV2>> tableWithColumnsOfProject(@Nonnull List<Long> projectIds);
    
    /**
     * @param projectIds
     * @return
     */
    @Nullable
    Map<String, TableInfoV2> tablesOfProject(@Nonnull List<Long> projectIds);
    
    /**
     * 添加列
     *
     * @param tableInfo       表
     * @param addedColumnList 待添加列
     */
    void alterTable(TableInfoV2 tableInfo, List<ColumnInfoV2> addedColumnList);
    
    /**
     * @param tableInfoId
     * @author yunzhong
     * @time 2017年9月6日上午9:04:01
     */
    void delete(String tableInfoId) throws Exception;
}
