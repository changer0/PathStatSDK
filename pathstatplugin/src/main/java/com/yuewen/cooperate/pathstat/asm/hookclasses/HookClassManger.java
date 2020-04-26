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
    public static boolean isDebug = false;
    private static int curMatchClassSize;
    /**
     * 需要 Hook 的类
     */
    private static List<HookClass> hookClassList = new ArrayList<>();
    static {
        hookClassList.add(new HookViewPager());
        hookClassList.add(new HookFragment());

        curMatchClassSize = hookClassList.size();
    }

    public static HookClass matchingClass(String className) {
        if (TextUtils.isEmpty(className)) {
            return null;
        }
        for (HookClass hookClass : hookClassList) {
            if (className.equals(hookClass.className)) {
                curMatchClassSize--;
                return hookClass;
            }
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

    /**
     *  是否所有类都已经匹配结束，节省编译时间
     * @return
     */
    public static boolean isAllClassMatchFinish() {
        System.out.println("curMatchClassSize：" + curMatchClassSize);
        if (curMatchClassSize <= 0) {
            return true;
        }
        return false;
    }
}
