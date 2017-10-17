package cn.rongcapital.chorus.das.service;

import java.util.List;

import cn.rongcapital.chorus.das.entity.ExternalDataSourceCSVField;
import cn.rongcapital.chorus.das.entity.ExternalDataSourceJsonField;
import cn.rongcapital.chorus.das.entity.ExternalDataSourceRDBField;
import cn.rongcapital.chorus.das.entity.ExternalDataSourceRDBTable;

/**
 * Created by abiton on 25/07/2017.
 */
public interface ExternalDataSourceService {
    List<ExternalDataSourceRDBTable> listRDBTables(String url,String userName,String password);
    List<ExternalDataSourceRDBField> listRDBFields(String url,String userName,String password,String table);
    List<ExternalDataSourceCSVField> listCSVFields(String csvFilePath, String dwUserName, String hasTitle);
    String getCSVSample(String csvFilePath, String dwUserName, String hasTitle, Integer displayLineCount);
    List<ExternalDataSourceJsonField> listJsonFields(String filePath, String dwUserName);
    String getJsonSample(String filePath, String dwUserName, Integer displayLineCount);
}
