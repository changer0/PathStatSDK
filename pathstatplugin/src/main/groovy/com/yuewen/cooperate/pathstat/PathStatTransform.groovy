package com.yuewen.cooperate.pathstat

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Status
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.yuewen.cooperate.pathstat.asm.hookclasses.HookClassManger
import com.yuewen.cooperate.pathstat.asm.PathStatModify
import com.yuewen.cooperate.pathstat.utils.TextUtil
import groovy.io.FileType
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import com.yuewen.cooperate.pathstat.utils.Logger
import org.gradle.api.Project

/**
 * 遍历所有文件更换字节码
 */
class PathStatTransform extends Transform {
    private Project mProject

    PathStatTransform(Project project) {
        mProject = project
    }
    // 设置我们自定义的Transform对应的Task名称
    // 类似：transformClassesWithPreDexForXXX
    // 这里应该是：transformClassesWithInjectTransformForxxx
    @Override
    String getName() {
        return "com.yuewen.cooperate.pathstat.asm.PathStat"
    }

    // 指定输入的类型，通过这里的设定，可以指定我们要处理的文件类型
    //  这样确保其他类型的文件不会传入
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    // 指定Transform的作用范围
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    // 当前Transform是否支持增量编译
    @Override
    boolean isIncremental() {
        return true
    }

    // 核心方法
    // inputs是传过来的输入流，有两种格式：jar和目录格式
    // outputProvider 获取输出目录，将修改的文件复制到输出目录，必须执行
    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        boolean isIncremental = transformInvocation.isIncremental()
        HookClassManger.isDebug = mProject.extensions.getByType(PathExtensions).getIsDebug()
        println "idDebug：${HookClassManger.isDebug}"
        println "isIncremental：$isIncremental"
        if(!isIncremental) {
            transformInvocation.getOutputProvider().deleteAll()
        }
        Logger.info("||=================================================||")
        Logger.info("||              PathStatSDK 开始计时                ||")
        Logger.info("||=================================================||")
        HookClassManger.initMatchClassSize()
        def startTime = System.currentTimeMillis()
        transformInvocation.getInputs().each {
            TransformInput input ->
                //对类型为jar文件的input进行遍历
                input.jarInputs.each {
                    JarInput jarInput ->
                        forEachJar(isIncremental, jarInput, transformInvocation)
                }
                // 遍历文件夹
                //文件夹里面包含的是我们手写的类以及R.class、BuildConfig.class以及R$XXX.class等
                input.directoryInputs.each {
                    DirectoryInput directoryInput ->
                        forEachDirectory(isIncremental, directoryInput, transformInvocation)
                }
        }
        //计算耗时
        def cost = (System.currentTimeMillis() - startTime) / 1000
        Logger.info("||=================================================||")
        Logger.info("||                总耗时：${cost}秒                  ||")
        Logger.info("||=================================================||")
    }


    /**
     * 遍历 Jar 文件
     * @param isIncremental
     * @param jarInput
     * @param transformInvocation
     */
    private static void forEachJar(boolean isIncremental, JarInput jarInput, TransformInvocation transformInvocation) {
        String destName = jarInput.file.name
        /** 截取文件路径的md5值重命名输出文件,因为可能同名,会覆盖*/
        def hexName = DigestUtils.md5Hex(jarInput.file.absolutePath).substring(0, 8)
        if (destName.endsWith(".jar")) {
            destName = destName.substring(0, destName.length() - 4)
        }
        /** 获得输出文件*/
        File dest = transformInvocation.getOutputProvider().getContentLocation(destName + "_" + hexName, jarInput.contentTypes, jarInput.scopes, Format.JAR)
        if (HookClassManger.isDebug) {
            Logger.info("开始遍历特定jar: ${dest.absolutePath} jarInput.status: ${jarInput.status}")
        }
        if (isIncremental) {
            switch (jarInput.status) {
                case Status.NOTCHANGED:
                    break
                case Status.ADDED:
                case Status.CHANGED:
                    copyJarToDest(jarInput, dest, transformInvocation.context.getTemporaryDir())
                    break
                case Status.REMOVED:
                    if (dest.exists()) {
                        FileUtils.forceDelete(dest)
                    }
                    break
            }
        } else {
            copyJarToDest(jarInput, dest, transformInvocation.context.getTemporaryDir())
        }
    }

    /**
     * 将 Jar 复制到输出目录
     */
    private static void copyJarToDest(JarInput jarInput, File dest, File tempDir) {
        def modifiedJar = null
        //如果已经遍历完成我们需要遍历的 class，就无需 modifyJarFile
        if (!HookClassManger.isAllClassMatchFinish()) {
            modifiedJar = modifyJarFile(jarInput.file, tempDir)
        } else {
            if (HookClassManger.isDebug) {
                Logger.info("该 Jar 包忽略: ${dest.absolutePath}")
            }
        }
        if (modifiedJar == null) {
            modifiedJar = jarInput.file
        }
        // 将input的目录复制到output指定目录
        FileUtils.copyFile(modifiedJar, dest)
    }

    /**
     * Jar文件中修改对应字节码
     */
    private static File modifyJarFile(File jarFile, File tempDir) {
        if (jarFile) {
            return PathStatModify.modifyJar(jarFile, tempDir, true)

        }
        return null
    }

    /**
     * 遍历目录
     * @param isIncremental
     * @param directoryInput
     * @param transformInvocation
     */
    private static void forEachDirectory(boolean isIncremental, DirectoryInput directoryInput, TransformInvocation transformInvocation) {
        File dest = transformInvocation.getOutputProvider().getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
        FileUtils.forceMkdir(dest)
        File dir = directoryInput.file
        String srcDirPath = dir.absolutePath
        String destDirPath = dest.absolutePath
        if (isIncremental) {
            //遍历所有的带有状态的文件
            Map<File, Status> fileStatusMap = directoryInput.getChangedFiles()
            for (Map.Entry<File, Status> changedFile: fileStatusMap.entrySet()) {
                Status status = changedFile.getValue()
                File inputFile = changedFile.getKey()
                String destFilePath = inputFile.absolutePath.replace(srcDirPath, destDirPath)
                if (HookClassManger.isDebug) {
                    Logger.info("目录 status = $status: $inputFile.absolutePath")
                    Logger.info("目录 srcDirPath: $srcDirPath")
                    Logger.info("目录 destDirPath: $destDirPath")
                    Logger.info("目录 destFilePath: $destFilePath")
                }
                File destFile = new File(destFilePath)
                switch (status) {
                    case Status.NOTCHANGED:
                        break
                    case Status.REMOVED:
                        if (destFile.exists()) {
                            destFile.delete()
                        }
                        break
                    case Status.ADDED:
                    case Status.CHANGED:
                        if (inputFile.isDirectory()) {//如果返回的是文件夹就不进行处理
                            if (destFile.isDirectory() && !destFile.exists()) {
                                destFile.mkdirs()
                            }
                        } else {
                            File modified = modifyClassFile(dir, inputFile, transformInvocation.getContext().getTemporaryDir())
                            if (destFile.exists()) {
                                destFile.delete()
                            }
                            if (modified != null) {
                                FileUtils.copyFile(modified, destFile)
                                modified.delete()
                            } else {
                                FileUtils.copyFile(inputFile, destFile)
                            }
                        }
                        break
                    default:
                        break
                }
            }
        } else {
            FileUtils.copyDirectory(dir, dest)
            dir.traverse(type: FileType.FILES, nameFilter: ~/.*\.class/) {
                File inputFile ->
                    //过滤掉 BuildConfig.class R 等文件
                    if (!inputFile.name.endsWith("R.class")
                            && !inputFile.name.endsWith("BuildConfig.class")
                            && !inputFile.name.contains("R\$") && !HookClassManger.isAllClassMatchFinish()) {
                        File modified = modifyClassFile(dir, inputFile, transformInvocation.getContext().getTemporaryDir())
                        if (modified != null) {
                            File target = new File(inputFile.absolutePath.replace(srcDirPath, destDirPath))
                            if (target.exists()) {
                                target.delete()
                            }
                            FileUtils.copyFile(modified, target)
                            modified.delete()
                        }
                    } else {
                        if (HookClassManger.isDebug) {
                            Logger.info("该 class 忽略: ${inputFile.name}")
                        }
                    }

            }
        }
    }


    /**
     * 开发写得 Class
     * @param dir
     * @param classFile
     * @param tempDir
     * @return
     */
    private static File modifyClassFile(File dir, File classFile, File tempDir) {
        File modified = null
        try {
            String className = TextUtil.path2ClassName(classFile.absolutePath.replace(dir.absolutePath + File.separator, ""))
            byte[] sourceClassBytes = IOUtils.toByteArray(new FileInputStream(classFile))
            byte[] modifyClassBytes = PathStatModify.modifyClasses(sourceClassBytes)
            if (modifyClassBytes) {
                modified = new File(tempDir, className.replace('.', '') + '.class')
                if (modified.exists()) {
                    modified.delete()
                }
                modified.createNewFile()
                new FileOutputStream(modified).write(modifyClassBytes)
            }
        } catch (Exception e) {
            e.printStackTrace()
        }
        return modified
    }
}