package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.dao.ProjectInfoDOMapper;
import cn.rongcapital.chorus.das.entity.ProjectInfo;
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
 * Created by Athletics on 2017/8/17.
 */
public class ProjectInfoDOMapperTest {

    SqlSessionFactory sqlSessionFactory;

    @Before
    public void before() throws IOException {
        Reader reader = Resources.getResourceAsReader("MapperConfig.xml");
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        reader.close();
    }

    @Test
    public void queryAllTest() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        ProjectInfoDOMapper mapper = sqlSession.getMapper(ProjectInfoDOMapper.class);
        List<ProjectInfo> list =  mapper.queryAll();
        list.stream().forEach(l -> System.out.println(l.getProjectCode()));
    }
}
