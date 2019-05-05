package cn.xydzjnq.astplugin;

import com.google.auto.service.AutoService;
import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("*")
@AutoService(Processor.class)
public class ASTProcessor extends AbstractProcessor {
    private ProcessingEnvironment processingEnvironment;
    private Trees trees;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        processingEnvironment = env;
        trees = Trees.instance(env);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (!roundEnvironment.processingOver()) {
            Set<? extends Element> elements = roundEnvironment.getRootElements();
            for (Element element : elements) {
                if (element.getKind() == ElementKind.CLASS) {
                    /**
                     * 获取元素的语法树
                     */
                    JCTree tree = (JCTree) trees.getTree(element);
                    /**
                     * 解析语法树并插入代码
                     */
                    TreeTranslator visitor = new InsertTrackCodeTreeTranslator(processingEnvironment);
                    tree.accept(visitor);
                }
            }
        }
        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
