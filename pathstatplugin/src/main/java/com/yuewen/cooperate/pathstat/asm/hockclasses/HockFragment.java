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
        methodNameList.add(new HockOnCreate());
        methodNameList.add(new HockOnOnStart());
        methodNameList.add(new HockOnOnStop());
        methodNameList.add(new HockOnDestroy());
        methodNameList.add(new HockSetUserVisibleHint());
    }
    
    //----------------------------------------------------------------------------------------------
    // Hock 方法

    private class HockOnCreate extends HockMethod {

        public HockOnCreate() {
            super("onCreate", "(Landroid/os/Bundle;)V");
        }

        @Override
        public MethodVisitor insertMethod(String[] mInterfaces, String mClassName, String superName, MethodVisitor methodVisitor, int access, String methodName, String desc) {
            return new PushStatMethodVisitor(methodVisitor, access, methodName, desc) {
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
        }
    }

    private class HockOnOnStart extends HockMethod {

        public HockOnOnStart() {
            super("onStart", "()V");
        }

        @Override
        public MethodVisitor insertMethod(String[] mInterfaces, String mClassName, String superName, MethodVisitor methodVisitor, int access, String methodName, String desc) {
            return new PushStatMethodVisitor(methodVisitor, access, methodName, desc) {
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
        }
    }

    private class HockOnOnStop extends HockMethod {

        public HockOnOnStop() {
            super("onStop", "()V");
        }

        @Override
        public MethodVisitor insertMethod(String[] mInterfaces, String mClassName, String superName, MethodVisitor methodVisitor, int access, String methodName, String desc) {
            return new PushStatMethodVisitor(methodVisitor, access, methodName, desc) {
                @Override
                protected void onMethodExit(int i) {
                    super.onMethodExit(i);
                    // ALOAD 获取类对象本身
                    methodVisitor.visitVarInsn(ALOAD, 0);
                    // INVOKESTATIC INVOKESTATIC
                    methodVisitor.visitMethodInsn(INVOKESTATIC,
                            "com/yuewen/cooperate/pathstat/HockHelper",
                            "hockFragmentOnStop",
                            "(Landroidx/fragment/app/Fragment;)V", false);
                    System.out.println("已插入 " + className + " " + methodName);
                }
            };
        }
    }

    private class HockOnDestroy extends HockMethod {

        public HockOnDestroy() {
            super("onDestroy", "()V");
        }

        @Override
        public MethodVisitor insertMethod(String[] mInterfaces, String mClassName, String superName, MethodVisitor methodVisitor, int access, String methodName, String desc) {
            return new PushStatMethodVisitor(methodVisitor, access, methodName, desc) {
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
    }

    private class HockSetUserVisibleHint extends HockMethod {

        public HockSetUserVisibleHint() {
            super("setUserVisibleHint", "(Z)V");
        }

        @Override
        public MethodVisitor insertMethod(String[] mInterfaces, String mClassName, String superName, MethodVisitor methodVisitor, int access, String methodName, String desc) {
            return new PushStatMethodVisitor(methodVisitor, access, methodName, desc) {
                @Override
                protected void onMethodExit(int i) {
                    super.onMethodExit(i);
                    // ALOAD 25
                    methodVisitor.visitVarInsn(ALOAD, 0);
                    // ILOAD 21
                    methodVisitor.visitVarInsn(ILOAD, 1);
                    // INVOKESTATIC INVOKESTATIC
                    methodVisitor.visitMethodInsn(INVOKESTATIC,
                            "com/yuewen/cooperate/pathstat/HockHelper",
                            "hockFragmentSetUserVisibleHint",
                            "(Landroidx/fragment/app/Fragment;Z)V", false);
                    System.out.println("已插入 " + className + " " + methodName);
                }
            };
        }
    }

    // Hock 方法 end
    //----------------------------------------------------------------------------------------------

}
