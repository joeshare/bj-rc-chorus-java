package cn.rongcapital.chorus.server.datadev.controller;

import cn.rongcapital.chorus.common.constant.XdModuleConstants;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.common.util.OrikaBeanMapper;
import cn.rongcapital.chorus.das.entity.XdModule;
import cn.rongcapital.chorus.das.entity.XdModuleDO;
import cn.rongcapital.chorus.das.entity.web.XdModuleCause;
import cn.rongcapital.chorus.das.service.XdModuleService;
import cn.rongcapital.chorus.server.datadev.controller.vo.XDModuleVO;
import cn.rongcapital.chorus.server.vo.ResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class XdModuleController {
	private static Logger logger = LoggerFactory.getLogger(XdModuleController.class);

	/**
	 * Spring XD Module信息
	 */
	@Autowired
	private XdModuleService xdModuleService = null;

	/**
	 * Spring XD Module信息查询
	 *
	 * @throws IOException
	 */
	@RequestMapping(value = {"/module/getXdModules"}, method = RequestMethod.POST)
	@ResponseBody
	public ResultVO<XDModuleVO> getXdModules(@RequestBody XdModuleCause cause) throws IOException {
		ResultVO resultVO = ResultVO.error();
		if (cause.getModuleType() != 0) {
			// 任务类型验证
			XdModuleConstants.XdModuleTypeConstants.getModuleTypeByInt(cause.getModuleType());
		}
		List<XDModuleVO> list = new ArrayList<XDModuleVO>();
		try {
			cause.setPage(null);
			cause.setSearchKey(cause.getModuleAliasName());
			List<XdModuleDO> xdModuleList = xdModuleService.getModuleList(cause);
			// 如果查询数据存在
			if (xdModuleList != null) {
				// 获取用户数据
				for (XdModuleDO item : xdModuleList) {
					XDModuleVO xdModuleVO = OrikaBeanMapper.INSTANCE.map(item, XDModuleVO.class);
					list.add(xdModuleVO);
				}
			}
			resultVO = ResultVO.success(list);
		} catch (ServiceException se) {
			logger.error("get xd modules error", se);
			return ResultVO.error(se);
		}  catch (Exception ex) {
        	logger.error("失败原因", ex);
			resultVO = ResultVO.error();
		}
		return resultVO;
	}
}