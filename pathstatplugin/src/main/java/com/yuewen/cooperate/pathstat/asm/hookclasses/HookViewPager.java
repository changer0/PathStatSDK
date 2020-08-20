package com.yuewen.cooperate.pathstat.asm.hookclasses;

import com.yuewen.cooperate.pathstat.asm.PathStatMethodVisitor;

import org.objectweb.asm.MethodVisitor;


/**
 * Created by zhanglulu on 2020/4/18.
 * for 注意这个 Hook 类可以传入两个构造方法，包含自定义的 ViewPager
 */
public class HookViewPager extends HookClass {

    public HookViewPager() {
        super("androidx/viewpager/widget/ViewPager");
        methodNameList.add(new HookSetAdapter());
    }

    public HookViewPager(String className) {
        super(className);
        methodNameList.add(new HookSetAdapter());
    }

    public class HookSetAdapter extends HookMethod {

        public HookSetAdapter() {
            super("setAdapter", "(Landroidx/viewpager/widget/PagerAdapter;)V");
        }

        @Override
        public MethodVisitor insertMethod(String[] mInterfaces, String mClassName, String superName, MethodVisitor methodVisitor, int access, String methodName, String desc) {
            return new PathStatMethodVisitor(methodVisitor, access, methodName, desc) {
                @Override
                protected void onMethodExit(int i) {
                    super.onMethodExit(i);
                    // ALOAD 25 获取方法的第一个参数
                    methodVisitor.visitVarInsn(ALOAD, 0);
                    // INVOKESTATIC INVOKESTATIC
                    methodVisitor.visitMethodInsn(INVOKESTATIC,
                            "com/yuewen/cooperate/pathstat/HookHelper",
                            "hookViewPagerSetAdapter",
                            "(Ljava/lang/Object;)V", false);
                    System.out.println("已插入 " + className + " " + methodName);
                }
            };
        }
    }
}
