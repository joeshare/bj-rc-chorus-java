package cn.rongcapital.chorus.modules.processor.collect_job_info.dao;

import cn.rongcapital.chorus.modules.processor.collect_job_info.entity.ChorusModule;
import org.apache.ibatis.annotations.Select;

/**
 * Created by abiton on 11/08/2017.
 */
public interface ChorusModuleRepository {
    @Select({"select module_name as moduleName,module_view_name as moduleViewName from xd_module"
        ," where module_name = #{moduleName}"})
    ChorusModule findByModuleName(String moduleName);
}
