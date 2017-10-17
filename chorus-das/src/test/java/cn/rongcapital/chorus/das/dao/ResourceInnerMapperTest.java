package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.common.util.NumUtils;
import cn.rongcapital.chorus.das.dao.ResourceInnerMapper;
import cn.rongcapital.chorus.das.entity.ResourceInner;
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
import java.util.Map;

/**
 * Created by abiton on 24/11/2016.
 */
public class ResourceInnerMapperTest {

    SqlSessionFactory sqlSessionFactory;

    @Before
    public void before() throws IOException {
        Reader reader = Resources.getResourceAsReader("MapperConfig.xml");
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        reader.close();
    }

    @Test
    public void insert(){
        ResourceInner resourceInner = new ResourceInner();
        resourceInner.setCreateTime(new Date());
        resourceInner.setCreateUserId("somebody");
        resourceInner.setProjectId(1l);
        resourceInner.setResourceCpu(10);
        resourceInner.setResourceMemory(10);
        resourceInner.setResourceStorage(100);
        resourceInner.setStatusCode("1000");

        SqlSession sqlSession = sqlSessionFactory.openSession();
        ResourceInnerMapper mapper = sqlSession.getMapper(ResourceInnerMapper.class);
        mapper.insert(resourceInner);
        System.out.println(resourceInner.getResourceInnerId());

    }

    @Test
    public void queryUseRateTest(){
        SqlSession sqlSession = sqlSessionFactory.openSession();
        ResourceInnerMapper mapper = sqlSession.getMapper(ResourceInnerMapper.class);
        Map<String, Object> map = mapper.queryUseRate();
        if(map!=null){
            System.out.println(NumUtils.percent(((BigDecimal)map.get("cpu")).longValue(),100L));
            System.out.println(NumUtils.percent(((BigDecimal)map.get("memory")).longValue(),100L));
            System.out.println(NumUtils.percent(((BigDecimal)map.get("storage")).longValue(),100L));
        }
    }

    @Test
    public void queryCountNumTest(){
        SqlSession sqlSession = sqlSessionFactory.openSession();
        ResourceInnerMapper mapper = sqlSession.getMapper(ResourceInnerMapper.class);
        int result = mapper.queryCountNum();
        System.out.println(result);
    }
}
