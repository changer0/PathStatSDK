package com.yuewen.cooperate.pathstat.asm;

import com.yuewen.cooperate.pathstat.asm.hockclasses.HockClass;
import com.yuewen.cooperate.pathstat.asm.hockclasses.HockClassManger;

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
    private HockClass mHockClass;

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
        mHockClass = HockClassManger.matchingClass(name);
        this.superName = superName;
        if (mHockClass != null) {
            System.out.println("---------开始遍历类 Start---------");
        } else {
            //被过滤掉的类
        }
        if (HockClassManger.isDebug.equals("true")) {
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
        if (HockClassManger.isDebug.equals("true")) {
            System.out.println("Method name：" + name + " desc: " + desc + " mHockClass: " + mHockClass);
        }
        if ((mHockClass != null && HockClassManger.isMatchingMethod(mHockClass, name, desc))) {
            try {
                adapter = mHockClass.insertMethod(mInterfaces, mClassName, superName, methodVisitor, access, name, desc);
            } catch (Exception e) {
                e.printStackTrace();
                adapter = null;
            }
        } else {
            //被过滤掉的方法
            adapter = new PushStatMethodVisitor(methodVisitor, access, name,desc);
        }
        if (adapter != null) {
            return adapter;
        }
        return methodVisitor;
    }

    @Override
    public void visitEnd() {
        if (mHockClass != null) {
            System.out.println("---------开始遍历类 End--------");
        }
        super.visitEnd();
    }
}