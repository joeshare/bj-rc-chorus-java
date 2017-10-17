package cn.rongcapital.chorus.das.service.impl;

import static cn.rongcapital.chorus.governance.Operation.AND;
import static cn.rongcapital.chorus.governance.Operation.OR;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.apache.atlas.AtlasClientV2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;

import cn.rongcapital.chorus.authorization.api.UserDataAuthorization;
import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.das.entity.ColumnInfoV2;
import cn.rongcapital.chorus.das.entity.TableInfoDOV2;
import cn.rongcapital.chorus.das.entity.TableInfoV2;
import cn.rongcapital.chorus.das.service.AuthorizationDetailService;
import cn.rongcapital.chorus.das.service.ColumnInfoServiceV2;
import cn.rongcapital.chorus.das.service.HiveTableInfoServiceV2;
import cn.rongcapital.chorus.das.service.ProjectInfoService;
import cn.rongcapital.chorus.das.service.TableInfoServiceV2;
import cn.rongcapital.chorus.governance.AtlasService;
import cn.rongcapital.chorus.governance.AtlasServiceImpl;
import cn.rongcapital.chorus.governance.KeyValuePairBuilder;
import cn.rongcapital.chorus.governance.StringValuePairBuilder;
import cn.rongcapital.chorus.governance.autoconfigure.AtlasClientV2AutoConfiguration;

/**
 * @author yimin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TableInfoServiceV2ImplTest.BeanContext.class, AtlasClientV2AutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class})
@Profile({"single"})
public class TableInfoServiceV2ImplTest {
    @Autowired
    TableInfoServiceV2 tableInfoServiceV2;

    @Test
    public void tableWithColumnsOfProject() throws Exception {
        ImmutableList<Long> projectIds = ImmutableList.of(222602L, 222622L, 222631L, 222627L, 222495L, 222630L);
        Map<TableInfoV2, List<ColumnInfoV2>> tableInfoV2ListMap = tableInfoServiceV2.tableWithColumnsOfProject(projectIds);
        System.out.println(new Gson().toJson(tableInfoV2ListMap));
    }

    @Test
    public void searchByTableNameAndProjectNameAndProjectCode() throws Exception {
        final List<TableInfoDOV2> system03 = tableInfoServiceV2.searchByTableNameAndProjectNameAndProjectCode("System03", 1, 100);
        System.out.println(new Gson().toJson(system03));
    }

    @Test
    public void dslWhereConditionBuild() throws Exception {
        String matchText="Green";
        final KeyValuePairBuilder first = new StringValuePairBuilder("statusCode").value(StatusCode.COLUMN_CREATED.getCode()).op(
                AND, new StringValuePairBuilder("name").value(matchText)
                                                       .op(OR, new StringValuePairBuilder("project").value(matchText))
                                                       .op(OR, new StringValuePairBuilder("projectName").value(matchText))
        );
        assertThat(first.toString()).isEqualTo("statusCode='1311' and (name='Green' or (project='Green') or (projectName='Green'))");
    }


    @Configuration
    @Profile({"single"})
    public static class BeanContext {
        @Bean
        public HiveTableInfoServiceV2 hiveTableInfoServiceV2() {
            return Mockito.mock(HiveTableInfoServiceV2.class);
        }

        @Bean
        public ColumnInfoServiceV2 columnInfoServiceV2() {
            return new ColumnInfoServiceV2Impl();
        }

        @Bean
        public TableInfoServiceV2 tableInfoServiceV2() {
            return new TableInfoServiceV2Impl();
        }

        @Bean
        public AtlasService atlasService(AtlasClientV2 atlasClientV2) {
            return new AtlasServiceImpl(atlasClientV2);
        }

        @Bean
        public ProjectInfoService projectInfoService() {
            return Mockito.mock(ProjectInfoService.class);
        }

        @Bean
        @Qualifier(value = "userDataAuthorizationByRanger")
        public UserDataAuthorization hiveRangerAuth() {return Mockito.mock(UserDataAuthorization.class);}

        @Bean
        public AuthorizationDetailService authDetailService() {return Mockito.mock(AuthorizationDetailService.class);}
    }
}
