package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.das.entity.TreeNode;
import cn.rongcapital.chorus.das.service.impl.TableLinageServiceV2Impl;
import cn.rongcapital.chorus.governance.AtlasService;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.atlas.model.instance.AtlasEntityHeader;
import org.apache.atlas.model.lineage.AtlasLineageInfo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by Athletics on 2017/8/11.
 */
public class TableLinageServiceV2Test {

    @InjectMocks
    private TableLinageServiceV2Impl tableLinageServiceV2;

    @Mock
    private AtlasService atlasService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getLineageByTableInfoIdTest() throws AtlasServiceException {
        String tableGuid = "b30f7dad-86af-4074-afcf-5deff3fcbccf";
        AtlasLineageInfo atlasLineageInfo = new AtlasLineageInfo();
        atlasLineageInfo.setBaseEntityGuid("b30f7dad-86af-4074-afcf-5deff3fcbccf");
        atlasLineageInfo.setLineageDirection(AtlasLineageInfo.LineageDirection.INPUT);
        atlasLineageInfo.setLineageDepth(5);

        Map<String, AtlasEntityHeader> map = new HashMap<>();
        AtlasEntityHeader header1 = new AtlasEntityHeader();
        header1.setGuid("c3bf817b-2bf2-495c-8438-ded36bc924b0");
        header1.setStatus(AtlasEntity.Status.ACTIVE);
        header1.setDisplayText("db_entity_06");
        header1.setTypeName("testDBType");
        header1.setAttribute("owner", "user1");
        header1.setAttribute("qualifiedName", "1502359669701");
        header1.setAttribute("unique", "db06");
        header1.setAttribute("name", "db_entity_06");
        header1.setAttribute("description", "testdatabaseentity");
        map.put("c3bf817b-2bf2-495c-8438-ded36bc924b0", header1);

        AtlasEntityHeader header2 = new AtlasEntityHeader();
        header2.setGuid("468a4b01-80e5-43da-bc68-b26d56c55714");
        header2.setAttribute("name", "process03");
        map.put("468a4b01-80e5-43da-bc68-b26d56c55714", header2);

        AtlasEntityHeader header3 = new AtlasEntityHeader();
        header3.setGuid("7a723c0e-7adf-40be-8e4e-6095353d2eaf");
        header3.setAttribute("name", "process02");
        map.put("7a723c0e-7adf-40be-8e4e-6095353d2eaf", header3);

        AtlasEntityHeader header4 = new AtlasEntityHeader();
        header4.setGuid("b3b15cd4-dff2-4cf9-8678-70391c974bbc");
        header4.setAttribute("name", "db_entity_04");
        map.put("b3b15cd4-dff2-4cf9-8678-70391c974bbc", header4);

        AtlasEntityHeader header5 = new AtlasEntityHeader();
        header5.setGuid("c54a00ac-4311-4533-baf5-875c9cfca6dc");
        header5.setAttribute("name", "process04");
        map.put("c54a00ac-4311-4533-baf5-875c9cfca6dc", header5);

        AtlasEntityHeader header6 = new AtlasEntityHeader();
        header6.setGuid("6804a098-6bb3-48a0-b12f-8ced05ce1897");
        header6.setAttribute("name", "db_entity_02");
        map.put("6804a098-6bb3-48a0-b12f-8ced05ce1897", header6);

        AtlasEntityHeader header7 = new AtlasEntityHeader();
        header7.setGuid("b30f7dad-86af-4074-afcf-5deff3fcbccf");
        header7.setAttribute("name", "db_entity_03");
        map.put("b30f7dad-86af-4074-afcf-5deff3fcbccf", header7);

        AtlasEntityHeader header8 = new AtlasEntityHeader();
        header8.setGuid("3ab1f74c-e8ba-4d77-984b-1545a771b792");
        header8.setAttribute("name", "db_entity_09");
        map.put("3ab1f74c-e8ba-4d77-984b-1545a771b792", header8);

        AtlasEntityHeader header9 = new AtlasEntityHeader();
        header9.setGuid("f4e92b85-c29b-4a29-846f-be24118d8992");
        header9.setAttribute("name", "db_entity_10");
        map.put("f4e92b85-c29b-4a29-846f-be24118d8992", header9);

        atlasLineageInfo.setGuidEntityMap(map);

        Set<AtlasLineageInfo.LineageRelation> set = new HashSet<>();
        AtlasLineageInfo.LineageRelation re1 = new AtlasLineageInfo.LineageRelation();
        re1.setFromEntityId("b30f7dad-86af-4074-afcf-5deff3fcbccf");
        re1.setToEntityId("b3b15cd4-dff2-4cf9-8678-70391c974bbc");
        set.add(re1);

        AtlasLineageInfo.LineageRelation re2 = new AtlasLineageInfo.LineageRelation();
        re2.setFromEntityId("b3b15cd4-dff2-4cf9-8678-70391c974bbc");
        re2.setToEntityId("6804a098-6bb3-48a0-b12f-8ced05ce1897");
        set.add(re2);

        AtlasLineageInfo.LineageRelation re3 = new AtlasLineageInfo.LineageRelation();
        re3.setFromEntityId("c54a00ac-4311-4533-baf5-875c9cfca6dc");
        re3.setToEntityId("b30f7dad-86af-4074-afcf-5deff3fcbccf");
        set.add(re3);

        AtlasLineageInfo.LineageRelation re4 = new AtlasLineageInfo.LineageRelation();
        re4.setFromEntityId("3ab1f74c-e8ba-4d77-984b-1545a771b792");
        re4.setToEntityId("b30f7dad-86af-4074-afcf-5deff3fcbccf");
        set.add(re4);

        AtlasLineageInfo.LineageRelation re5 = new AtlasLineageInfo.LineageRelation();
        re5.setFromEntityId("7a723c0e-7adf-40be-8e4e-6095353d2eaf");
        re5.setToEntityId("c3bf817b-2bf2-495c-8438-ded36bc924b0");
        set.add(re5);

        AtlasLineageInfo.LineageRelation re6 = new AtlasLineageInfo.LineageRelation();
        re6.setFromEntityId("6804a098-6bb3-48a0-b12f-8ced05ce1897");
        re6.setToEntityId("468a4b01-80e5-43da-bc68-b26d56c55714");
        set.add(re6);

        AtlasLineageInfo.LineageRelation re7 = new AtlasLineageInfo.LineageRelation();
        re7.setFromEntityId("468a4b01-80e5-43da-bc68-b26d56c55714");
        re7.setToEntityId("b30f7dad-86af-4074-afcf-5deff3fcbccf");
        set.add(re7);

        AtlasLineageInfo.LineageRelation re8 = new AtlasLineageInfo.LineageRelation();
        re8.setFromEntityId("c3bf817b-2bf2-495c-8438-ded36bc924b0");
        re8.setToEntityId("b30f7dad-86af-4074-afcf-5deff3fcbccf");
        set.add(re8);

        AtlasLineageInfo.LineageRelation re9 = new AtlasLineageInfo.LineageRelation();
        re9.setFromEntityId("f4e92b85-c29b-4a29-846f-be24118d8992");
        re9.setToEntityId("c3bf817b-2bf2-495c-8438-ded36bc924b0");
        set.add(re9);

        AtlasLineageInfo.LineageRelation re10 = new AtlasLineageInfo.LineageRelation();
        re10.setFromEntityId("6804a098-6bb3-48a0-b12f-8ced05ce1897");
        re10.setToEntityId("3ab1f74c-e8ba-4d77-984b-1545a771b792");
        set.add(re10);

        AtlasLineageInfo.LineageRelation re11 = new AtlasLineageInfo.LineageRelation();
        re11.setFromEntityId("6804a098-6bb3-48a0-b12f-8ced05ce1897");
        re11.setToEntityId("c3bf817b-2bf2-495c-8438-ded36bc924b0");
        set.add(re11);

        AtlasLineageInfo.LineageRelation re12 = new AtlasLineageInfo.LineageRelation();
        re12.setFromEntityId("6804a098-6bb3-48a0-b12f-8ced05ce1897");
        re12.setToEntityId("c54a00ac-4311-4533-baf5-875c9cfca6dc");
        set.add(re12);

        atlasLineageInfo.setRelations(set);

        when(atlasService.getLineageByGuid(anyString(),any(),anyInt())).thenReturn(atlasLineageInfo);
        TreeNode treeNode = tableLinageServiceV2.getLineageByTableInfoId(tableGuid);
        System.out.println(treeNode.toString());
        assertThat(treeNode).isNotNull();
        assertEquals(treeNode.getNodeId(), "b30f7dad-86af-4074-afcf-5deff3fcbccf");

//        String tableGuid2 = "967e8353-3533-45cf-9046-e5623f2e4a37";
//        atlasLineageInfo.setBaseEntityGuid(tableGuid2);
//        atlasLineageInfo.setRelations(null);
//        atlasLineageInfo.setLineageDirection(null);
//        when(atlasService.getLineageByGuid(anyString(), any(), anyInt())).thenReturn(atlasLineageInfo);
//
//        AtlasEntity atlasEntity = new AtlasEntity();
//        atlasEntity.setGuid(tableGuid2);
//        atlasEntity.setAttribute("name","testb");
//        when(atlasService.getByGuid(any())).thenReturn(atlasEntity);
//        TreeNode treeNode2 = tableLinageServiceV2.getLineageByTableInfoId(tableGuid);
//        System.out.println(treeNode2.toString());
//        assertThat(treeNode2).isNotNull();
//        assertEquals(treeNode2.getNodeName(),"testb");
    }
}
