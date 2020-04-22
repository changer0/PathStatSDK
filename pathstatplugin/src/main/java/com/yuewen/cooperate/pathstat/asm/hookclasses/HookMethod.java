package com.yuewen.cooperate.pathstat.asm.hookclasses;

import org.objectweb.asm.MethodVisitor;

/**
 * Created by zhanglulu on 2020/4/18.
 * for Hook 基方法
 */
public abstract class HookMethod {
    public HookMethod(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String name;
    public String desc;
    /**
     * 插桩
     */
    public abstract MethodVisitor insertMethod(String[] mInterfaces, String mClassName, String superName, MethodVisitor methodVisitor, int access, String methodName, String desc);

}
