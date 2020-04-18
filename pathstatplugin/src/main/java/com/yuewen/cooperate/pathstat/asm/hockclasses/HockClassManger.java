package com.yuewen.cooperate.pathstat.asm.hockclasses;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanglulu on 2020/4/18.
 * for
 */
public class HockClassManger {
    public static String isDebug = "false";

    //----------------------------------------------------------------------------------------------
    // 自定义 Hock 的类
    public static String customViewPagerClass;
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
    //  自定义 Hock 的类 end
    //----------------------------------------------------------------------------------------------

    /**
     * 需要 Hock 的类
     */
    private static List<HockClass> hockClassList = new ArrayList<>();
    static {
        hockClassList.add(new HockViewPager());
        hockClassList.add(new HockFragment());
    }

    public static HockClass matchingClass(String className) {
        for (HockClass hockClass : hockClassList) {
            if (className.equals(hockClass.className)) {
                return hockClass;
            }
        }
        if (isMatchingCustomClass(className)) {
            return new HockViewPager();
        }
        return null;
    }
    public static boolean isMatchingMethod(HockClass hockClass, String name, String desc) {
        for (HockMethod method : hockClass.methodNameList) {
            if (name.equals(method.name) && desc.equals(method.desc)) {
                return true;
            }
        }
        return false;
    }
}
