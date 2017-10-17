package cn.rongcapital.chorus.governance.atlas.types;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author yimin
 */
public class AtlasTypeDefinitionPostProcessorRegistrar implements ImportBeanDefinitionRegistrar {
    /**
     * The bean name of the {@link AtlasTypeDefinitionPostProcessor}.
     */
    public static final String BINDER_BEAN_NAME = AtlasTypeDefinitionPostProcessorRegistrar.class.getName();

    @Override
    public void registerBeanDefinitions(
            AnnotationMetadata importingClassMetadata,
            BeanDefinitionRegistry registry
    ) {
        if (!registry.containsBeanDefinition(BINDER_BEAN_NAME)) {
            BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition(AtlasTypeDefinitionPostProcessor.class);
            registry.registerBeanDefinition(BINDER_BEAN_NAME, bean.getBeanDefinition());
        }
    }
}
