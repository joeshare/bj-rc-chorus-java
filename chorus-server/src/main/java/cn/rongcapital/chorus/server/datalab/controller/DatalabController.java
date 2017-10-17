package cn.rongcapital.chorus.server.datalab.controller;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.util.BeanUtils;
import cn.rongcapital.chorus.common.util.OrikaBeanMapper;
import cn.rongcapital.chorus.das.entity.DatalabInfo;
import cn.rongcapital.chorus.datalab.service.DatalabStatus;
import cn.rongcapital.chorus.datalab.service.LabService;
import cn.rongcapital.chorus.datalab.service.LabStatusService;
import cn.rongcapital.chorus.server.datalab.param.DatalabCreate;
import cn.rongcapital.chorus.server.datalab.vo.DatalabVO;
import cn.rongcapital.chorus.server.vo.ResultVO;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by abiton on 17/03/2017.
 */
@RestController
@Slf4j
public class DatalabController {

    @Autowired
    private LabService labService;
    @Autowired
    private LabStatusService labStatusService;

    @RequestMapping(value = "/datalab/{projectCode}", method = RequestMethod.GET)
    public ResultVO getByProject(@PathVariable String projectCode) {

        List<DatalabInfo> labsByProject = labService.getLabsByProject(projectCode);
        List<DatalabVO> result = new ArrayList<>();
        labsByProject.stream().forEach(operation -> {

                    DatalabVO vo = OrikaBeanMapper.INSTANCE.map(operation, DatalabVO.class);
                    boolean alive = this.isAlive(vo.getProjectCode(), vo.getLabCode());
                    if (alive) {
                        vo.setStatus(DatalabStatus.START);
                    } else {
                        vo.setStatus(DatalabStatus.STOP);
                    }
                    result.add(vo);
                }
        );
        return ResultVO.success(result);
    }

    @RequestMapping(value = "/datalab/{projectCode}/{pageNum}/{pageSize}", method = RequestMethod.GET)
    public ResultVO getByProject(@PathVariable String projectCode,
                                 @PathVariable int pageNum, @PathVariable int pageSize) {

        List<DatalabInfo> labsByProject = labService.getLabsByProject(projectCode, pageNum, pageSize);
        List<DatalabVO> result = new ArrayList<>();
        labsByProject.stream().forEach(operation -> {
                    DatalabVO vo = OrikaBeanMapper.INSTANCE.map(operation, DatalabVO.class);
                    boolean alive = this.isAlive(vo.getProjectCode(), vo.getLabCode());
                    if (alive) {
                        vo.setStatus(DatalabStatus.START);
                    } else {
                        vo.setStatus(DatalabStatus.STOP);
                    }
                    result.add(vo);
                }
        );

        PageInfo page = new PageInfo(labsByProject);
        page.setList(result);

        return ResultVO.success(page);
    }

    @RequestMapping(value = "/datalab", method = RequestMethod.POST)
    public ResultVO createDatalab(@RequestBody  DatalabCreate create) {
        DatalabInfo datalabInfo = new DatalabInfo();
        try {
            BeanUtils.copyProperties(datalabInfo, create);
            labService.createLab(datalabInfo);
        } catch (IllegalAccessException e) {
            log.error("create lab error ", e);
            return ResultVO.error(StatusCode.DATALAB_CREATE_ERROR);
        } catch (InvocationTargetException e) {
            log.error("create lab error ", e);
            return ResultVO.error(StatusCode.DATALAB_CREATE_ERROR);
        } catch (DuplicateKeyException e) {
            log.error("create lab error ", e);
            return ResultVO.error(StatusCode.DATALAB_ALREADY_EXIST_ERROR);
        } catch (RuntimeException e) {
            log.error("create lab error ", e);
            return ResultVO.error(StatusCode.DATALAB_CREATE_ERROR);
        }
        return ResultVO.success(StatusCode.DATALAB_CREATED);
    }

    @RequestMapping(value = "/datalab/{projectCode}/{labCode}/start", method = RequestMethod.POST)
    public ResultVO startDatalab(@PathVariable String projectCode, @PathVariable String labCode) {
        try {
            labService.startLab(projectCode, labCode);
        } catch (RuntimeException e) {
            log.error("start lab error ", e);
            return ResultVO.error(StatusCode.DATALAB_START_ERROR);
        }
        return ResultVO.success(StatusCode.DATALAB_STARTED);
    }

    @RequestMapping(value = "/datalab/{projectCode}/{labCode}/stop", method = RequestMethod.POST)
    public ResultVO stopDatalab(@PathVariable String projectCode, @PathVariable String labCode) {
        try {
            labService.stopLab(projectCode, labCode);
        } catch (RuntimeException e) {
            log.error("stop lab error ", e);
            return ResultVO.error(StatusCode.DATALAB_STOP_ERROR);
        }
        return ResultVO.success(StatusCode.DATALAB_STOPPED);
    }

    @RequestMapping(value = "/datalab/{projectCode}/{labCode}/delete", method = RequestMethod.POST)
    public ResultVO deleteDatalab(@PathVariable String projectCode, @PathVariable String labCode) {
        try {
            labService.deleteLab(projectCode, labCode);
        } catch (RuntimeException e) {
            log.error("delete lab error ", e);
            return ResultVO.error(StatusCode.DATALAB_DESTROY_ERROR);
        }

        return ResultVO.success(StatusCode.DATALAB_DESTROYED);
    }

    @RequestMapping(value = "/datalab/{projectCode}/{labCode}/alive", method = RequestMethod.GET)
    public boolean isAlive(@PathVariable String projectCode, @PathVariable String labCode) {
        try {
            return labStatusService.isAlive(projectCode, labCode);
        } catch (RuntimeException e) {
            log.error("get lab status error", e);
        }
        return false;
    }
}
