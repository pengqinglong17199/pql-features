package kfang.agent.feature.lombok.pql.processor;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;
import kfang.agent.feature.lombok.pql.annotations.EnumDesc;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * AgentProcessor
 *
 * @author pengqinglong
 * @since 2022/5/9
 */
public abstract class AgentProcessor extends AbstractProcessor {

    protected JavacTrees trees;
    protected TreeMaker treeMaker;
    protected Names names;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.trees = JavacTrees.instance(processingEnv);
        Method[] declaredMethods = processingEnv.getClass().getDeclaredMethods();
        Context context  = null;
        for (Method declaredMethod : declaredMethods) {
            if ("getContext".equals(declaredMethod.getName())) {
                try {
                    context = (Context) declaredMethod.invoke(processingEnv, null);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
    }

    /**
     * jdk版本11
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_11;
    }

    /**
     * 需要处理的注解类型
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return getAnnotationTypes();
    }

    /**
     * 需要处理的注解集合
     */
    protected abstract Set<String> getAnnotationTypes();

    /**
     * 注解类型比对
     */
    protected boolean jcEquals(JCTree.JCAnnotation annotation, Class<?> clazz) {
        return annotation.type.toString().equals(clazz.getCanonicalName());
    }

    /**
     * 字段类型比对
     */
    protected boolean typeEquals(JCTree.JCVariableDecl it, Class<?> clazz){
        Type.ClassType classType= (Type.ClassType)it.getType().type;
        return classType.supertype_field.baseType().tsym.toString().equals(clazz.getCanonicalName());
    }

    /**
     * 字符串首字母大写
     */
    protected String upperCase(String str) {
        String suffix;
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        bytes[0] = (byte)(bytes[0] - 32);
        suffix = new String(bytes);
        return suffix;
    }

}