package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.dao.ApplyFormMapper;
import cn.rongcapital.chorus.das.entity.ApplyForm;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.Date;

/**
 * Created by abiton on 15/11/2016.
 */
public class ApplyFormMapperTest extends BaseTest{
    @Test
    public void insert(){
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            ApplyFormMapper mapper = sqlSession.getMapper(ApplyFormMapper.class);
            ApplyForm record = new ApplyForm();
            record.setApplyTime(new Date());
            record.setTableInfoId(1L);
            record.setApplyUserId("111");
            record.setReason("no reason");
            record.setStatusCode("100000");
            record.setTableInfoId(1L);
            mapper.insert(record);
            System.out.println(record.getApplyFormId());
            ApplyForm applyForm = mapper.selectByPrimaryKey(1L);

            System.out.println(applyForm.getApplyFormId());

            sqlSession.commit();
        } catch (Exception e){
            System.out.println(e);
        } finally {
            sqlSession.close();
        }
    }
}
