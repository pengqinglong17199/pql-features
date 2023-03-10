package kfang.agent.feature.lombok.pql.processor;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import kfang.agent.feature.lombok.pql.annotations.EnumDesc;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
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
                Set<JCTree.JCMethodDecl> methodDeclSet = new HashSet<>();

                /*
                 * 1.只处理所有的变量
                 * 2.强转为变量
                 * 3.class级只处理枚举字段
                 * 4.只处理没有注解的变量 注解的变量已经处理过了
                 */
                jcClassDecl.defs.stream()
                        .filter(it -> it.getKind().equals(Tree.Kind.VARIABLE))
                        .map(it -> (JCTree.JCVariableDecl) it)
                        .filter(it -> typeEquals(it, Enum.class))
                        .filter(it -> {
                            List<JCTree.JCAnnotation> list = it.getModifiers().getAnnotations();
                            return !(list != null && list.stream().anyMatch(jc -> jcEquals(jc, EnumDesc.class)));
                        })
                        .forEach(it -> {
                            JCTree.JCMethodDecl jcMethodDecl = fieldGetterMethod(upperCase(DESC), it);
                            methodDeclSet.add(jcMethodDecl);
                        });
                for (JCTree.JCMethodDecl jcMethodDecl : methodDeclSet) {
                    appendMethod(jcClassDecl, jcMethodDecl);
                }
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
                            JCTree.JCMethodDecl jcTree = fieldGetterMethod(name, tree);
                            appendMethod((JCTree.JCClassDecl) clazzTree, jcTree);
                        }
                        break;
                    }
                }
                super.visitVarDef(tree);
            }
        });
    }

    /**
     * 添加方法进入语法树
     */
    private void appendMethod(JCTree.JCClassDecl clazzTree, JCTree.JCMethodDecl jcTree) {
        // 校验方法是否存在 如果存在 则不处理
        boolean isExist = clazzTree.defs
                .stream()
                .filter(def -> Objects.equals(Tree.Kind.METHOD, def.getKind()))
                .map(def -> (JCTree.JCMethodDecl) def)
                .anyMatch(def -> def.name.toString().equals(jcTree.name.toString()));
        if (isExist) {
            return;
        }
        clazzTree.defs = clazzTree.defs.append(jcTree);
    }

    /**
     * 获取方法后缀名
     */
    private Set<String> getMethodNameSuffixSet(JCTree.JCAnnotation annotation) {
        List<JCTree.JCExpression> args = annotation.getArguments();

        Set<String> nameSet = new HashSet<>();

        // 默认值
        if (args == null || args.size() == 0) {
            nameSet.add(super.upperCase(DESC));
            return nameSet;
        }

        // EnumDesc注解只有一个字段
        JCTree.JCAssign assign = (JCTree.JCAssign) args.get(0);
        if (assign.getExpression() instanceof JCTree.JCLiteral) {
            JCTree.JCLiteral literal = (JCTree.JCLiteral)assign.getExpression();
            nameSet.add(super.upperCase(literal.getValue().toString()));
            return nameSet;
        }

        // EnumDesc注解存在多个字段
        if (assign.getExpression() instanceof JCTree.JCNewArray) {
            JCTree.JCNewArray array = (JCTree.JCNewArray) assign.getExpression();
            for (JCTree.JCExpression elem : array.elems) {
                JCTree.JCLiteral literal = (JCTree.JCLiteral) elem;
                nameSet.add(super.upperCase(literal.getValue().toString()));
            }
        }

        return nameSet;
    }

    /**
     * 字段生成方法
     */
    private JCTree.JCMethodDecl fieldGetterMethod(String suffix, JCTree.JCVariableDecl tree) {

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
                // 参数值列表
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
        String getMethodName = String.format("get%s%s", super.upperCase(variableName.toString()), suffix);
        return names.fromString(getMethodName);
    }

    @Override
    protected Set<String> getAnnotationTypes() {
        Set<String> annotationTypes = new HashSet<>();
        annotationTypes.add("kfang.agent.feature.lombok.pql.annotations.EnumDesc");
        return annotationTypes;
    }

}