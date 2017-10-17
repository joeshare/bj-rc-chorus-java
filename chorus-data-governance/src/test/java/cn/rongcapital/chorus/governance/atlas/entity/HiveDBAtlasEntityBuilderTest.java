package cn.rongcapital.chorus.governance.atlas.entity;

import com.google.common.collect.Maps;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author yimin
 */
public class HiveDBAtlasEntityBuilderTest {

    private final HiveDBEntityBuilder builder = HiveDBEntityBuilder.INSTANCE;

    @Test(expected = NullPointerException.class)
    public void buildGetNull() throws Exception {
        assertThat(builder.build(builder.getEntity())).isNull();
    }


    @Test
    public void multipleThreadsBuildAtSameTime() {

        final List<Thread> collect = IntStream.range(1, 500).mapToObj(i -> new Thread(() -> {
//            System.out.println(Thread.currentThread().getName() + ": [active:" + Thread.activeCount()+"]");

            try {
                String name = "thread-" + i;
                final AtlasEntity sourceEntity = builder.getEntity();
                builder.name(sourceEntity, name);
                TimeUnit.MILLISECONDS.sleep(RandomUtils.nextLong(100L, 300L));

                String description = RandomStringUtils.randomAlphabetic(10) + "_description";
                builder.descritpion(sourceEntity, description);
                builder.ownerType(sourceEntity,1);
                builder.clusterName(sourceEntity,"clusterName");
                builder.parameters(sourceEntity, Maps.newHashMap());

                fillNonNullFields(sourceEntity);

                TimeUnit.MILLISECONDS.sleep(RandomUtils.nextLong(100L, 300L));
                final AtlasEntity entity = builder.build(sourceEntity);

                assertThat(entity.getAttribute("name")).isEqualTo(name);
                assertThat(entity.getAttribute("description")).isEqualTo(description);

                assertThat(Thread.currentThread().getName()).isEqualTo(name);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }, "thread-" + i)).collect(Collectors.toList());

        collect.forEach(thread -> {

            thread.start();
        });
        collect.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.err.println(e.getLocalizedMessage());
            }
        });
    }

    @Test
    public void descriptionNullable() throws Exception {
        final AtlasEntity entity = builder.getEntity();
        builder.name(entity, RandomStringUtils.randomAlphabetic(20));
        fillNonNullFields(entity);
        builder.descritpion(entity, null);
    }

    @Test
    public void localUrlNullable() throws Exception {
        final AtlasEntity entity = builder.getEntity();
        builder.name(entity, RandomStringUtils.randomAlphabetic(20));
        fillNonNullFields(entity);
        builder.locationUrl(entity, null);
    }

    private void fillNonNullFields(AtlasEntity entity) {
        builder.owner(entity, RandomStringUtils.randomAlphabetic(20));

        builder.createUserId(entity, RandomStringUtils.randomAlphabetic(20));
        builder.createUser(entity, RandomStringUtils.randomAlphabetic(20));
        builder.projectId(entity, RandomUtils.nextLong(1, 1000L));
        builder.project(entity, RandomStringUtils.randomAlphabetic(20));
    }
}
