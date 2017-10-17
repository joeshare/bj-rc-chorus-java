package cn.rongcapital.chorus.server.datadev.controller;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.constant.XdModuleConstants;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.common.util.OrikaBeanMapper;
import cn.rongcapital.chorus.common.xd.XDClient;
import cn.rongcapital.chorus.das.entity.XdModule;
import cn.rongcapital.chorus.das.entity.XdModuleDO;
import cn.rongcapital.chorus.das.entity.web.XdModuleCause;
import cn.rongcapital.chorus.das.service.XdModuleService;
import cn.rongcapital.chorus.server.datadev.controller.vo.XDModuleVO;
import cn.rongcapital.chorus.server.vo.ResultVO;
import cn.rongcapital.chorus.das.entity.web.R;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.xd.rest.client.SpringXDOperations;
import org.springframework.xd.rest.domain.ModuleDefinitionResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hhlfl on 2017-5-22.
 *
 */
@Slf4j
@Controller
@RequestMapping("/xd_module")
public class XdModuleUploadController {
    private static Logger logger = LoggerFactory.getLogger(XdModuleUploadController.class);

    /**
     * Spring XD Module信息
     */
    @Autowired
    private XdModuleService xdModuleService = null;
    @Autowired
    private XDClient xdClient;

    /**
     * Spring XD Module 上传
     *
     * @throws IOException
     */
    @CrossOrigin
    @RequestMapping(value = {"/upload"}, method = RequestMethod.POST)
    @ResponseBody
    public ResultVO<String> upload(@RequestParam("moduleViewName") String moduleName,
                                   @RequestParam(value="moduleAliasName",required=false) String moduleAliasName,
                                   @RequestParam("moduleLevel") Integer moduleLevel,
                                   @RequestParam("projectId") Long projectId,
                                   @RequestParam("moduleCategory") Integer moduleCategory,
                                   @RequestParam("moduleType") Integer moduleType,
                                   @RequestParam(value="remark",required=false) String remark,
                                   @RequestParam("userName") String userName,
                                   @RequestParam("userId") String userId,
                                   @RequestParam("file") MultipartFile file) throws IOException {
        ResultVO resultVO = ResultVO.error();

        try {
            if(file == null || file.isEmpty()){
                log.error("上传文件不能为空！");
                return new ResultVO<String>(StatusCode.XD_MODULE_UPLOAD_ERROR,"上传文件不能为空!");
            }

            String fileName = file.getOriginalFilename();
            int suffixIndex = fileName.lastIndexOf(".");
            String suffix = "";
            if(suffixIndex !=-1){
                suffix = fileName.substring(suffixIndex,fileName.length());
                fileName = fileName.substring(0,suffixIndex);
            }

            if(!".jar".equals(suffix.toLowerCase())){
                log.error("文件类型必须为jar文件！");
                return new ResultVO<String>(StatusCode.XD_MODULE_UPLOAD_ERROR,"上传文件必须为jar文件!");
            }

            /* 封装对象并校验 */
            XdModule xdModule = create(moduleName,moduleAliasName,moduleLevel,projectId,moduleCategory,moduleType,remark,userName,userId,file.getOriginalFilename());

//            boolean exists = xdModuleService.moduleExists(xdModule);
//            if(exists){
//               throw new ServiceException(StatusCode.XD_MODULE_UPLOAD_EXISTS);
//            }


            /* 上传文件 */
            SpringXDOperations springXDTemplate = xdClient.getSpringXDTemplate();
            File tmpFile = null;
            try {
                long t0 = System.currentTimeMillis();

                tmpFile = File.createTempFile(fileName,suffix);
                file.transferTo(tmpFile);
                if(log.isDebugEnabled()) {
                    log.debug(String.format("文件成功上传到临时路径[%s]。耗时[%s]ms", tmpFile.getPath(), (System.currentTimeMillis()-t0)));
                }

                Resource resource = new FileSystemResource(tmpFile);
                ModuleDefinitionResource mdResource = springXDTemplate.moduleOperations().uploadModule(xdModule.getModuleName(), XdModuleConstants.XdModuleTypeConstants.getModuleTypeByInt(moduleType)
                        , resource, true);
                if (mdResource == null)
                    throw new ServiceException(StatusCode.XD_MODULE_UPLOAD_ERROR);

                if(log.isDebugEnabled()){
                    log.debug(String.format("组件成功上传到spring xd. 总耗时[%s]ms",(System.currentTimeMillis()-t0)));
                }

            }catch(IOException iex){
                log.error("module upload failed.", iex);
                throw new ServiceException(StatusCode.XD_MODULE_UPLOAD_ERROR,iex);
            }finally {
                if(tmpFile != null)
                    tmpFile.delete();
            }

            /* 存入本地数据库 */
            xdModuleService.saveOrUpdate(xdModule);
//            if(!isSucess) {
//                throw new Exception("插入或更新本地数据库的Module信息失败");
//            }

            resultVO = ResultVO.success();
            log.info(String.format("module[%s_%s_%s] upload success.", projectId,moduleName, moduleType));
        } catch (ServiceException se) {
            String msg = String.format("module[%s_%s_%s] upload exception.", projectId,moduleName,moduleType);
            logger.error(msg, se);
            return ResultVO.error(se);
        }  catch (Exception ex) {
            String msg = String.format("module[%s_%s_%s] upload exception.", projectId,moduleName,moduleType);
            logger.error(msg, ex);
            resultVO = ResultVO.error();
        }
        return resultVO;
    }

    /**
     * Spring XD Module信息查询
     *
     * @throws IOException
     */
    @RequestMapping(value = {"/getXdModules/{pageNum}/{pageSize}"}, method = RequestMethod.POST)
    @ResponseBody
    public ResultVO<PageInfo> getXdModulesByName(@PathVariable int pageNum, @PathVariable int pageSize,
                                                 @RequestBody XdModuleCause cause) throws IOException {
        ResultVO resultVO = ResultVO.error();
        List<XDModuleVO> list = new ArrayList<XDModuleVO>();
        try {
            if(pageNum<1 || pageSize<1){
                log.warn("pageNum and pageSize must greater than 0!");
//                PageInfo<XDModuleVO> pageInfo = new PageInfo<XDModuleVO>(null);
//                return resultVO.success(pageInfo);
            }
            cause.setSearchKey(cause.getModuleAliasName());
            List<XdModuleDO> xdModuleList = xdModuleService.getModuleListByPage(cause, pageNum,pageSize);
            Page<XDModuleVO> page = new Page<>();
//             如果查询数据存在
            if (xdModuleList != null) {
                if(xdModuleList instanceof Page){
                    Page oldPage = (Page) xdModuleList;
                    page.setTotal(oldPage.getTotal());
                    page.setEndRow(oldPage.getEndRow());
                    page.setPageNum(oldPage.getPageNum());
                    page.setPageSize(oldPage.getPageSize());
                    page.setPages(oldPage.getPages());
                    page.setStartRow(oldPage.getStartRow());
                    page.setOrderBy(oldPage.getOrderBy());
                }

                // 获取用户数据
                for (XdModuleDO item : xdModuleList) {
                    XDModuleVO xdModuleVO = OrikaBeanMapper.INSTANCE.map(item, XDModuleVO.class);
                    page.add(xdModuleVO);

                }
            }

            PageInfo<XDModuleVO> pageInfo = new PageInfo<XDModuleVO>(page);
            resultVO = ResultVO.success(pageInfo);
        } catch (ServiceException se) {
            logger.error(se.getMessage(), se);
            return ResultVO.error(se);
        }  catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            resultVO = ResultVO.error();
        }
        return resultVO;
    }



    private XdModule create(String moduleName,
                              String moduleAliasName,
                              Integer moduleLevel,
                              Long projectId,
                              Integer moduleCategory,
                              Integer moduleType,
                              String remark,
                              String userName,
                              String userId,
                              String fileName){

        XdModule xdModule = new XdModule();
        xdModule.setModuleAliasName(moduleAliasName);
        xdModule.setModuleViewName(moduleName);
        xdModule.setModuleName(String.format("%s_%s",moduleName, projectId));
        xdModule.setFileName(fileName);
        xdModule.setModuleCategory(moduleCategory);
        xdModule.setRemark(remark);
        xdModule.setModuleLevel(moduleLevel);
        xdModule.setModuleType(moduleType);
        xdModule.setCreateUser(userId);
        xdModule.setCreateTime(new Date());
        xdModule.setCreateUserName(userName);
        xdModule.setUseYn("Y");
        xdModule.setProjectId(projectId);

        return  xdModule;
    }

}
