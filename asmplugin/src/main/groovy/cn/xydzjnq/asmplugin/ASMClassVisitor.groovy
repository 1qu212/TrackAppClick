package cn.xydzjnq.asmplugin

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

class ASMClassVisitor extends ClassVisitor implements Opcodes {
    private final static String SDK_API_CLASS = "cn/xydzjnq/track/api/TrackHelper"
    private String[] mInterfaces

    ASMClassVisitor(final ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor)
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        mInterfaces = interfaces
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions)

        String nameDesc = name + desc

        methodVisitor = new AdviceAdapter(Opcodes.ASM5, methodVisitor, access, name, desc) {
            boolean isTrackViewOnClickAnnotation = false

            @Override
            AnnotationVisitor visitAnnotation(String s, boolean b) {
                if (s == 'Lcn/xydzjnq/track/api/TrackViewOnClick;') {
                    isTrackViewOnClickAnnotation = true
                }

                return super.visitAnnotation(s, b)
            }

            /**
             * 在原有方法执行完后进行一些操作
             * @param opcode
             */
            @Override
            protected void onMethodExit(int opcode) {
                super.onMethodExit(opcode)

                /**
                 * 使用@TrackViewOnClick注解，并只有一个View参数的方法
                 */
                if (isTrackViewOnClickAnnotation && desc == '(Landroid/view/View;)V') {
                    methodVisitor.visitVarInsn(ALOAD, 1)
                    methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, "trackViewOnClick", "(Landroid/view/View;)V", false)
                    return
                }

                /**
                 * onContextItemSelected(MenuItem item)方法
                 * 或onContextItemSelected(MenuItem item)方法
                 */
                if (nameDesc == 'onContextItemSelected(Landroid/view/MenuItem;)Z' ||
                        nameDesc == 'onOptionsItemSelected(Landroid/view/MenuItem;)Z') {
                    /**
                     * 0代表this,其他数字代表第几个参数
                     */
                    methodVisitor.visitVarInsn(ALOAD, 0)
                    methodVisitor.visitVarInsn(ALOAD, 1)
                    methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, "trackViewOnClick", "(Ljava/lang/Object;Landroid/view/MenuItem;)V", false)
                }

                if ((mInterfaces != null && mInterfaces.length > 0) || isTrackViewOnClickAnnotation) {
                    /**
                     *         new View.OnClickListener() {
                     *              * @Override
                     *             public void onClick(View view) {
                     *
                     *             }
                     *         }
                     */
                    if ((mInterfaces.contains('android/view/View$OnClickListener') && nameDesc == 'onClick(Landroid/view/View;)V')) {
                        methodVisitor.visitVarInsn(ALOAD, 1)
                        methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, "trackViewOnClick", "(Landroid/view/View;)V", false)
                    }
                    /**
                     *          new DialogInterface.OnClickListener() {
                     *              * @Override
                     *             public void onClick(DialogInterface dialog, int which) {
                     *
                     *             }
                     *         }
                     */
                    else if (mInterfaces.contains('android/content/DialogInterface$OnClickListener') && nameDesc == 'onClick(Landroid/content/DialogInterface;I)V') {
                        methodVisitor.visitVarInsn(ALOAD, 1)
                        methodVisitor.visitVarInsn(ILOAD, 2)
                        methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, "trackViewOnClick", "(Landroid/content/DialogInterface;I)V", false)
                    }
                    /**
                     *              new DialogInterface.OnMultiChoiceClickListener() {
                     *                      *@Override
                     *                     public void onClick(DialogInterface dialogInterface,
                     *                                         int which, boolean isChecked) {
                     *
                     *                     }
                     *                 }
                     */
                    else if (mInterfaces.contains('android/content/DialogInterface$OnMultiChoiceClickListener') && nameDesc == 'onClick(Landroid/content/DialogInterface;IZ)V') {
                        methodVisitor.visitVarInsn(ALOAD, 1)
                        methodVisitor.visitVarInsn(ILOAD, 2)
                        methodVisitor.visitVarInsn(ILOAD, 3)
                        methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, "trackViewOnClick", "(Landroid/content/DialogInterface;IZ)V", false)
                    }
                    /**
                     *          new CompoundButton.OnCheckedChangeListener() {
                     *              * @Override
                     *             public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                     *
                     *             }
                     *         }
                     */
                    else if (mInterfaces.contains('android/widget/CompoundButton$OnCheckedChangeListener') && nameDesc == 'onCheckedChanged(Landroid/widget/CompoundButton;Z)V') {
                        methodVisitor.visitVarInsn(ALOAD, 1)
                        methodVisitor.visitVarInsn(ILOAD, 2)
                        methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, "trackViewOnClick", "(Landroid/widget/CompoundButton;Z)V", false)
                    }
                    /**
                     *          new RatingBar.OnRatingBarChangeListener() {
                     *              * @Override
                     *             public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                     *
                     *             }
                     *         }
                     */
                    else if (mInterfaces.contains('android/widget/RatingBar$OnRatingBarChangeListener') && nameDesc == 'onRatingChanged(Landroid/widget/RatingBar;FZ)V') {
                        methodVisitor.visitVarInsn(ALOAD, 1)
                        methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, "trackViewOnClick", "(Landroid/view/View;)V", false)
                    }
                    /**
                     *         new SeekBar.OnSeekBarChangeListener() {
                     *             @Override
                     *             pungTouch(SeekBar seekBar) {
                     *
                     *             }
                     *         }
                     */
                    else if (mInterfaces.contains('android/widget/SeekBar$OnSeekBarChangeListener') && nameDesc == 'onStopTrackingTouch(Landroid/widget/SeekBar;)V') {
                        methodVisitor.visitVarInsn(ALOAD, 1)
                        methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, "trackViewOnClick", "(Landroid/view/View;)V", false)
                    }
                    /**
                     *         new AdapterView.OnItemSelectedListener() {
                     *              * @Override
                     *             public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                     *
                     *             }
                     *         }
                     */
                    else if (mInterfaces.contains('android/widget/AdapterView$OnItemSelectedListener') && nameDesc == 'onItemSelected(Landroid/widget/AdapterView;Landroid/view/View;IJ)V') {
                        methodVisitor.visitVarInsn(ALOAD, 1)
                        methodVisitor.visitVarInsn(ALOAD, 2)
                        methodVisitor.visitVarInsn(ILOAD, 3)
                        methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, "trackViewOnClick", "(Landroid/widget/AdapterView;Landroid/view/View;I)V", false)
                    }
                    /**
                     *      new TabHost.OnTabChangeListener() {
                     *              * @Override
                     *             public void onTabChanged(String tabId) {
                     *
                     *             }
                     *         }
                     */
                    else if (mInterfaces.contains('android/widget/TabHost$OnTabChangeListener') && nameDesc == 'onTabChanged(Ljava/lang/String;)V') {
                        methodVisitor.visitVarInsn(ALOAD, 1)
                        methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, "trackViewOnClick", "(Ljava/lang/String;)V", false)
                    }
                    /**
                     *          new AdapterView.OnItemClickListener() {
                     *          * @Override
                     *             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                     *
                     *             }
                     *         }
                     */
                    else if (mInterfaces.contains('android/widget/AdapterView$OnItemClickListener') && nameDesc == 'onItemClick(Landroid/widget/AdapterView;Landroid/view/View;IJ)V') {
                        methodVisitor.visitVarInsn(ALOAD, 1)
                        methodVisitor.visitVarInsn(ALOAD, 2)
                        methodVisitor.visitVarInsn(ILOAD, 3)
                        methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, "trackViewOnClick", "(Landroid/widget/AdapterView;Landroid/view/View;I)V", false)
                    }
                    /**
                     *          new ExpandableListView.OnGroupClickListener() {
                     *          * @Override
                     *             public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                     *
                     *             }
                     *         }
                     */
                    else if (mInterfaces.contains('android/widget/ExpandableListView$OnGroupClickListener') && nameDesc == 'onGroupClick(Landroid/widget/ExpandableListView;Landroid/view/View;IJ)Z') {
                        methodVisitor.visitVarInsn(ALOAD, 1)
                        methodVisitor.visitVarInsn(ALOAD, 2)
                        methodVisitor.visitVarInsn(ILOAD, 3)
                        methodVisitor.visitVarInsn(LLOAD, 4)
                        methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, "trackViewOnClick", "(Landroid/widget/ExpandableListView;Landroid/view/View;IJ)V", false)
                    }
                    /**
                     *      new ExpandableListView.OnChildClickListener() {
                     *          * @Override
                     *             public boolean onChildClick(ExpandableListView expandableListView, View view,
                     *                                         int parentPos, int childPos, long l) {
                     *
                     *             }
                     *         }
                     */
                    else if (mInterfaces.contains('android/widget/ExpandableListView$OnChildClickListener') && nameDesc == 'onChildClick(Landroid/widget/ExpandableListView;Landroid/view/View;IIJ)Z') {
                        methodVisitor.visitVarInsn(ALOAD, 1)
                        methodVisitor.visitVarInsn(ALOAD, 2)
                        methodVisitor.visitVarInsn(ILOAD, 3)
                        methodVisitor.visitVarInsn(ILOAD, 4)
                        methodVisitor.visitVarInsn(LLOAD, 5)
                        methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, "trackViewOnClick", "(Landroid/widget/ExpandableListView;Landroid/view/View;IIL)V", false)
                    }
                }
            }
        }
        return methodVisitor
    }
}