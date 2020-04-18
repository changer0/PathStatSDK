package com.yuewen.cooperate.pathstat.asm.hockclasses;

import org.objectweb.asm.MethodVisitor;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanglulu on 2020/4/18.
 * for Hock 基类
 */
abstract public class HockClass {
    public String className;
    public List<HockMethod> methodNameList = new ArrayList<>();

    public HockClass(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return className;
    }
}
