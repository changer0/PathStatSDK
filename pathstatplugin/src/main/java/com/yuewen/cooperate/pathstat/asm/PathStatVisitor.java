package com.yuewen.cooperate.pathstat.asm;

import com.yuewen.cooperate.pathstat.asm.hookclasses.HookClass;
import com.yuewen.cooperate.pathstat.asm.hookclasses.HookClassManger;
import com.yuewen.cooperate.pathstat.asm.hookclasses.HookMethod;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import javax.annotation.Nullable;

/**
 * ASM 类观察者
 * 核心操作
 */
class PathStatVisitor extends ClassVisitor {
    private String mClassName;
    private String[] mInterfaces;
    private String superName;
    @Nullable
    private HookClass mHookClass;

    PathStatVisitor() {
        super(Opcodes.ASM4);
    }

    PathStatVisitor( ClassVisitor cv) {
        super(Opcodes.ASM4, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        mClassName = name;
        mInterfaces = interfaces;
        mHookClass = HookClassManger.matchingClass(name);
        this.superName = superName;
        if (mHookClass != null) {
            System.out.println("---------开始遍历类 Start---------");
        } else {
            //被过滤掉的类
        }
        if (HookClassManger.isDebug) {
            System.out.println("className: " + name);
        }
        super.visit(version, access, name, signature, superName, interfaces);
    }

    /**
     * 核心修改方法
     * @param access
     * @param name
     * @param desc
     * @param signature
     * @param exceptions
     * @return
     */
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
        MethodVisitor adapter = null;
        if (HookClassManger.isDebug) {
            System.out.println("Method name：" + name + " desc: " + desc + " mHookClass: " + mHookClass);
        }
        HookMethod hookMethod = HookClassManger.matchingMethod(mHookClass, name, desc);
        if (hookMethod != null) {
            try {
                adapter = hookMethod.insertMethod(mInterfaces, mClassName, superName, methodVisitor, access, name, desc);
            } catch (Exception e) {
                e.printStackTrace();
                adapter = null;
            }
        } else {
            //被过滤掉的方法
            adapter = new PathStatMethodVisitor(methodVisitor, access, name,desc);
        }
        if (adapter != null) {
            return adapter;
        }
        return methodVisitor;
    }

    @Override
    public void visitEnd() {
        if (mHookClass != null) {
            System.out.println("---------开始遍历类 End--------");
        }
        super.visitEnd();
    }
}