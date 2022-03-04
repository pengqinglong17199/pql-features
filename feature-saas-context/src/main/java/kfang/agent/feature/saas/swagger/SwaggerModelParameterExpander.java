package kfang.agent.feature.saas.swagger;

import cn.hyugatool.core.clazz.ClassUtil;
import cn.hyugatool.core.collection.ListUtil;
import cn.hyugatool.core.instance.ReflectionUtil;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.members.ResolvedField;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import kfang.agent.feature.saas.constants.SaasConstants;
import kfang.infra.api.validate.extend.annotations.IgnoreSwaggerParameter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.Maps;
import springfox.documentation.schema.Types;
import springfox.documentation.schema.property.field.FieldProvider;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.schema.AlternateTypeProvider;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;
import springfox.documentation.spring.web.readers.parameter.ExpansionContext;
import springfox.documentation.spring.web.readers.parameter.ModelAttributeField;
import springfox.documentation.spring.web.readers.parameter.ModelAttributeParameterExpander;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Predicates.*;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static springfox.documentation.schema.Collections.collectionElementType;
import static springfox.documentation.schema.Collections.isContainerType;
import static springfox.documentation.schema.Types.typeNameFor;

/**
 * 覆盖{@link ModelAttributeParameterExpander}
 *
 * @author hyuga
 * @see IgnoreSwaggerParameter
 * @since 2021/5/13
 */
@Slf4j
@Component
@Primary
@Profile({SaasConstants.DEV})
@SuppressWarnings("all")
public class SwaggerModelParameterExpander extends ModelAttributeParameterExpander {

    private static final Logger LOG = LoggerFactory.getLogger(SwaggerModelParameterExpander.class);
    private final FieldProvider fieldProvider;
    private final EnumTypeDeterminer enumTypeDeterminer;

    @Autowired
    public SwaggerModelParameterExpander(FieldProvider fields, EnumTypeDeterminer enumTypeDeterminer) {
        super(fields, enumTypeDeterminer);
        this.fieldProvider = fields;
        this.enumTypeDeterminer = enumTypeDeterminer;
    }

    @Override
    public List<Parameter> expand(ExpansionContext context) {

        List<Parameter> parameters = newArrayList();
        Set<String> beanPropNames = getBeanPropertyNames(context.getParamType().getErasedType());
        Iterable<ResolvedField> fields = FluentIterable.from(fieldProvider.in(context.getParamType()))
                .filter(onlyBeanProperties(beanPropNames));
        LOG.debug("Expanding parameter type: {}", context.getParamType());
        AlternateTypeProvider alternateTypeProvider = context.getDocumentationContext().getAlternateTypeProvider();

        FluentIterable<ModelAttributeField> modelAttributes = from(fields)
                .transform(toModelAttributeField(alternateTypeProvider));

        FluentIterable<ModelAttributeField> expendables = modelAttributes
                .filter(not(simpleType()))
                .filter(not(recursiveType(context)));
        for (ModelAttributeField each : expendables) {
            LOG.debug("Attempting to expand expandable field: {}", each.getField());
            parameters.addAll(
                    expand(
                            context.childContext(
                                    nestedParentName(context.getParentName(), each.getField()),
                                    each.getFieldType(),
                                    context.getDocumentationContext())));
        }

        FluentIterable<ModelAttributeField> collectionTypes = modelAttributes
                .filter(and(isCollection(), not(recursiveCollectionItemType(context.getParamType()))));
        for (ModelAttributeField each : collectionTypes) {
            LOG.debug("Attempting to expand collection/array field: {}", each.getField());

            ResolvedType itemType = collectionElementType(each.getFieldType());
            if (Types.isBaseType(itemType) || enumTypeDeterminer.isEnum(itemType.getErasedType())) {
                parameters.add(simpleFields(context.getParentName(), context.getDocumentationContext(), each));
            } else {
                parameters.addAll(
                        expand(
                                context.childContext(
                                        nestedParentName(context.getParentName(), each.getField()),
                                        itemType,
                                        context.getDocumentationContext())));
            }
        }

        FluentIterable<ModelAttributeField> simpleFields = modelAttributes.filter(simpleType());
        for (ModelAttributeField each : simpleFields) {
            parameters.add(simpleFields(context.getParentName(), context.getDocumentationContext(), each));
        }
        return FluentIterable.from(parameters).filter(not(hiddenParameters())).toList();
    }

    private Predicate<ModelAttributeField> recursiveCollectionItemType(final ResolvedType paramType) {
        return input -> {
            assert input != null;
            return equal(collectionElementType(input.getFieldType()), paramType);
        };
    }

    private Predicate<Parameter> hiddenParameters() {
        return Parameter::isHidden;
    }

    private Parameter simpleFields(
            String parentName,
            DocumentationContext documentationContext,
            ModelAttributeField each) {
        LOG.debug("Attempting to expand field: {}", each);
        String dataTypeName = Optional.fromNullable(typeNameFor(each.getFieldType().getErasedType()))
                .or(each.getFieldType().getErasedType().getSimpleName());
        LOG.debug("Building parameter for field: {}, with type: ", each, each.getFieldType());
        ParameterExpansionContext parameterExpansionContext = new ParameterExpansionContext(
                dataTypeName,
                parentName,
                each.getField(),
                documentationContext.getDocumentationType(),
                new ParameterBuilder());
        return pluginsManager.expandParameter(parameterExpansionContext);
    }


    private Predicate<ModelAttributeField> recursiveType(final ExpansionContext context) {
        return input -> context.hasSeenType(input.getFieldType());
    }

    private Predicate<ModelAttributeField> simpleType() {
        return and(not(isCollection()), not(isMap()),
                or(
                        belongsToJavaPackage(),
                        isBaseType(),
                        isEnum()));
    }

    private Predicate<ModelAttributeField> isCollection() {
        return input -> isContainerType(input.getFieldType());
    }

    private Predicate<ModelAttributeField> isMap() {
        return input -> Maps.isMapType(input.getFieldType());
    }

    private Predicate<ModelAttributeField> isEnum() {
        return input -> enumTypeDeterminer.isEnum(input.getFieldType().getErasedType());
    }

    private Predicate<ModelAttributeField> belongsToJavaPackage() {
        return input -> ClassUtils.getPackageName(input.getFieldType().getErasedType()).startsWith("java.lang");
    }

    private Predicate<ModelAttributeField> isBaseType() {
        return input -> Types.isBaseType(input.getFieldType())
                || input.getField().getType().isPrimitive();
    }

    private Function<ResolvedField, ModelAttributeField> toModelAttributeField(
            final AlternateTypeProvider
                    alternateTypeProvider) {
        return input -> new ModelAttributeField(fieldType(alternateTypeProvider, input), input);
    }

    private Predicate<ResolvedField> onlyBeanProperties(final Set<String> beanPropNames) {
        return input -> beanPropNames.contains(input.getName());
    }

    private String nestedParentName(String parentName, ResolvedField field) {
        String name = field.getName();
        ResolvedType fieldType = field.getType();
        if (isContainerType(fieldType) && !Types.isBaseType(collectionElementType(fieldType))) {
            name += "[0]";
        }

        if (isNullOrEmpty(parentName)) {
            return name;
        }
        return String.format("%s.%s", parentName, name);
    }

    private ResolvedType fieldType(AlternateTypeProvider alternateTypeProvider, ResolvedField field) {
        return alternateTypeProvider.alternateFor(field.getType());
    }

    private Set<String> getBeanPropertyNames(final Class<?> clazz) {
        try {
            Set<String> beanProps = new HashSet<>();
            PropertyDescriptor[] propDescriptors = getBeanInfo(clazz).getPropertyDescriptors();

            List<String> declaredFieldNames = ListUtil.emptyList();
            try {
                if (ClassUtil.isNormalClass(clazz)) {
                    declaredFieldNames = ReflectionUtil.getDeclaredFieldNames(clazz);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (PropertyDescriptor propDescriptor : propDescriptors) {
                // 增加逻辑，忽略@IgnoreSwaggerParameter注解的字段
                String name = propDescriptor.getName();
                if (declaredFieldNames.contains(name)) {
                    Field field = ReflectionUtil.getDeclaredField(clazz, name);
                    if (field != null) {
                        field.setAccessible(true);
                        IgnoreSwaggerParameter ignoreSwaggerParameter = field.getDeclaredAnnotation(IgnoreSwaggerParameter.class);
                        if (ignoreSwaggerParameter != null) {
                            continue;
                        }
                    }
                }
                // 增加结束

                if (propDescriptor.getReadMethod() != null) {
                    beanProps.add(name);
                }
            }

            return beanProps;

        } catch (Exception e) {
            LOG.warn(String.format("Failed to get bean properties on (%s)", clazz), e);
        }
        return newHashSet();
    }

    @VisibleForTesting
    BeanInfo getBeanInfo(Class<?> clazz) throws IntrospectionException {
        return Introspector.getBeanInfo(clazz);
    }

}
