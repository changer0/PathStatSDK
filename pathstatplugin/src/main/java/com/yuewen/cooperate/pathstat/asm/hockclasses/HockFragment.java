package com.yuewen.cooperate.pathstat.asm.hockclasses;

import com.yuewen.cooperate.pathstat.asm.PushStatMethodVisitor;

import org.objectweb.asm.MethodVisitor;

/**
 * Created by zhanglulu on 2020/4/18.
 * for
 */
public class HockFragment extends HockClass {

    public HockFragment() {
        super("androidx/fragment/app/Fragment");
        methodNameList.add(new HockMethod("onCreate", "(Landroid/os/Bundle;)V"));
        methodNameList.add(new HockMethod("onStart", "()V"));
        methodNameList.add(new HockMethod("onDestroy", "()V"));
    }

    @Override
    public MethodVisitor insertMethod(String[] mInterfaces, String mClassName, String superName, MethodVisitor methodVisitor, int access, String methodName, String desc) {
        MethodVisitor visitor = null;
        if ("onCreate".equals(methodName)) {
            visitor = new PushStatMethodVisitor(methodVisitor, access, methodName, desc) {
                @Override
                protected void onMethodExit(int i) {
                    super.onMethodExit(i);
                    // ALOAD 获取类对象本身
                    methodVisitor.visitVarInsn(ALOAD, 0);
                    // INVOKESTATIC INVOKESTATIC
                    methodVisitor.visitMethodInsn(INVOKESTATIC,
                            "com/yuewen/cooperate/pathstat/HockHelper",
                            "hockFragmentOnCreate",
                            "(Landroidx/fragment/app/Fragment;)V", false);
                    System.out.println("已插入 " + className + " " + methodName);
                }
            };
        } else if ("onStart".equals(methodName)) {
            visitor = new PushStatMethodVisitor(methodVisitor, access, methodName, desc) {
                @Override
                protected void onMethodExit(int i) {
                    super.onMethodExit(i);
                    // ALOAD 获取类对象本身
                    methodVisitor.visitVarInsn(ALOAD, 0);
                    // INVOKESTATIC INVOKESTATIC
                    methodVisitor.visitMethodInsn(INVOKESTATIC,
                            "com/yuewen/cooperate/pathstat/HockHelper",
                            "hockFragmentOnStart",
                            "(Landroidx/fragment/app/Fragment;)V", false);
                    System.out.println("已插入 " + className + " " + methodName);
                }
            };
        } else if ("onDestroy".equals(methodName)) {
            visitor = new PushStatMethodVisitor(methodVisitor, access, methodName, desc) {
                @Override
                protected void onMethodExit(int i) {
                    super.onMethodExit(i);
                    // ALOAD 获取类对象本身
                    methodVisitor.visitVarInsn(ALOAD, 0);
                    // INVOKESTATIC INVOKESTATIC
                    methodVisitor.visitMethodInsn(INVOKESTATIC,
                            "com/yuewen/cooperate/pathstat/HockHelper",
                            "hockFragmentOnDestroy",
                            "(Landroidx/fragment/app/Fragment;)V", false);
                    System.out.println("已插入 " + className + " " + methodName);
                }
            };
        }
        return visitor;
    }
}
