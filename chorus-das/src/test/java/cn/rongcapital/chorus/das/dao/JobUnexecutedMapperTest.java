package cn.rongcapital.chorus.das.dao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import cn.rongcapital.chorus.das.entity.JobUnexecutedDO;
import cn.rongcapital.chorus.das.entity.UnexecutedJob;

/**
 * CommonInfoMapper测试
 * @author kevin.gong
 * @Time 2017年9月19日 下午2:49:09
 */
public class JobUnexecutedMapperTest {

    SqlSessionFactory sqlSessionFactory;

    @Before
    public void before() throws IOException {
        Reader reader = Resources.getResourceAsReader("MapperConfig.xml");
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        reader.close();
    }
    
    @Test
    public void testMethod(){
        SqlSession sqlSession = sqlSessionFactory.openSession();
        JobUnexecutedMapper mapper = sqlSession.getMapper(JobUnexecutedMapper.class);
        JobUnexecutedDO jobUnexecutedDO1 = new JobUnexecutedDO();
        jobUnexecutedDO1.setJobName("jn1");
        jobUnexecutedDO1.setScheExecTime("2017-09-11 00:00:00");
        jobUnexecutedDO1.setRerunFlag(0);
        jobUnexecutedDO1.setNoticeFlag(0);
        mapper.insert(jobUnexecutedDO1);
        
        JobUnexecutedDO jobUnexecutedDO2 = new JobUnexecutedDO();
        jobUnexecutedDO2.setJobName("jn2");
        jobUnexecutedDO2.setScheExecTime("2017-09-11 01:00:00");
        jobUnexecutedDO2.setRerunFlag(0);
        jobUnexecutedDO2.setNoticeFlag(0);
        mapper.insert(jobUnexecutedDO2);
        
        JobUnexecutedDO jobUnexecutedDO3 = new JobUnexecutedDO();
        jobUnexecutedDO3.setJobName("jn3");
        jobUnexecutedDO3.setScheExecTime("2017-09-11 02:00:00");
        jobUnexecutedDO3.setRerunFlag(1);
        jobUnexecutedDO3.setNoticeFlag(0);
        mapper.insert(jobUnexecutedDO3);
        
        JobUnexecutedDO jobUnexecutedDO4 = new JobUnexecutedDO();
        jobUnexecutedDO4.setJobName("jn4");
        jobUnexecutedDO4.setScheExecTime("2017-09-11 03:00:00");
        jobUnexecutedDO4.setRerunFlag(0);
        jobUnexecutedDO4.setNoticeFlag(1);
        mapper.insert(jobUnexecutedDO4);
        
        assertEquals(3, mapper.selectWaitExecuteJobsCount());
        
        List<JobUnexecutedDO> list = mapper.selectJobUnexecuted(0, 0);
        assertEquals(3, list.size());
        assertEquals("jn1", list.get(0).getJobName());
        assertEquals("jn2", list.get(1).getJobName());
        assertEquals("jn4", list.get(2).getJobName());
        assertEquals("2017-09-11 00:00:00", list.get(0).getScheExecTime());
        assertEquals("2017-09-11 01:00:00", list.get(1).getScheExecTime());
        
        List<UnexecutedJob> wjlist = mapper.selectWaitExecuteJobs(0, 2);
        assertEquals(2, wjlist.size());
        assertEquals("2017-09-11 03:00:00", wjlist.get(0).getScheExecTime());
        assertEquals("2017-09-11 01:00:00", wjlist.get(1).getScheExecTime());
        
        wjlist = mapper.selectWaitExecuteJobs(2, 2);
        assertEquals(1, wjlist.size());
        assertEquals("2017-09-11 00:00:00", wjlist.get(0).getScheExecTime());
        
        assertEquals(1, mapper.updateWaitExecuteJobStatus("jn2"));
        wjlist = mapper.selectWaitExecuteJobs(0, 20);
        assertEquals(2, wjlist.size());
        assertEquals("2017-09-11 03:00:00", wjlist.get(0).getScheExecTime());
        assertEquals("2017-09-11 00:00:00", wjlist.get(1).getScheExecTime());
    }
    
}
