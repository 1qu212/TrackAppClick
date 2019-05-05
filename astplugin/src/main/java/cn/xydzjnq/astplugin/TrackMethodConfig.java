package cn.xydzjnq.astplugin;

import com.sun.tools.javac.tree.JCTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TrackMethodConfig {
    private final static List<TrackMethodCell> sInterfaceMethods = new ArrayList<>();

    /**
     * 代码不能在return语句之后插入，所以有返回值的必须是InsertLocation.BEFORE
     */
    static {
        sInterfaceMethods.add(new TrackMethodCell("onClick", "void", Collections.singletonList("View"), InsertLocation.AFTER));
        sInterfaceMethods.add(new TrackMethodCell("onClick", "void", Collections.singletonList("android.view.View"), InsertLocation.AFTER));
        sInterfaceMethods.add(new TrackMethodCell("onClick", "void", Arrays.asList("DialogInterface", "int"), InsertLocation.AFTER));
        sInterfaceMethods.add(new TrackMethodCell("onClick", "void", Arrays.asList("DialogInterface", "int", "boolean"), InsertLocation.AFTER));
        sInterfaceMethods.add(new TrackMethodCell("onOptionsItemSelected", "boolean", Collections.singletonList("MenuItem"), InsertLocation.BEFORE));
        sInterfaceMethods.add(new TrackMethodCell("onContextItemSelected", "boolean", Collections.singletonList("MenuItem"), InsertLocation.BEFORE));
        sInterfaceMethods.add(new TrackMethodCell("onCheckedChanged", "void", Arrays.asList("CompoundButton", "boolean"), InsertLocation.AFTER));
        sInterfaceMethods.add(new TrackMethodCell("onRatingChanged", "void", Arrays.asList("RatingBar", "float", "boolean"), InsertLocation.AFTER));
        sInterfaceMethods.add(new TrackMethodCell("onStopTrackingTouch", "void", Collections.singletonList("SeekBar"), InsertLocation.AFTER));
        sInterfaceMethods.add(new TrackMethodCell("onItemSelected", "void", Arrays.asList("AdapterView<?>", "View", "int", "long"), InsertLocation.AFTER));
        sInterfaceMethods.add(new TrackMethodCell("onItemClick", "void", Arrays.asList("AdapterView<?>", "View", "int", "long"), InsertLocation.AFTER));
        sInterfaceMethods.add(new TrackMethodCell("onTabChanged", "void", Collections.singletonList("String"), InsertLocation.AFTER));
        sInterfaceMethods.add(new TrackMethodCell("onGroupClick", "boolean", Arrays.asList("ExpandableListView", "View", "int", "long"), InsertLocation.BEFORE));
        sInterfaceMethods.add(new TrackMethodCell("onChildClick", "boolean", Arrays.asList("ExpandableListView", "View", "int", "int", "long"), InsertLocation.BEFORE));
    }

    /**
     * 方法匹配
     * @param jcMethodDecl
     * @return
     */
    public static TrackMethodCell isMatched(JCTree.JCMethodDecl jcMethodDecl) {
        for (TrackMethodCell methodCell : sInterfaceMethods) {
            if (jcMethodDecl.getName().toString().equals(methodCell.getName())) {
                if (jcMethodDecl.getReturnType().toString().equals(methodCell.getReturnType())) {
                    if (jcMethodDecl.getParameters().size() == methodCell.getParamsType().size()) {
                        if (isParamsMatched(jcMethodDecl, methodCell.getParamsType())) {
                            return methodCell;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 方法参数匹配
     * @param jcMethodDecl
     * @param paramsList
     * @return
     */
    private static boolean isParamsMatched(JCTree.JCMethodDecl jcMethodDecl, List<String> paramsList) {
        boolean isMatched = true;
        for (int i = 0; i < jcMethodDecl.getParameters().size(); i++) {
            if (!jcMethodDecl.getParameters().get(i).vartype.toString().equals(paramsList.get(i))) {
                isMatched = false;
                break;
            }
        }
        return isMatched;
    }
}
