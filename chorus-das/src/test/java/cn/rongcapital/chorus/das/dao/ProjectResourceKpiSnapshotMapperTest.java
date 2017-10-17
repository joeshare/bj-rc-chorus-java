package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.common.util.DateUtils;
import cn.rongcapital.chorus.common.util.NumUtils;
import cn.rongcapital.chorus.das.dao.ProjectResourceKpiSnapshotMapper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Athletics on 2017/7/20.
 */
public class ProjectResourceKpiSnapshotMapperTest{

    SqlSessionFactory sqlSessionFactory;

    @Before
    public void before() throws IOException {
        Reader reader = Resources.getResourceAsReader("MapperConfig.xml");
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        reader.close();
    }

    @Test
    public void queryDataDailyIncrTotalNumByDateTest(){
        SqlSession sqlSession = sqlSessionFactory.openSession();
        ProjectResourceKpiSnapshotMapper mapper = sqlSession.getMapper(ProjectResourceKpiSnapshotMapper.class);
        Long result = mapper.queryDataDailyIncrTotalNumByDate("2017-07-30");
        System.out.println(NumUtils.byteToGb(result == null ? 0L : result));
    }

    @Test
    public void getTrendTest(){
        SqlSession sqlSession = sqlSessionFactory.openSession();
        ProjectResourceKpiSnapshotMapper mapper = sqlSession.getMapper(ProjectResourceKpiSnapshotMapper.class);
        List<Map<String, Object>> list = mapper.getTrend("2017-07-19");
        if(CollectionUtils.isNotEmpty(list)){
            for(Map<String, Object> map : list){
                for(Map.Entry<String, Object> entry : map.entrySet()){
                    System.out.println(entry.getKey());
                    System.out.println(entry.getValue());
                }
            }
            System.out.println("===========");
            for(Map<String, Object> resultMap : list){
                System.out.println(DateUtils.format((Date) resultMap.get("kpiDate"), DateUtils.FORMATER_DAY));
                System.out.println(NumUtils.byteToGb(((BigDecimal)resultMap.get("dataDailyIncrTotal")).longValue()));
            }
        }
    }
}
