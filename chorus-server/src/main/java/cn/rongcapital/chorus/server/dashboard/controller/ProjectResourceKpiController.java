package cn.rongcapital.chorus.server.dashboard.controller;

import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.common.hadoop.QuotaEnum;
import cn.rongcapital.chorus.common.util.OrikaBeanMapper;
import cn.rongcapital.chorus.das.entity.ProjectResourceKpiSnapshotVO;
import cn.rongcapital.chorus.das.service.ProjectResourceKpiSnapshotService;
import cn.rongcapital.chorus.server.dashboard.vo.ProjectResourceKpiVO;
import cn.rongcapital.chorus.server.vo.ResultVO;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hhlfl on 2017-7-20.
 */
@Slf4j
@RestController
@RequestMapping(value = {"/dashboard/platform"})
public class ProjectResourceKpiController {

    private final static String MAPKEY_KPI_DATE = "kpiDate";

    private final static String MAPKEY_ORDER_COLUMN_NAME = "orderColumnName";

    private final static String MAPKEY_ORDER_SORT = "orderSort";

    @Autowired
    private ProjectResourceKpiSnapshotService projectResourceKpiSnapshotService;

    @ApiOperation(value = "项目资源KPI查询")
    @ApiResponses({
            @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"),
            @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误")
    })
    @RequestMapping(value = {"project/kpi/{pageNum}/{pageSize}"}, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResultVO<PageInfo<ProjectResourceKpiVO>> queryYesterdayProjectResourceKpi(@PathVariable("pageNum") int pageNum, @PathVariable("pageSize") int pageSize){
        return queryYesterdayProjectResourceKpiWithOrder(pageNum, pageSize, 0, 0);
    }

    @ApiOperation(value = "项目资源KPI查询(排序)")
    @ApiResponses({
            @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"),
            @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误")
    })
    @RequestMapping(value = {"project/kpi/{pageNum}/{pageSize}/{orderType}/{orderSort}"}, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResultVO<PageInfo<ProjectResourceKpiVO>> queryYesterdayProjectResourceKpiWithOrder(@PathVariable("pageNum") int pageNum, @PathVariable("pageSize") int pageSize,
                                                                                              @PathVariable("orderType") int orderType, @PathVariable("orderSort") int orderSort){
        int percent100 = 100;
        ResultVO resultVO = ResultVO.error();
        try {
            if(pageNum<1 || pageSize<1){
                log.warn("pageNum and pageSize must greater than 0!");
            }
            Date yestDate = new Date(getYesterday().getTime());
            List<ProjectResourceKpiSnapshotVO> projectResourceKpiSnapshots = null;

            // 不排序
            if (orderType == 0) {
                projectResourceKpiSnapshots = projectResourceKpiSnapshotService.selectByKpiDate(yestDate,pageNum,pageSize);
            } else {
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put(MAPKEY_KPI_DATE, String.format("'%s'", new SimpleDateFormat("yyyy-MM-dd").format(yestDate)));
                switch (orderType) {
                    case 1: // 按CPU利用率排序
                        paramMap.put(MAPKEY_ORDER_COLUMN_NAME, "cpu_usage");
                        break;
                    case 2: // 按内存利用率排序
                        paramMap.put(MAPKEY_ORDER_COLUMN_NAME, "memory_usage");
                        break;
                    case 3: // 按存储利用率排序
                        paramMap.put(MAPKEY_ORDER_COLUMN_NAME, "storage_usage");
                        break;
                    case 4: // 按数据总量排序
                        paramMap.put(MAPKEY_ORDER_COLUMN_NAME, "storage_used");
                        break;
                    case 5: // 按日增数据量排序
                        paramMap.put(MAPKEY_ORDER_COLUMN_NAME, "data_daily_incr");
                        break;
                    case 6: // 按任务成功率排序
                        paramMap.put(MAPKEY_ORDER_COLUMN_NAME, "task_success_rate");
                        break;
                    default:
                        paramMap.put(MAPKEY_ORDER_COLUMN_NAME, "cpu_usage");
                        break;
                }

                switch (orderSort) {
                    case 0: // 升序
                        paramMap.put(MAPKEY_ORDER_SORT, "asc");
                        break;
                    case 1: // 降序
                        paramMap.put(MAPKEY_ORDER_SORT, "desc");
                        break;
                    default:
                        paramMap.put(MAPKEY_ORDER_SORT, "asc");
                        break;
                }
                projectResourceKpiSnapshots = projectResourceKpiSnapshotService.selectByKpiDateWithOrder(paramMap,pageNum,pageSize);
            }

            List<ProjectResourceKpiVO> vos = new ArrayList<>();
            //////             如果查询数据存在
            if (projectResourceKpiSnapshots != null) {
                for (ProjectResourceKpiSnapshotVO item : projectResourceKpiSnapshots) {
                    ProjectResourceKpiVO kpiVO = OrikaBeanMapper.INSTANCE.map(item, ProjectResourceKpiVO.class);
                    int scale = 2;
                    kpiVO.setCpuUsage(precision(kpiVO.getCpuUsage()*percent100, scale));
                    kpiVO.setMemoryUsage(precision(kpiVO.getMemoryUsage()*percent100, scale));
                    kpiVO.setStorageUsage(precision(kpiVO.getStorageUsage()*percent100,scale));
                    kpiVO.setTaskSuccessRate(precision(kpiVO.getTaskSuccessRate()*percent100, scale));
                    vos.add(kpiVO);
                }
            }

            PageInfo pageInfo = new PageInfo<>(projectResourceKpiSnapshots);
            pageInfo.setList(vos);
            resultVO = ResultVO.success(pageInfo);
        } catch (ServiceException se) {
            log.error(se.getMessage(), se);
            return ResultVO.error(se);
        }  catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            resultVO = ResultVO.error();
        }
        return resultVO;
    }

    private String formatStorage(long size){
        String result = "";
        double r=size;
        long v = 1;
        String unitName=QuotaEnum.B.name();
        if(size>=QuotaEnum.T.getValue()){
            v=QuotaEnum.T.getValue();
            unitName=QuotaEnum.T.name();
        }else if(size>= QuotaEnum.G.getValue()){
            v=QuotaEnum.G.getValue();
            unitName=QuotaEnum.G.name();
        }else if(size>=QuotaEnum.M.getValue()){
            v=QuotaEnum.M.getValue();
            unitName=QuotaEnum.M.name();
        }else if(size>=QuotaEnum.K.getValue()){
            v=QuotaEnum.K.getValue();
            unitName=QuotaEnum.K.name();
        }

        BigDecimal big =new BigDecimal(r/v);
        r=big.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        return String.format("%s%s", r, unitName);
    }

    private double precision(double v,int precision){
        BigDecimal big =new BigDecimal(v);
        return big.setScale(precision,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    
    private Date getYesterday(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR,-1);
        return calendar.getTime();
    }
}
