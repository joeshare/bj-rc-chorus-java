package cn.rongcapital.chorus.governance.atlas.types;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yimin
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AtlasEntityTypeDef {
    String name();

    String description() default "";

    String typeVersion() default "0.1";

    String[] superTypes();
}
