package cn.rongcapital.chorus.das.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.das.entity.ExternalDataSourceCSVField;
import cn.rongcapital.chorus.das.entity.ExternalDataSourceJsonField;
import cn.rongcapital.chorus.das.entity.ExternalDataSourceRDBField;
import cn.rongcapital.chorus.das.entity.ExternalDataSourceRDBTable;
import cn.rongcapital.chorus.das.service.ExternalDataSourceService;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by abiton on 25/07/2017.
 */
@Slf4j
@Service("ExternalDataSourceService")
public class ExternalDataSourceServiceImpl implements ExternalDataSourceService {

    private final static int DEFAULT_LINE_COUNT = 10;

    private final static int MAX_LINE_COUNT = 100;
    
    private final static Gson gson = new Gson();

    @Autowired
    private FileSystem fileSystem;
    @Override
    public List<ExternalDataSourceRDBTable> listRDBTables(String url, String userName, String password) {
        List<ExternalDataSourceRDBTable> result = new ArrayList<>();
        try (Connection connection = getMySqlConnection(url, userName, password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SHOWTABLES);
            while (resultSet.next()) {
                ExternalDataSourceRDBTable table = new ExternalDataSourceRDBTable();
                table.setName(resultSet.getString(1));
                result.add(table);
            }

        } catch (SQLException e) {
            log.error("get connection error ", e);
            throw new ServiceException(StatusCode.SYSTEM_ERR);
        }
        return result;
    }

    @Override
    public List<ExternalDataSourceRDBField> listRDBFields(String url, String userName, String password, String table) {
        List<ExternalDataSourceRDBField> result = new ArrayList<>();
        try (Connection connection = getMySqlConnection(url, userName, password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("desc " + table);
            while (resultSet.next()) {
                ExternalDataSourceRDBField field = new ExternalDataSourceRDBField();
                field.setName(resultSet.getString(1));
                String type = resultSet.getString(2).toUpperCase();
                if (type.indexOf("(") > 0) {
                    field.setType(type.substring(0, type.indexOf("(")));
                    field.setLength((type.substring(type.indexOf("(") + 1, type.indexOf(")"))));
                } else {
                    field.setType(type);
                    field.setLength("0");
                }
                result.add(field);
            }
        } catch (SQLException e) {
            log.error("get connection error ", e);
            throw new ServiceException(StatusCode.SYSTEM_ERR);
        }
        return result;
    }

    @Override
    public List<ExternalDataSourceCSVField> listCSVFields(String csvFilePath, String dwUserName, String hasTitle) {
        List<ExternalDataSourceCSVField> result = new ArrayList<>();
        FSDataInputStream fsr;
        BufferedReader bufferedReader = null;
        String lineTxt;
        String firstLine = "";
        try {
            String finalCsvFilePath = findRealPath(csvFilePath);
            if (fileSystem.exists(new Path(finalCsvFilePath))) {
                fsr = fileSystem.open(new Path(finalCsvFilePath));
                bufferedReader = new BufferedReader(new InputStreamReader(fsr));

                // 读取第一行
                if ((lineTxt = bufferedReader.readLine()) != null) {
                    firstLine = lineTxt;
                }
            }
        } catch (Exception e) {
            log.error("read csv error ", e);
            throw new ServiceException(StatusCode.SYSTEM_ERR);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    log.error("buffer reader close error");
                }
            }
        }

        // 分割第一行
        String[] titles = firstLine.split(",", -1);
        int colSize = titles.length;

        // 没有表头
        if ("NOT_HAVE".equals(hasTitle)) {
            // 根据csv列的index创建对应的hive表的列
            for (int i = 1;i <= colSize;i++) {
                ExternalDataSourceCSVField field = new ExternalDataSourceCSVField();
                field.setName("col" + i);
                result.add(field);
            }
        } else { // 有表头
            // 根据csv的表头创建对应的hive表的列
            for (int i = 0;i < colSize;i++) {
                ExternalDataSourceCSVField field = new ExternalDataSourceCSVField();
                field.setName(titles[i]);
                result.add(field);
            }
        }

        return result;
    }

    @Override
    public String getCSVSample(String csvFilePath, String dwUserName, String hasTitle, Integer displayLineCount) {
        FSDataInputStream fsr;
        BufferedReader bufferedReader = null;
        String lineTxt;
        String sampleTxt = "";
        try {
            String finalCsvFilePath = findRealPath(csvFilePath);
            if (fileSystem.exists(new Path(finalCsvFilePath))) {
                fsr = fileSystem.open(new Path(finalCsvFilePath));
                bufferedReader = new BufferedReader(new InputStreamReader(fsr));

                int readCount;
                int currentCount = 0;
                if (displayLineCount == null) {
                    readCount = DEFAULT_LINE_COUNT;
                } else if (displayLineCount > MAX_LINE_COUNT) {
                    readCount = MAX_LINE_COUNT;
                } else {
                    readCount = displayLineCount;
                }

                // 有表头多读一行
                if ("HAVE".equals(hasTitle)) {
                    readCount++;
                }

                // 读取第一行
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    currentCount++;
                    sampleTxt += (lineTxt + "<br/>");
                    if (currentCount >= readCount) {
                        break;
                    }

                }
            }
        } catch (Exception e) {
            log.error("read csv error ", e);
            throw new ServiceException(StatusCode.SYSTEM_ERR);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    log.error("buffer reader close error");
                }
            }
        }

        return sampleTxt;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<ExternalDataSourceJsonField> listJsonFields(String filePath, String dwUserName) {
        List<ExternalDataSourceJsonField> result = new ArrayList<>();
        FSDataInputStream fsr;
        BufferedReader bufferedReader = null;
        String lineTxt;
        String firstLine = "";
        try {
            if (fileSystem.exists(new Path(filePath))) {
                Path p = new Path(filePath);
                FileStatus[] files = fileSystem.listStatus(p);
                if(files != null && files.length > 0){
                    for (FileStatus fileStatus : files) {
                        if(fileStatus.isFile() && !fileStatus.getPath().toString().endsWith("_SUCCESS")){
                            fsr = fileSystem.open(fileStatus.getPath());
                            bufferedReader = new BufferedReader(new InputStreamReader(fsr));
    
                            // 读取第一行
                            if ((lineTxt = bufferedReader.readLine()) != null) {
                                firstLine = lineTxt;
                            }
                            break;
                        }
                    }
                    
                    LinkedTreeMap data = null;
                    try {
                        data = gson.fromJson(firstLine, LinkedTreeMap.class);
                    } catch (Exception e) {
                        log.error("parse json file firstLine error : {}", firstLine, e);
                        throw new ServiceException(StatusCode.SYSTEM_ERR);
                    }
                    
                    data.keySet().stream().forEach(column -> {
                        ExternalDataSourceJsonField field = new ExternalDataSourceJsonField();
                        field.setName((String)column);
                        result.add(field);
                    });
                }
            }
        } catch (Exception e) {
            log.error("read json file error ", e);
            throw new ServiceException(StatusCode.SYSTEM_ERR);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    log.error("buffer reader close error");
                }
            }
        }

        return result;
    }

    @Override
    public String getJsonSample(String filePath, String dwUserName, Integer displayLineCount) {
        FSDataInputStream fsr;
        BufferedReader bufferedReader = null;
        String lineTxt;
        String sampleTxt = "";
        try {
            if (fileSystem.exists(new Path(filePath))) {
                fsr = fileSystem.open(new Path(filePath));
                bufferedReader = new BufferedReader(new InputStreamReader(fsr));

                int readCount;
                int currentCount = 0;
                if (displayLineCount == null) {
                    readCount = DEFAULT_LINE_COUNT;
                } else if (displayLineCount > MAX_LINE_COUNT) {
                    readCount = MAX_LINE_COUNT;
                } else {
                    readCount = displayLineCount;
                }

                // 读取第一行
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    currentCount++;
                    sampleTxt += (lineTxt + "<br/>");
                    if (currentCount >= readCount) {
                        break;
                    }

                }
            }
        } catch (Exception e) {
            log.error("read json file error ", e);
            throw new ServiceException(StatusCode.SYSTEM_ERR);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    log.error("buffer reader close error");
                }
            }
        }

        return sampleTxt;
    }

    private String findRealPath(String csvFilePath) throws IOException {
        String realPath = csvFilePath;
        if (csvFilePath != null && csvFilePath.contains("$systemDate")) {
            String reg = "^([\\s\\S]*)/([\\s\\S]*\\$systemDate)";
            Pattern p = Pattern.compile(reg);
            Matcher m = p.matcher(csvFilePath);
            if(m.find()) {
                String folder = m.group(1);
                FileStatus[] fileStatuses = fileSystem.listStatus(new Path(folder));
                for (FileStatus fileStatus : fileStatuses) {
                    String tmpPath = csvFilePath.replace(m.group(2), fileStatus.getPath().getName());
                    if (fileSystem.exists(new Path(tmpPath))) {
                        realPath = tmpPath;
                        break;
                    }
                }
            }
        }
        return realPath;
    }

    private Connection getMySqlConnection(String url, String userName, String password) throws SQLException {
        if (StringUtils.isNotBlank(url)) {
            return DriverManager.getConnection(url, userName, password);
        } else {
            throw new SQLException("url should not be null");
        }
    }

    public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver"; // 驱动
    public static final String SHOWTABLES = "show tables"; //

    static {
        try {
            Class.forName(MYSQL_DRIVER);
        } catch (ClassNotFoundException e) {
            log.error("mysql driver not found", e);
        }
    }
}
