package kfang.agent.feature.lombok.processor;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;
import kfang.agent.feature.lombok.annotations.EnumDesc;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    protected boolean jcEquals(JCTree.JCAnnotation annotation, Class<?> clazz) {
        return annotation.type.toString().equals(clazz.getCanonicalName());
    }
}