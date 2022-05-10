package kfang.agent.feature.lombok.pql.processor;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.*;
import kfang.agent.feature.lombok.pql.annotations.EnumDesc;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static kfang.agent.feature.lombok.pql.constants.EnumConstants.DESC;

/**
 * 枚举描述方法生成处理
 *
 * @author pengqinglong
 * @since 2022/5/9
 */
public class EnumDescProcessor extends AgentProcessor {

    /**
     * 处理实现
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> enumDescList = roundEnv.getElementsAnnotatedWith(EnumDesc.class);

        // 字段处理
        enumDescList.stream()
                .filter(element -> Objects.equals(ElementKind.FIELD, element.getKind()))
                .forEach(this::handleField);


        // class处理
        enumDescList.stream()
                .filter(element -> Objects.equals(ElementKind.CLASS, element.getKind()))
                .forEach(this::handleClass);

        return true;
    }

    /**
     * 核心方法 class的处理逻辑
     */
    private void handleClass(Element element) {
        JCTree tree = trees.getTree(element);
        tree.accept(new TreeTranslator() {
            @Override
            public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                jcClassDecl.defs.stream()
                        // 只处理所有的变量
                        .filter(it -> it.getKind().equals(Tree.Kind.VARIABLE))
                        // 强转为变量
                        .map(it -> (JCTree.JCVariableDecl) it)
                        // class级只处理枚举字段
                        .filter(it -> typeEquals(it, Enum.class))
                        // 只处理没有注解的变量 注解的变量已经处理过了
                        .filter(it -> {
                            List<JCTree.JCAnnotation> list = it.getModifiers().getAnnotations();
                            return !(list != null && list.stream().anyMatch(jc -> jcEquals(jc, EnumDesc.class)));
                        })
                        .forEach(it -> jcClassDecl.defs = jcClassDecl.defs.append(fieldGetterMethod(upperCase(DESC), it)));

                super.visitClassDef(jcClassDecl);
            }
        });
    }

    /**
     * 核心方法 字段的处理逻辑
     */
    private void handleField(Element element) {
        JCTree tree = trees.getTree(element);
        tree.accept(new TreeTranslator() {

            @Override
            public void visitVarDef(JCTree.JCVariableDecl tree) {
                JCTree.JCModifiers modifiers = tree.getModifiers();
                List<JCTree.JCAnnotation> annotations = modifiers.getAnnotations();
                if (annotations == null) {
                    return;
                }
                JCTree clazzTree = trees.getTree(element.getEnclosingElement());

                for (JCTree.JCAnnotation annotation : annotations) {

                    // 找到注解
                    if (jcEquals(annotation, EnumDesc.class)) {
                        Set<String> nameSet = getMethodNameSuffixSet(annotation);
                        for (String name : nameSet) {
                            JCTree jcTree = fieldGetterMethod(name, tree);
                            ((JCTree.JCClassDecl) clazzTree).defs = ((JCTree.JCClassDecl) clazzTree).defs.append(jcTree);
                        }
                        break;
                    }
                }
                super.visitVarDef(tree);
            }
        });
    }

    /**
     * 获取方法后缀名
     */
    private Set<String> getMethodNameSuffixSet(JCTree.JCAnnotation annotation) {
        List<JCTree.JCExpression> args = annotation.getArguments();

        Set<String> nameSet = new HashSet<>();

        // 默认值
        if(args == null || args.size() == 0){
            nameSet.add(super.upperCase(DESC));
            return nameSet;
        }

        // EnumDesc注解只有一个字段
        JCTree.JCAssign assign = (JCTree.JCAssign) args.get(0);
        JCTree.JCNewArray array = (JCTree.JCNewArray) assign.getExpression();
        for (JCTree.JCExpression elem : array.elems) {
            JCTree.JCLiteral literal = (JCTree.JCLiteral) elem;
            nameSet.add(super.upperCase(literal.getValue().toString()));
        }

        return nameSet;
    }

    /**
     * 字段生成方法
     */
    private JCTree fieldGetterMethod(String suffix, JCTree.JCVariableDecl tree) {

        // 生成return语句
        JCTree.JCReturn returnStatement = treeMaker.Return(
                treeMaker.Conditional(
                        treeMaker.Binary(JCTree.Tag.NE, treeMaker.Select(treeMaker.Ident(names.fromString("this")), tree.getName()), treeMaker.Literal(TypeTag.BOT, 0)),
                        treeMaker.Apply(List.nil(), treeMaker.Select(treeMaker.Ident(tree.getName()), names.fromString(String.format("get%s", suffix))), List.nil()),
                        treeMaker.Literal("")
                )
        );

        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<JCTree.JCStatement>().append(returnStatement);

        // public 方法访问级别修饰
        JCTree.JCModifiers modifiers = treeMaker.Modifiers(Flags.PUBLIC);
        // 方法名 getXXX ，根据字段名生成首字母大写的get方法
        Name getMethodName = createGetMethodName(tree.getName(), suffix);
        // 返回值类型，get类型的返回值类型与字段类型一致
        JCTree.JCIdent string = treeMaker.Ident(names.fromString("String"));
        // 生成方法体
        JCTree.JCBlock body = treeMaker.Block(0, statements.toList());
        // 泛型参数列表
        List<JCTree.JCTypeParameter> methodGenericParamList = List.nil();
        // 参数值列表
        List<JCTree.JCVariableDecl> parameterList = List.nil();
        // 异常抛出列表
        List<JCTree.JCExpression> throwCauseList = List.nil();

        // 生成方法定义树节点
        return treeMaker.MethodDef(
                // 方法访问级别修饰符
                modifiers,
                // get 方法名
                getMethodName,
                // 返回值类型
                string,
                // 泛型参数列表
                methodGenericParamList,
                //参数值列表
                parameterList,
                // 异常抛出列表
                throwCauseList,
                // 方法默认体
                body,
                // 默认值
                null
        );
    }

    /**
     * 创建get方法名
     */
    private Name createGetMethodName(Name variableName, String suffix) {
        return names.fromString(String.format("get%s%s", super.upperCase(variableName.toString()), suffix));
    }
    @Override
    protected Set<String> getAnnotationTypes() {
        return Set.of("kfang.agent.feature.lombok.pql.annotations.EnumDesc", "kfang.agent.feature.lombok.pql.annotations.EnumDescs");
    }
}