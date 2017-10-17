package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.ColumnInfoV2;
import cn.rongcapital.chorus.das.entity.TableInfoV2;
import org.apache.atlas.AtlasServiceException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author fuxiangli
 *
 */
public interface ColumnInfoServiceV2 {
    /**
     * 1.根据表ID,列ID,查询字段信息
     *
     * @param columnEntityGuid
     * @return
     */
	@Nullable ColumnInfoV2 getColumnInfo(String columnEntityGuid);
    /**
     * 2.根据表ID,查询字段信息
     *
     * @param tableEntityGuid
     * @return
     */
    List<ColumnInfoV2> selectColumnInfo(String tableEntityGuid);

    @Nonnull Map<String,List<ColumnInfoV2>> columnsOfTables(Collection<String> tablesUnique) throws AtlasServiceException;

    /**
     * 根据tableinof中的columns信息查询column的信息
     * @param tableInfo
     * @return
     */
    List<ColumnInfoV2> selectColumnInfoByTableEntity(TableInfoV2 tableInfo) throws AtlasServiceException;
}
