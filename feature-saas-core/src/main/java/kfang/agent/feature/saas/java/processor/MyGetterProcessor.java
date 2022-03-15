/*
package kfang.agent.feature.saas.java.processor;

import com.sun.source.util.JavacTask;
import com.sun.tools.javac.*;
import lombok.Data;
import lombok.Getter;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.util.Set;

*/
/**
 * MyGetterProcessor
 *
 * @author pengqinglong
 * @since 2022/3/10
 *//*

@Getter
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@SupportedAnnotationTypes("kfang.agent.feature.saas.java.processor.MyGetter")
public class MyGetterProcessor extends AbstractProcessor {

    private Messager messager;
*/
/*    protected com.sun.tools.javac.api.JavacTrees trees;
    protected TreeMaker treeMaker;
    protected Names names;*//*


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv){
        //Elements elements = javacTrees.getElements();
        JavacTask instance = JavacTask.instance(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(MyGetter.class);
        // elements = javacTrees.getElements();
        return false;
    }
}*/
