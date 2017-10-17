package cn.rongcapital.chorus.das.dao;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import cn.rongcapital.chorus.das.entity.XDExecution;
import cn.rongcapital.chorus.das.xd.dao.XDMapper;

public class XDMapperTest {
    SqlSessionFactory sqlSessionFactory;

    @Before
    public void before() throws IOException {
        Reader reader = Resources.getResourceAsReader("MapperConfigxd.xml");
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        reader.close();
    }
    
    @Test
    public void testgetJobExecutions() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        XDMapper mapper = sqlSession.getMapper(XDMapper.class);
        final List<XDExecution> jobExecutions = mapper.getJobExecutions("chorus_xJqhhJrZcZRRxiqMgisv1504577441147",
                Arrays.asList("STARTING", "STARTED", "STOPPING"));
        assertNotNull(jobExecutions);
        assertEquals(1, jobExecutions.size());
    }
}
