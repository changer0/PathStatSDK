package com.yuewen.cooperate.pathstat.asm.hookclasses;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanglulu on 2020/4/18.
 * for Hook 基类
 */
abstract public class HookClass {
    public String className;
    public List<HookMethod> methodNameList = new ArrayList<>();

    public HookClass(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return className;
    }
}
