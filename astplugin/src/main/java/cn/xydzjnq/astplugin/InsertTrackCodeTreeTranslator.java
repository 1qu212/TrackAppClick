package cn.xydzjnq.astplugin;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.ProcessingEnvironment;

public class InsertTrackCodeTreeTranslator extends TreeTranslator {
    private TreeMaker treeMaker;
    private Name.Table names;

    InsertTrackCodeTreeTranslator(ProcessingEnvironment env) {
        Context context = ((JavacProcessingEnvironment)
                env).getContext();
        treeMaker = TreeMaker.instance(context);
        names = Names.instance(context).table;
    }

    @Override
    public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl) {
        super.visitMethodDef(jcMethodDecl);

        try {
            List<JCTree.JCAnnotation> annotationList = jcMethodDecl.getModifiers().annotations;
            /**
             * 注解的方法也可以注入代码
             */
            if (annotationList.toString().startsWith("@OnClick") ||
                    annotationList.toString().contains("@TrackViewOnClick")) {
                insertTrackCode(jcMethodDecl, 1, InsertLocation.AFTER);
                return;
            }

            /**
             * 如果是匹配的方法则注入代码
             */
            TrackMethodCell methodCell = TrackMethodConfig.isMatched(jcMethodDecl);
            if (methodCell != null) {
                insertTrackCode(jcMethodDecl, methodCell.getParamsType().size(), methodCell.getInsertLocation());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertTrackCode(JCTree.JCMethodDecl jcMethodDecl, int paramsCount, InsertLocation insertLocation) {
        JCTree.JCExpression sdkExpression = treeMaker.Ident(names.fromString("cn"));
        sdkExpression = treeMaker.Select(sdkExpression, names.fromString("xydzjnq"));
        sdkExpression = treeMaker.Select(sdkExpression, names.fromString("track"));
        sdkExpression = treeMaker.Select(sdkExpression, names.fromString("api"));
        sdkExpression = treeMaker.Select(sdkExpression, names.fromString("TrackHelper"));
        sdkExpression = treeMaker.Select(sdkExpression, names.fromString("trackViewOnClick"));

        List<JCTree.JCExpression> paramsList = List.nil();
        for (int i = 0; i < paramsCount; i++) {
            paramsList = paramsList.append(treeMaker.Ident(jcMethodDecl.getParameters().get(i).name));
        }

        JCTree.JCStatement statement = treeMaker.Exec(
                treeMaker.Apply(List.<JCTree.JCExpression>nil(),
                        sdkExpression,
                        paramsList
                )
        );

        if (insertLocation == InsertLocation.AFTER) {
            jcMethodDecl.body.stats = jcMethodDecl.body.stats.appendList(List.of(statement));
        } else {
            jcMethodDecl.body.stats = jcMethodDecl.body.stats.prependList(List.of(statement));
        }
    }

}
