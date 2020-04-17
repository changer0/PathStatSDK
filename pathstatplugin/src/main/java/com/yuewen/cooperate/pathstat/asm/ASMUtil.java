package com.yuewen.cooperate.pathstat.asm;

import org.objectweb.asm.MethodVisitor;

/**
 * Created by zhanglulu on 2020/4/17.
 */

public class ASMUtil {

    public static String customViewPagerClass;
    public static String isPrintAllClass = "false";
    public static String isPrintAllMethod = "false";

    private static boolean isMatchingCustomClass(String className) {
        //用户自定义的 ViewPager
        if (customViewPagerClass == null || customViewPagerClass.length() < 3) {
            return false;
        }
        String temStr = customViewPagerClass.substring(1, customViewPagerClass.length() - 1);
        String[] classesArray = temStr.split(",");
        if (classesArray.length > 0) {
            for (String viewPagerClass : classesArray) {
                if (viewPagerClass.equals(className)) {
                    System.out.println("存在 customViewPagerClass: " + className);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 类是否满足匹配条件，满足的才会允许修改其中的方法
     *
     * @param className  类名
     * @param interfaces 类的实现接口
     */
    public static boolean isMatchingClass(String className, String[] interfaces) {
        //System.out.println("ClassName: " + className);
        if ("androidx/viewpager/widget/ViewPager".equals(className)) {
            System.out.println("找到 ViewPager : className:" + className);
            return true;
        }
        if (isMatchingCustomClass(className)) {
            return true;
        }

        //剔除掉以android开头的类，即系统类，以避免出现不可预测的bug 目前没有意义！
        //if (className.startsWith("android")) {
        //    return false;
        //}
        return false;
    }


    /**
     * 接口名是否匹配
     *
     * @param interfaces    类的实现接口
     * @param interfaceName 需要匹配的接口名
     */
    private static boolean isMatchingInterfaces(String[] interfaces, String interfaceName) {
        boolean isMatch = false;
        // 是否满足实现的接口
        for (String anInterface : interfaces) {
            if (anInterface.equals(interfaceName)) {
                isMatch = true;
            }
        }
        return isMatch;
    }


    /**
     * 方法是否匹配到，根据方法名和参数的描述符来确定一个方法是否需要修改的初步条件，
     * 可以扩展自己想监听的方法
     *
     * @param name 方法名
     * @param desc 参数的方法的描述符
     */
    static boolean isMatchingMethod(String name, String desc) {
        if (name.equals("setAdapter") && desc.equals("(Landroidx/viewpager/widget/PagerAdapter;)V")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 核心修改方法
     * @param mInterfaces
     * @param mClassName
     * @param superName
     * @param methodVisitor
     * @param access
     * @param name
     * @param desc
     * @return
     */
    public static MethodVisitor getMethodVisitor(String[] mInterfaces, String mClassName, String superName, MethodVisitor methodVisitor, int access, String name, String desc) {
        MethodVisitor adapter = null;
        if (name.equals("setAdapter")) {
            System.out.println("||* visitMethod * setAdapter in ViewPager");
            adapter = new PushStatMethodVisitor(methodVisitor, access, name, desc) {
                @Override
                protected void onMethodExit(int i) {
                    super.onMethodExit(i);
                    // ALOAD 25 获取方法的第一个参数
                    methodVisitor.visitVarInsn(ALOAD, 0);
                    // INVOKESTATIC INVOKESTATIC
                    methodVisitor.visitMethodInsn(INVOKESTATIC,
                            "com/yuewen/cooperate/pathstat/HockHelper",
                            "setViewPagerAdapter",
                            "(Ljava/lang/Object;)V", false);
                    System.out.println("||* visitMethod * setViewPagerAdapter 已调用");
                }
            };
        }
        return adapter;
    }
}
