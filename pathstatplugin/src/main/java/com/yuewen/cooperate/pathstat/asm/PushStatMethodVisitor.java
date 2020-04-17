package com.yuewen.cooperate.pathstat.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;


/**
 * Created by zhanglulu on 2020/4/17.
 */

public class PushStatMethodVisitor extends AdviceAdapter {
    String methodName = "";


    protected PushStatMethodVisitor(MethodVisitor methodVisitor, int access, String name, String desc) {
        super(Opcodes.ASM4, methodVisitor, access, name, desc);
        methodName = name;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String s, boolean b) {
        //System.out.println(methodName + "visitAnnotation" + s + "===============================");
        return super.visitAnnotation(s, b);
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();
        //System.out.println(methodName + "onMethodEnter" + "===============================");
    }

    @Override
    protected void onMethodExit(int i) {
        super.onMethodExit(i);
        //System.out.println(methodName + "onMethodExit" + "===============================");
    }


}
