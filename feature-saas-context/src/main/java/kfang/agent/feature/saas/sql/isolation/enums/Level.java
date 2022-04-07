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
        ALL("", "", false, null, 0,DataIsolation.class),
        /**
         * 平台级别
         */
        PLATFORM("FPLATFORM_ORG_ID",
                "platformOrgId",
                true,
                ALL,
                1,
                PlatformDataIsolation.class
        ),
        /**
         * 人员级别
         */
        PERSON("FCREATE_OPERATOR_ID", "operatorId", false, ALL,1,null)
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
         * 所属父级别 用于模块区分
         */
        private final Level parent;

        /**
         * 所属等级 与parent对
         */
        private final int rank;

        /**
         * 拼接sql 参数form需要实现的接口form
         */
        private final Class<? extends DataIsolation> clazz;
}