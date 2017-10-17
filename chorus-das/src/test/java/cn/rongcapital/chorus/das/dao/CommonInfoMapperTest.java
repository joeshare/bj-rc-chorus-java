package cn.rongcapital.chorus.das.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import cn.rongcapital.chorus.das.entity.CommonInfoDO;

/**
 * CommonInfoMapper测试
 * @author kevin.gong
 * @Time 2017年9月19日 下午2:49:09
 */
public class CommonInfoMapperTest {

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
        CommonInfoMapper mapper = sqlSession.getMapper(CommonInfoMapper.class);
        CommonInfoDO commonInfoDO = new CommonInfoDO();
        commonInfoDO.setUserId("0");
        commonInfoDO.setAttributeId("8888");
        commonInfoDO.setValue("abc");
        assertEquals(1, mapper.insert(commonInfoDO));
        
        commonInfoDO.setValue("abcd");
        assertEquals(1, mapper.update(commonInfoDO));
        
        CommonInfoDO commonInfo = mapper.selectByUserIdAndAttrId("0", "8888");
        assertEquals("0", commonInfo.getUserId());
        assertEquals("8888", commonInfo.getAttributeId());
        assertEquals("abcd", commonInfo.getValue());
        assertNotNull(commonInfo.getCreateTime());
        assertNotNull(commonInfo.getUpdateTime());
    }
    
}
