package com.yuewen.cooperate.pathstat.asm;

import com.yuewen.cooperate.pathstat.asm.hookclasses.HookClassManger;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

/**
 * 做相关类的修改
 */
public class PathStatModify {

    /**
     * 修改 Jar
     * @param jarFile
     * @param tempDir
     * @param nameHex
     * @return
     * @throws IOException
     */
    public static File modifyJar(File jarFile, File tempDir, boolean nameHex) throws IOException {
        /**
         * 读取原jar
         */
        JarFile file = null;
        file = new JarFile(jarFile);

        /** 设置输出到的jar */
        String hexName = "";
        if (nameHex) {
            hexName = DigestUtils.md5Hex(jarFile.getAbsolutePath()).substring(0, 8);
        }
        File outputJar = new File(tempDir, hexName + jarFile.getName());
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(outputJar));
        Enumeration enumeration = file.entries();

        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement();
            InputStream inputStream = file.getInputStream(jarEntry);
            String entryName = jarEntry.getName();
            ZipEntry zipEntry = new ZipEntry(entryName);
            jarOutputStream.putNextEntry(zipEntry);
            byte[] modifiedClassBytes = null;
            byte[] sourceClassBytes = IOUtils.toByteArray(inputStream);
            if (entryName.endsWith(".class") && !entryName.endsWith("R.class")
                    && !entryName.endsWith("BuildConfig.class")) {
                modifiedClassBytes = modifyClasses(sourceClassBytes);
            }
            if (modifiedClassBytes == null) {
                jarOutputStream.write(sourceClassBytes);
            } else {
                jarOutputStream.write(modifiedClassBytes);
            }
            jarOutputStream.closeEntry();
        }
        jarOutputStream.close();
        file.close();
        return outputJar;
    }

    /**
     * 修改类
     * @param srcByteCode
     * @return
     */
    public static byte[] modifyClasses(byte[] srcByteCode) {
        if (HookClassManger.isAllClassMatchFinish()) {
            //为了加快编译时间，如果所有类都匹配结束了，直接返回
            if (HookClassManger.isDebug) {
                System.out.println("modifyClasses: 已匹配完成，无需要继续执行");
            }
            return srcByteCode;
        }
        byte[] classBytesCode = null;
        try {
            classBytesCode = modifyClass(srcByteCode);
            if (classBytesCode == null) {
                classBytesCode = srcByteCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return classBytesCode;
        }
    }

    static byte[] modifyClass(byte[] srcByteCode) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor classVisitor = new PathStatVisitor(classWriter);
        ClassReader classReader = new ClassReader(srcByteCode);
        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }

}