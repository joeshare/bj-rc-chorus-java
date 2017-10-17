package cn.rongcapital.chorus.common.constant;

import cn.rongcapital.chorus.common.exception.ServiceException;
import org.springframework.xd.rest.domain.RESTModuleType;

/**
 * 模块类型
 * 1:默认Job 2:用户自定义Job
 * 3:默认Stream source 4:用户自定义Stream source
 * 5:默认Stream processor 6:用自定义Stream processor
 * 7:默认Stream sink 8:用户自定义Stream sink
 * 9:默认Stream other 10:用户自定义Stream other
 * 11:job definition
 */
public class XdModuleConstants {
    public enum XdModuleTypeConstants {
        defJob(1, RESTModuleType.job),
        custJob(2, RESTModuleType.job),
        source(3, RESTModuleType.source),
        custSource(4, RESTModuleType.source),
        processor(5, RESTModuleType.processor),
        custProcessor(6, RESTModuleType.processor),
        sink(7, RESTModuleType.sink),
        custSink(8, RESTModuleType.sink),
        other(9, null),
        custOther(10, null),
        jobDefinition(11, null),
        ;

        public final int val;
        public final RESTModuleType restModuleType;

        XdModuleTypeConstants(int val, RESTModuleType restModuleType) {
            this.val = val;
            this.restModuleType = restModuleType;
        }

        public static RESTModuleType getModuleTypeByInt(int moduleTypeIntVal) {
            for (XdModuleTypeConstants xdModuleTypeConstants : XdModuleTypeConstants.values()) {
                if (xdModuleTypeConstants.val == moduleTypeIntVal &&
                        xdModuleTypeConstants.restModuleType != null)
                    return xdModuleTypeConstants.restModuleType;
            }
            throw new ServiceException(StatusCode.XD_MODULE_TYPE_ERROR);
        }
    }
}
