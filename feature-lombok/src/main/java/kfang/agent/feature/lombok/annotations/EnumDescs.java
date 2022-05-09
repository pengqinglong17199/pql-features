package kfang.agent.feature.lombok.annotations;

import java.lang.annotation.*;

/**
 * EnumDescs
 *
 * @author pengqinglong
 * @since 2022/5/9
 */
@Documented
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
public @interface EnumDescs {

    EnumDesc[] value();
}