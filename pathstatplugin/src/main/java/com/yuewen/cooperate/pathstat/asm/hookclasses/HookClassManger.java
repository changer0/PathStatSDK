package com.yuewen.cooperate.pathstat.asm.hookclasses;

import org.apache.http.util.TextUtils;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Created by zhanglulu on 2020/4/18.
 * for
 */
public class HookClassManger {
    public static String isDebug = "false";

    //----------------------------------------------------------------------------------------------
    // 自定义 Hook 的类
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
    //  自定义 Hook 的类 end
    //----------------------------------------------------------------------------------------------

    /**
     * 需要 Hook 的类
     */
    private static List<HookClass> hookClassList = new ArrayList<>();
    static {
        hookClassList.add(new HookViewPager());
        hookClassList.add(new HookFragment());
    }

    public static HookClass matchingClass(String className) {
        if (TextUtils.isEmpty(className)) {
            return null;
        }
        for (HookClass hookClass : hookClassList) {
            if (className.equals(hookClass.className)) {
                return hookClass;
            }
        }
        if (isMatchingCustomClass(className)) {
            return new HookViewPager(className);
        }
        return null;
    }
    public static HookMethod matchingMethod(@Nullable HookClass hookClass, String name, String desc) {
        if (hookClass == null || TextUtils.isEmpty(name) || TextUtils.isEmpty(desc)) {
            return null;
        }
        for (HookMethod method : hookClass.methodNameList) {
            if (name.equals(method.name) && desc.equals(method.desc)) {
                return method;
            }
        }
        return null;
    }
}
