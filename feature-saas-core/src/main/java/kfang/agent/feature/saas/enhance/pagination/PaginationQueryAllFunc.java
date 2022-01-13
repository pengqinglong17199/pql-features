package kfang.agent.feature.saas.enhance.pagination;

import kfang.infra.common.model.Pagination;

/**
 * 多任务函数
 *
 * @author hyuga
 */
@FunctionalInterface
public interface PaginationQueryAllFunc<T, V> {

    Pagination<V> pageQuery(T pageForm);

}
