package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.dao.PlatformResourceSnapshotMapper;
import cn.rongcapital.chorus.das.entity.PlatformResourceSnapshot;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * Created by Athletics on 2017/7/20.
 */
public class PlatformResourceSnapshotMapperTest {

    SqlSessionFactory sqlSessionFactory;

    @Before
    public void before() throws IOException {
        Reader reader = Resources.getResourceAsReader("MapperConfig.xml");
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        reader.close();
    }

    @Test
    public void getUseRateTrendTest(){
        SqlSession sqlSession = sqlSessionFactory.openSession();
        PlatformResourceSnapshotMapper mapper = sqlSession.getMapper(PlatformResourceSnapshotMapper.class);
        List<PlatformResourceSnapshot> list = mapper.getUseRateTrend("2017-06-01");
        if(CollectionUtils.isNotEmpty(list)){
            System.out.println("======" + list.size());
            for(PlatformResourceSnapshot vo : list){
                System.out.println(vo.getAppliedCpu());
                System.out.println(vo.getPlatformCpu());
                System.out.println(vo.getAppliedMemory());
                System.out.println(vo.getPlatformMemory());
                System.out.println(vo.getAppliedStorage());
                System.out.println(vo.getPlatformStorage());
                System.out.println(vo.getSnapshotDate());
            }
        }
    }
}
