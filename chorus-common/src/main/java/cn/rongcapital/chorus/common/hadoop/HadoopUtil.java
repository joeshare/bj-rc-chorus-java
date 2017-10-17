package cn.rongcapital.chorus.common.hadoop;

import cn.rongcapital.chorus.common.util.StringUtils;

public class HadoopUtil {
    public static final String PATH_SPLITER = "/";
    public static final String PROJECT_BASE_DIR = "/chorus/project/";

    public static String formatPath(String dir) {
        if (StringUtils.isEmpty(dir)) {
            return null;
        }
        return PROJECT_BASE_DIR.concat(dir);
    }

    /**
     * @param parent
     * @param folder
     * @return
     * @author yunzhong
     * @time 2017年6月22日下午2:45:33
     */
    public static String appendPath(String parent, String folder) {
        if (StringUtils.isEmpty(parent) || StringUtils.isEmpty(folder)) {
            return null;
        }
        String path = parent;
        if (!parent.endsWith(PATH_SPLITER)) {
            path = parent.concat("/");
        }
        return path.concat(folder);
    }
}
