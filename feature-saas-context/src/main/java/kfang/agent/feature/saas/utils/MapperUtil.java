package kfang.agent.feature.saas.utils;

import cn.hyugatool.core.collection.ListUtil;
import cn.hyugatool.core.instance.ReflectionUtil;
import cn.hyugatool.core.object.ObjectUtil;
import cn.hyugatool.core.optional.HyugaOptional;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import kfang.infra.common.model.Pagination;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * MapperUtil
 * 对象/集合/分页对象拷贝工具类
 * 注意：form类型的对象不要使用该工具，使用form.derive(class)
 *
 * @author hyuga
 * @since 2020-12-16 下午2:52
 */
@Slf4j
public class MapperUtil {

    private static final Mapper MAPPER = DozerBeanMapperBuilder.buildDefault();

    /**
     * 单个对象拷贝
     *
     * @param <T>    泛型
     * @param <R>    泛型
     * @param source 源对象
     * @param target 目标对象
     */
    public static <T, R> void copy(T source, R target) {
        if (HyugaOptional.ofNullable(source).isEmpty()) {
            return;
        }
        MAPPER.map(source, target);
    }

    /**
     * 单个对象拷贝
     *
     * @param source 源对象
     * @param clazz  目标对象class
     * @param <T>    泛型
     * @param <R>    泛型
     * @return 拷贝后的目标对象
     */
    public static <T, R> R copy(T source, Class<R> clazz) {
        if (HyugaOptional.ofNullable(source).isEmpty()) {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
                log.error("MapperUtil.copy error:{}", e.getMessage());
            }
        }
        return MAPPER.map(source, clazz);
    }

    /**
     * 对象集合拷贝
     *
     * @param sourceList 源对象集合
     * @param clazz      目标对象class
     * @param <T>        泛型
     * @param <R>        泛型
     * @return 拷贝后的目标对象集合
     */
    public static <T, R> List<R> copy(List<T> sourceList, Class<R> clazz) {
        if (ListUtil.isEmpty(sourceList)) {
            return ListUtil.emptyList();
        }
        List<R> targetList = ListUtil.newArrayList();
        sourceList.forEach(sourceObject -> {
            R targetObject = MAPPER.map(sourceObject, clazz);
            targetList.add(targetObject);
        });
        return targetList;
    }

    /**
     * 分页对象集合拷贝，包含item集合
     *
     * @param sourcePagination 源分页对象
     * @param clazz            目标分页对象元素实体class
     * @param <T>              泛型
     * @param <R>              泛型
     * @return 拷贝后的目标分页对象
     */
    public static <T, R> Pagination<R> copy(Pagination<T> sourcePagination, Class<R> clazz) {
        Pagination<R> targetPagination = copyPageBasicParam(sourcePagination);

        HyugaOptional<Pagination<T>> paginationHyugaOptional = HyugaOptional.ofNullable(sourcePagination);
        if (paginationHyugaOptional.isPresent()) {
            List<T> sourceList = paginationHyugaOptional.getBean(Pagination::getItems).orElse(ListUtil.emptyList());
            targetPagination.setItems(copy(sourceList, clazz));
        }
        return targetPagination;
    }

    /**
     * 分页对象拷贝，不包含item集合
     *
     * @param sourcePagination 源分页对象
     * @param <T>              泛型
     * @param <R>              泛型
     * @return 拷贝后的目标分页对象
     */
    public static <T, R> Pagination<R> copyPageBasicParam(Pagination<T> sourcePagination) {
        Pagination<R> targetPagination = new Pagination<>();
        targetPagination.setCurrentPage(sourcePagination.getCurrentPage());
        targetPagination.setPageSize(sourcePagination.getPageSize());
        targetPagination.setRecordCount(sourcePagination.getRecordCount());
        targetPagination.setQueryRecordCount(sourcePagination.isQueryRecordCount());
        return targetPagination;
    }

    /**
     * 拷贝对象中的分页信息【"currentPage", "pageSize", "queryRecordCount"】
     *
     * @param source      源对象
     * @param targetClazz 目标对象class
     * @param <T>         泛型
     * @param <R>         泛型
     * @return 拷贝分页基础参数
     */
    public static <T, R> R copyPageBasicParam(T source, Class<R> targetClazz) {
        R target = ReflectionUtil.newInstance(targetClazz);
        if (ObjectUtil.isNull(source)) {
            return target;
        }

        List<String> sourceDeclaredFieldNames = ReflectionUtil.getDeclaredFieldNames(source.getClass());
        List<String> targetDeclaredFieldNames = ReflectionUtil.getDeclaredFieldNames(targetClazz);

        List<String> fields = ListUtil.newArrayList("currentPage", "pageSize", "queryRecordCount");

        fields.forEach(field -> {
            if (sourceDeclaredFieldNames.contains(field) && targetDeclaredFieldNames.contains(field)) {
                ReflectionUtil.setFieldValue(target, field, ReflectionUtil.getFieldValue(source, field));
            }
        });
        return target;
    }

}
