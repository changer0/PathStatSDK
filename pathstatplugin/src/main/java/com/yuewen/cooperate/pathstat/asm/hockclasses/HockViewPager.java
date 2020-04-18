package com.yuewen.cooperate.pathstat.asm.hockclasses;

import com.yuewen.cooperate.pathstat.asm.PushStatMethodVisitor;

import org.objectweb.asm.MethodVisitor;


/**
 * Created by zhanglulu on 2020/4/18.
 * for 注意这个 Hock 类可以传入两个构造方法，包含自定义的 ViewPager
 */
public class HockViewPager extends HockClass {

    public HockViewPager() {
        super("androidx/viewpager/widget/ViewPager");
        methodNameList.add(new HockSetAdapter());
    }

    public HockViewPager(String className) {
        super(className);
        methodNameList.add(new HockSetAdapter());
    }

    public class HockSetAdapter extends HockMethod {

        public HockSetAdapter() {
            super("setAdapter", "(Landroidx/viewpager/widget/PagerAdapter;)V");
        }

        @Override
        public MethodVisitor insertMethod(String[] mInterfaces, String mClassName, String superName, MethodVisitor methodVisitor, int access, String methodName, String desc) {
            return new PushStatMethodVisitor(methodVisitor, access, methodName, desc) {
                @Override
                protected void onMethodExit(int i) {
                    super.onMethodExit(i);
                    // ALOAD 25 获取方法的第一个参数
                    methodVisitor.visitVarInsn(ALOAD, 0);
                    // INVOKESTATIC INVOKESTATIC
                    methodVisitor.visitMethodInsn(INVOKESTATIC,
                            "com/yuewen/cooperate/pathstat/HockHelper",
                            "hockViewPagerSetAdapter",
                            "(Ljava/lang/Object;)V", false);
                    System.out.println("已插入 " + className + " " + methodName);
                }
            };
        }
    }
}
