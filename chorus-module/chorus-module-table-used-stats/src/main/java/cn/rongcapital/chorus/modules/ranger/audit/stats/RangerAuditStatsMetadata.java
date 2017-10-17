package cn.rongcapital.chorus.modules.ranger.audit.stats;

import org.springframework.xd.module.options.spi.Mixin;
import org.springframework.xd.module.options.spi.ModuleOption;
import org.springframework.xd.module.options.spi.page.PageElement;

import static org.springframework.xd.module.options.spi.page.PageElementType.InputText;
import static org.springframework.xd.module.options.spi.page.PageElementType.Password;

/**
 * Created by hhlfl on 2017-6-27.
 */
@Mixin({})
public class RangerAuditStatsMetadata {
    private int days;
    private String repos;
    private String dbURL;
    private String userName;
    private String password;
    private String zkHosts;
    private String collectionName;
//    private String solrURL;

    @PageElement(InputText)
    public int getDays() {
        return days;
    }

    @ModuleOption(value = " time range(day), default 7 days.", defaultValue ="7")
    public void setDays(int days) {
        this.days = days;
    }

    @PageElement(InputText)
    public String getRepos() {
        return repos;
    }

    @ModuleOption(value = "repository names config in ranger admin,separated by ',' , example: chorus_test_hive, default 'chorus_test_hive'.",defaultValue = "chorus_test_hive")
    public void setRepos(String repos) {
        this.repos = repos;
    }

    @PageElement(InputText)
    public String getDbURL() {
        return dbURL;
    }

    @ModuleOption(value="mysql db url.",defaultValue = "jdbc:mysql://{ip}:{port}/{db}?useUnicode=true&characterEncoding=utf-8&useSSL=false")
    public void setDbURL(String dbURL) {
        this.dbURL = dbURL;
    }

    @PageElement(InputText)
    public String getUserName() {
        return userName;
    }

    @ModuleOption(value="db use name.")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @PageElement(Password)
    public String getPassword() {
        return password;
    }

    @ModuleOption(value = "db password.")
    public void setPassword(String password) {
        this.password = password;
    }

    @PageElement(InputText)
    public String getZkHosts() {
        return zkHosts;
    }

    @ModuleOption(value="zk hosts.", defaultValue = "{host}:{port},{host}:{port}/{dir}")
    public void setZkHosts(String zkHosts) {
        this.zkHosts = zkHosts;
    }

    @PageElement(InputText)
    public String getCollectionName() {
        return collectionName;
    }

    @ModuleOption(value = "ranger solr audit collection name.", defaultValue = "ranger_audits")
    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

//    @PageElement(InputText)
//    public String getSolrURL() {
//        return solrURL;
//    }
//
//    @ModuleOption(value = "ranger solr url. required if zkHost is empty.")
//    public void setSolrURL(String solrURL) {
//        this.solrURL = solrURL;
//    }
}
