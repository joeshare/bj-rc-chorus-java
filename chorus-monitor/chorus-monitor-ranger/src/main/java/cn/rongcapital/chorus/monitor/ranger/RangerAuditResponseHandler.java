package cn.rongcapital.chorus.monitor.ranger;

import org.apache.solr.client.solrj.response.QueryResponse;

public interface RangerAuditResponseHandler {

    void handleResponse(QueryResponse response);

}
