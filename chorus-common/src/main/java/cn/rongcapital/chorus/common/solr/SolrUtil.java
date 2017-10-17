package cn.rongcapital.chorus.common.solr;

import cn.rongcapital.chorus.common.solr.query.SearchCriteria;
import cn.rongcapital.chorus.common.solr.query.SearchField;
import cn.rongcapital.chorus.common.solr.query.SearchField.SEARCH_TYPE;
import cn.rongcapital.chorus.common.solr.query.SortField;
import cn.rongcapital.chorus.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.params.CursorMarkParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Slf4j
public class SolrUtil {

    SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss'Z'");

    public SolrUtil() {
    }

    public QueryResponse runQuery(SolrClient solrClient, SolrQuery solrQuery) {
        if (solrQuery != null) {
            QueryResponse response;
            try {
                response = solrClient.query(solrQuery, METHOD.POST);
                return response;
            } catch (Throwable e) {
                log.error("Error from Solr server.", e);
            }
        }
        return null;
    }

    public QueryResponse searchResources(SearchCriteria searchCriteria,
                                         List<SearchField> searchFields, List<SortField> sortFieldList, List<String> facetPovitFields,
                                         SolrClient solrClient) {
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        if (searchCriteria.getParamList() != null) {
            // For now assuming there is only date field where range query will
            // be done. If we there are more than one, then we should create a
            // hashmap for each field name
            Date fromDate = null;
            Date toDate = null;
            String dateFieldName = null;

            for (SearchField searchField : searchFields) {
                Object paramValue = searchCriteria.getParamValue(searchField
                        .getClientFieldName());
                if (paramValue == null || paramValue.toString().isEmpty()) {
                    continue;
                }
                String fieldName = searchField.getFieldName();
                if (paramValue instanceof Collection) {
                    String fq = orList(fieldName, (Collection<?>) paramValue);
                    if (fq != null) {
                        query.addFilterQuery(fq);
                    }
                } else if (searchField.getDataType() == SearchField.DATA_TYPE.DATE) {
                    if (!(paramValue instanceof Date)) {
                        log.error("Search file is not of java object instanceof Date");
                    } else {
                        if (searchField.getSearchType() == SEARCH_TYPE.GREATER_EQUAL_THAN
                                || searchField.getSearchType() == SEARCH_TYPE.GREATER_THAN) {
                            fromDate = (Date) paramValue;
                            dateFieldName = fieldName;
                        } else if (searchField.getSearchType() == SEARCH_TYPE.LESS_EQUAL_THAN
                                || searchField.getSearchType() == SEARCH_TYPE.LESS_THAN) {
                            toDate = (Date) paramValue;
                        }
                    }
                } else if (searchField.getSearchType() == SearchField.SEARCH_TYPE.GREATER_EQUAL_THAN
                        || searchField.getSearchType() == SEARCH_TYPE.GREATER_THAN
                        || searchField.getSearchType() == SEARCH_TYPE.LESS_EQUAL_THAN
                        || searchField.getSearchType() == SEARCH_TYPE.LESS_THAN) { //NOPMD
                    // TODO: Need to handle range here
                } else {
                    String fq = setField(fieldName, paramValue);

                    if (searchField.getSearchType() == SEARCH_TYPE.PARTIAL) {
                        fq = setFieldForPartialSearch(fieldName, paramValue);
                    }

                    if (fq != null) {
                        query.addFilterQuery(fq);
                    }
                }
            }
            if (fromDate != null || toDate != null) {
                String fq = setDateRange(dateFieldName, fromDate, toDate);
                if (fq != null) {
                    query.addFilterQuery(fq);
                }
            }
        }
        setFacetPivot(facetPovitFields, query);
        setSortClause(searchCriteria, sortFieldList, query);
        setCursorMark(searchCriteria,query);
        query.setStart(searchCriteria.getStartIndex());
        query.setRows(searchCriteria.getMaxRows());
        // Fields to get
        // query.setFields("myClassType", "id", "score", "globalId");
        if (log.isDebugEnabled()) {
            log.debug("SOLR QUERY=" + query.toString());
        }
        QueryResponse response = runQuery(solrClient, query);

        if (response == null || response.getStatus() != 0) {
            log.error("Error running query. query=" + query.toString()
                    + ", response=" + response);
        }
        return response;
    }

    private void setCursorMark(SearchCriteria searchCriteria, SolrQuery query) {
        if (searchCriteria.getCursorMark() != null) {
            query.set(CursorMarkParams.CURSOR_MARK_PARAM, searchCriteria.getCursorMark());
        }
    }

    private void setFacetPivot(List<String> facetPovitFields, SolrQuery query) {
        if (facetPovitFields != null && facetPovitFields.size() > 0) {
            query.addFacetPivotField(facetPovitFields.toArray(new String[]{}));
        }
    }

    private String setFieldForPartialSearch(String fieldName, Object value) {
        if (value == null || value.toString().trim().length() == 0) {
            return null;
        }
        return fieldName + ":*" + ClientUtils.escapeQueryChars(value.toString().trim().toLowerCase()) + "*";
    }

    public String setField(String fieldName, Object value) {
        if (value == null || value.toString().trim().length() == 0) {
            return null;
        }
        return fieldName
                + ":"
                + ClientUtils.escapeQueryChars(value.toString().trim()
                .toLowerCase());
    }

    public String setDateRange(String fieldName, Date fromDate, Date toDate) {
        String fromStr = "*";
        String toStr = "NOW";
        if (fromDate != null) {
            fromStr = dateFormat.format(fromDate);
        }
        if (toDate != null) {
            toStr = dateFormat.format(toDate);
        }
        return fieldName + ":[" + fromStr + " TO " + toStr + "]";
    }

    public String orList(String fieldName, Collection<?> valueList) {
        if (valueList == null || valueList.size() == 0) {
            return null;
        }
        String expr = "";
        int count = -1;
        for (Object value : valueList) {
            count++;
            if (count > 0) {
                expr += " OR ";
            }
            expr += fieldName
                    + ":"
                    + ClientUtils.escapeQueryChars(value.toString()
                    .toLowerCase());
        }
        if (valueList.size() == 0) {
            return expr;
        } else {
            return "(" + expr + ")";
        }

    }

    public String andList(String fieldName, Collection<?> valueList) {
        if (valueList == null || valueList.size() == 0) {
            return null;
        }
        String expr = "";
        int count = -1;
        for (Object value : valueList) {
            count++;
            if (count > 0) {
                expr += " AND ";
            }
            expr += fieldName
                    + ":"
                    + ClientUtils.escapeQueryChars(value.toString()
                    .toLowerCase());
        }
        if (valueList.size() == 0) {
            return expr;
        } else {
            return "(" + expr + ")";
        }
    }

    public void setSortClause(SearchCriteria searchCriteria,
                              List<SortField> sortFields, SolrQuery query) {
        if (sortFields == null)
            return;

        // TODO: We are supporting single sort field only for now
        String sortBy = searchCriteria.getSortBy();
        String querySortBy = null;
        if (!StringUtils.isEmpty(sortBy)) {
            sortBy = sortBy.trim();
            for (SortField sortField : sortFields) {
                if (sortBy.equalsIgnoreCase(sortField.getParamName())) {
                    querySortBy = sortField.getFieldName();
                    // Override the sortBy using the normalized value
                    searchCriteria.setSortBy(sortField.getParamName());
                    break;
                }
            }
        }

        if (querySortBy == null) {
            for (SortField sortField : sortFields) {
                if (sortField.isDefault()) {
                    querySortBy = sortField.getFieldName();
                    // Override the sortBy using the default value
                    searchCriteria.setSortBy(sortField.getParamName());
                    searchCriteria.setSortType(sortField.getDefaultOrder()
                            .name());
                    break;
                }
            }
        }

        if (querySortBy != null) {
            // Add sort type
            String sortType = searchCriteria.getSortType();
            ORDER order = ORDER.asc;
            if (sortType != null && sortType.equalsIgnoreCase("desc")) {
                order = ORDER.desc;

            }
            query.addSort(querySortBy, order);
        }
    }

    // Utility methods
    public int toInt(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value.toString().isEmpty()) {
            return 0;
        }
        try {
            return Integer.valueOf(value.toString());
        } catch (Throwable t) {
            log.error("Error converting value to integer. value=" + value, t);
        }
        return 0;
    }

    public long toLong(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value.toString().isEmpty()) {
            return 0;
        }
        try {
            return Long.valueOf(value.toString());
        } catch (Throwable t) {
            log.error("Error converting value to long. value=" + value, t);
        }
        return 0;
    }

    public Date toDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Date) {
            return (Date) value;
        }
        try {
            // TODO: Do proper parsing based on Solr response value
            return new Date(value.toString());
        } catch (Throwable t) {
            log.error("Error converting value to date. value=" + value, t);
        }
        return null;
    }
}
