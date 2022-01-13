package kfang.agent.feature.saas.sql.isolation.enums;

import kfang.infra.common.isolation.DataIsolation;
import kfang.infra.common.isolation.PlatformDataIsolation;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum Level {

        /**
         * 无隔离
         */
        ALL("", "", false, DataIsolation.class),
        /**
         * 平台级别
         */
        PLATFORM("FPLATFORM_ORG_ID",
                "platformOrgId",
                true,
                PlatformDataIsolation.class
        ),
        /**
         * 人员级别
         */
        PERSON("FCREATE_OPERATOR_ID", "operatorId", false, null)
        ;

        /**
         * sql字段名
         */
        private final String sqlFieldName;

        /**
         * java字段名
         */
        private final String javaFieldName;

        /**
         * 是否需要拼接sql
         */
        private final boolean joinSql;

        /**
         * 拼接sql 参数form需要实现的接口form
         */
        private final Class<? extends DataIsolation> clazz;
}