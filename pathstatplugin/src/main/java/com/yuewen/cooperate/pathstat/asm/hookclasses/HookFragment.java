package com.yuewen.cooperate.pathstat.asm.hookclasses;

import com.yuewen.cooperate.pathstat.asm.PushStatMethodVisitor;

import org.objectweb.asm.MethodVisitor;

/**
 * Created by zhanglulu on 2020/4/18.
 * for
 */
public class HookFragment extends HookClass {

    public HookFragment() {
        super("androidx/fragment/app/Fragment");
        methodNameList.add(new HookOnCreate());
        methodNameList.add(new HookOnOnStart());
        methodNameList.add(new HookOnOnStop());
        methodNameList.add(new HookOnDestroy());
        methodNameList.add(new HookSetUserVisibleHint());
    }
    
    //----------------------------------------------------------------------------------------------
    // Hook 方法

    private class HookOnCreate extends HookMethod {

        public HookOnCreate() {
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
                            "com/yuewen/cooperate/pathstat/HookHelper",
                            "hookFragmentOnCreate",
                            "(Landroidx/fragment/app/Fragment;)V", false);
                    System.out.println("已插入 " + className + " " + methodName);
                }
            };
        }
    }

    private class HookOnOnStart extends HookMethod {

        public HookOnOnStart() {
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
                            "com/yuewen/cooperate/pathstat/HookHelper",
                            "hookFragmentOnStart",
                            "(Landroidx/fragment/app/Fragment;)V", false);
                    System.out.println("已插入 " + className + " " + methodName);
                }
            };
        }
    }

    private class HookOnOnStop extends HookMethod {

        public HookOnOnStop() {
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
                            "com/yuewen/cooperate/pathstat/HookHelper",
                            "hookFragmentOnStop",
                            "(Landroidx/fragment/app/Fragment;)V", false);
                    System.out.println("已插入 " + className + " " + methodName);
                }
            };
        }
    }

    private class HookOnDestroy extends HookMethod {

        public HookOnDestroy() {
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
                            "com/yuewen/cooperate/pathstat/HookHelper",
                            "hookFragmentOnDestroy",
                            "(Landroidx/fragment/app/Fragment;)V", false);
                    System.out.println("已插入 " + className + " " + methodName);
                }
            };
        }
    }

    private class HookSetUserVisibleHint extends HookMethod {

        public HookSetUserVisibleHint() {
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
                            "com/yuewen/cooperate/pathstat/HookHelper",
                            "hookFragmentSetUserVisibleHint",
                            "(Landroidx/fragment/app/Fragment;Z)V", false);
                    System.out.println("已插入 " + className + " " + methodName);
                }
            };
        }
    }

    // Hook 方法 end
    //----------------------------------------------------------------------------------------------

}
