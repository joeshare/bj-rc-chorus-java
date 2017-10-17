package cn.rongcapital.chorus.governance.bridge;

import org.apache.atlas.AtlasServiceException;

/**
 * @author yimin
 */
public interface Bridge {
    /**
     * import customer types to atlas server
     * @throws AtlasServiceException
     */
    void importMetadata() throws AtlasServiceException;
}
