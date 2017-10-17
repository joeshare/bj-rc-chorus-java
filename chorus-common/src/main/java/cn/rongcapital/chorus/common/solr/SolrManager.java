package cn.rongcapital.chorus.common.solr;

import cn.rongcapital.chorus.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * This class initializes Solr
 */
@Slf4j
public class SolrManager {

    static final Object lock = new Object();

    SolrClient solrClient = null;
//    Date lastConnectTime = null;
    volatile boolean initDone = false;
    private String solrURL;
    private String zkHosts;
    private String collectionName;
    public static final String DEFAULT_COLLECTION_NAME = "ranger_audits";

    public SolrManager(){}

    public SolrManager(String zkHosts, String solrURL, String collectionName){
        this.solrURL=solrURL;
        this.zkHosts=zkHosts;
        this.collectionName=collectionName;
    }

    public SolrManager(String zkHosts, String collectionName) {
        this(zkHosts,null,collectionName);

    }

    void connect() {
        if (!initDone) {
            synchronized (lock) {
                if (!initDone) {
                    if (zkHosts != null && !zkHosts.trim().equals("")
                            && !zkHosts.trim().equalsIgnoreCase("none")) {
                        zkHosts = zkHosts.trim();

//                        if (!StringUtils.isEmpty(zkDir))
//                            zkHosts = zkHosts + zkDir.trim();

                        if (collectionName == null
                                || collectionName.equalsIgnoreCase("none")) {
                            collectionName = DEFAULT_COLLECTION_NAME;
                        }

                        log.info("Solr zkHosts=" + zkHosts
                                + ", collectionName=" + collectionName);

                        try {
                            // Instantiate
                            CloudSolrClient solrCloudClient = new CloudSolrClient(
                                    zkHosts);
                            solrCloudClient
                                    .setDefaultCollection(collectionName);
                            solrClient = solrCloudClient;
                            initDone = true;
                        } catch (Throwable t) {
                            log.error(
                                    "Can't connect to Solr server. ZooKeepers="
                                            + zkHosts + ", collection="
                                            + collectionName, t);
                        }

                    } else {
                        if (solrURL == null || solrURL.isEmpty()
                                || solrURL.equalsIgnoreCase("none")) {
                            log.error("Solr ZKHosts and URL for Audit are empty. Please set ZKHosts or Solr URL");
                        } else {
                            try {
                                solrClient = new HttpSolrClient(solrURL);
                                if (solrClient == null) {
                                    log.error("Can't connect to Solr. URL="
                                            + solrURL);
                                } else {
                                    if (solrClient instanceof HttpSolrClient) {
                                        HttpSolrClient httpSolrClient = (HttpSolrClient) solrClient;
                                        httpSolrClient
                                                .setAllowCompression(true);
                                        httpSolrClient
                                                .setConnectionTimeout(1000);
                                        httpSolrClient.setMaxRetries(1);
                                        httpSolrClient
                                                .setRequestWriter(new BinaryRequestWriter());
                                    }
                                    initDone = true;
                                }

                            } catch (Throwable t) {
                                log.error(
                                        "Can't connect to Solr server. URL="
                                                + solrURL, t);
                            }
                        }
                    }
                }
            }
        }
    }

    public SolrClient getSolrClient() {
        if (solrClient == null) {
            connect();
        }
        return solrClient;
    }

}
