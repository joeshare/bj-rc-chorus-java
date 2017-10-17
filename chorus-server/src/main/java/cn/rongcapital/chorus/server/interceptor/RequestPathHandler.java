package cn.rongcapital.chorus.server.interceptor;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import cn.rongcapital.chorus.common.constant.CommonAttribute;
import cn.rongcapital.chorus.das.service.PlatformMaintenanceService;
import lombok.extern.slf4j.Slf4j;

/**
 * 请求地址拦截器
 * @author kevin.gong
 * @Time 2017年9月20日 下午3:28:52
 */
@Slf4j
@Component
public class RequestPathHandler extends HandlerInterceptorAdapter {
    
    /**
     * 维护时允许调用的路径
     */
    private List<String> allowPath = new ArrayList<>();
    
    {
        allowPath.add("/dashboard/platform");
        allowPath.add("/resource/apply");
        allowPath.add("/resource/left");
        allowPath.add("/resource/approve");
        allowPath.add("/xd_module/upload");
        allowPath.add("/xd_module/getXdModules");
        allowPath.add("/platform/maintenance");
        allowPath.add("/swagger-resources");
        allowPath.add("/v2/api-docs");
    }

    private PlatformMaintenanceService platformMaintenanceService;
    
    @Autowired
    public void setPlatformMaintenanceService(PlatformMaintenanceService platformMaintenanceService) {
        this.platformMaintenanceService = platformMaintenanceService;
    }

    /**
     * 前置检查
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if(platformMaintenanceService.getPlatformMaintenanceStatus() == CommonAttribute.MAINTENANCE_STATUS_MAINTAINED) {
            String url = request.getRequestURI();
            boolean allowFlag = false;
            for (String path : allowPath) {
                if(url.startsWith(path)) {
                    allowFlag = true;
                    break;
                }
            }
            
            if(!allowFlag) {
                log.info("平台维护中,路径:{}无法访问!", url);
                response.setHeader("Content-type","application/json;charset=UTF-8");
                String data = "{\"code\":\"8000\",\"msg\":\"平台已维护\",\"data\":null}";
                OutputStream stream = response.getOutputStream();
                stream.write(data.getBytes("UTF-8"));
                return false;
            }
        }
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
    }
}
