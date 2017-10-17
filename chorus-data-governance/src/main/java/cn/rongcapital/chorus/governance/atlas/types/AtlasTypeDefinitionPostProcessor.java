package cn.rongcapital.chorus.governance.atlas.types;

import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.atlas.model.typedef.AtlasEntityDef;
import org.apache.atlas.model.typedef.AtlasStructDef;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.atlas.type.AtlasTypeUtil.createOptionalAttrDef;
import static org.apache.atlas.type.AtlasTypeUtil.createRequiredAttrDef;
import static org.apache.atlas.type.AtlasTypeUtil.createUniqueRequiredAttrDef;

/**
 * @author yimin
 */
@Slf4j
public class AtlasTypeDefinitionPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        AtlasEntityTypeDef annotation = AnnotationUtils.findAnnotation(bean.getClass(), AtlasEntityTypeDef.class);
        List<AtlasEntityAttributeDef> attributes = Arrays.stream(bean.getClass().getMethods())
                                                         .map(method -> AnnotationUtils.findAnnotation(method, AtlasEntityAttributeDef.class))
                                                         .filter(Objects::nonNull)
                                                         .collect(Collectors.toList());
        if (annotation != null && CollectionUtils.isNotEmpty(attributes)) {
            postProcessBeforeInitialization(bean, beanName, annotation, attributes);
        }

        return bean;
    }

    private void postProcessBeforeInitialization(Object bean, String beanName, AtlasEntityTypeDef annotation, List<AtlasEntityAttributeDef> attributes) {
        log.info("bean = [" + bean + "], beanName = [" + beanName + "], annotation = [" + annotation + "], attributes = [" + attributes + "]");

        List<AtlasStructDef.AtlasAttributeDef> attributesDef = attributes(attributes);
        AtlasEntityDef atlasEntityDef = new AtlasEntityDef(
                annotation.name(),
                annotation.description(),
                annotation.typeVersion(),
                attributesDef,
                ImmutableSet.copyOf(annotation.superTypes())
        );
        invokeAwareInterfaces(bean, atlasEntityDef);
    }

    private void invokeAwareInterfaces(Object bean, AtlasEntityDef entityDef) {
        if (bean instanceof AbstractAtlasEntityDefinitionAndBuilder) {
            ((AbstractAtlasEntityDefinitionAndBuilder) bean).setDefinition(entityDef);
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * @param attributeDef
     *
     * @return <p>
     * <p>
     * supported list
     *
     * @see org.apache.atlas.type.AtlasTypeUtil#createUniqueRequiredAttrDef(java.lang.String, org.apache.atlas.type.AtlasType)
     * @see org.apache.atlas.type.AtlasTypeUtil#createUniqueRequiredAttrDef(java.lang.String, java.lang.String)
     * @see org.apache.atlas.type.AtlasTypeUtil#createRequiredAttrDef(java.lang.String, org.apache.atlas.type.AtlasType)
     * @see org.apache.atlas.type.AtlasTypeUtil#createRequiredAttrDef(java.lang.String, java.lang.String)
     * @see org.apache.atlas.type.AtlasTypeUtil#createOptionalAttrDef(java.lang.String, org.apache.atlas.type.AtlasType)
     * @see org.apache.atlas.type.AtlasTypeUtil#createOptionalAttrDef(java.lang.String, java.lang.String)
     * <p>
     * not supported list //TODO 2017-08-22T18:27:36+08:00
     * @see org.apache.atlas.type.AtlasTypeUtil#createListRequiredAttrDef
     * @see org.apache.atlas.type.AtlasTypeUtil#createOptionalListAttrDef
     * @see org.apache.atlas.type.AtlasTypeUtil#createRequiredListAttrDefWithConstraint
     * @see org.apache.atlas.type.AtlasTypeUtil#createRequiredAttrDefWithConstraint
     * @see org.apache.atlas.type.AtlasTypeUtil#createOptionalAttrDefWithConstraint
     */
    private List<AtlasStructDef.AtlasAttributeDef> attributes(List<AtlasEntityAttributeDef> attributeDef) {
        return attributeDef.stream().map(attr -> {
            if (attr.required() && attr.unique()) return createUniqueRequiredAttrDef(attr.name(), attr.type());
            if (attr.required()) {
                return createRequiredAttrDef(attr.name(), attr.type());
            } else {
                return createOptionalAttrDef(attr.name(), attr.type());
            }
        }).collect(Collectors.toList());
    }
}
