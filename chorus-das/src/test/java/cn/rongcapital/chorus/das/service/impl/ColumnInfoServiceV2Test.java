package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.das.entity.ColumnInfoV2;
import cn.rongcapital.chorus.das.service.ColumnInfoServiceV2;
import cn.rongcapital.chorus.das.util.BasicSpringTest;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Athletics on 2017/7/28.
 */
public class ColumnInfoServiceV2Test extends BasicSpringTest {

    @Autowired
    private ColumnInfoServiceV2 columnInfoServiceV2;

    @Test
    public void getColumnInfoTest(){
        ColumnInfoV2 columnInfoV2 = columnInfoServiceV2.getColumnInfo("18d5e981-6455-46bf-ae09-23f4f66e1437");
        System.out.println(columnInfoV2.getColumnInfoId().toString());
        assertThat(columnInfoV2).isNotNull();
    }

    @Test
    public void selectColumnInfoTest(){
        String tableEntityGuid = "e8b193e5-fad0-42ab-b983-398ca03d4ee5";
        List<ColumnInfoV2> list = columnInfoServiceV2.selectColumnInfo(tableEntityGuid);
        assertThat(list).isNotNull();
    }
}
