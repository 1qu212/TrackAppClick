package cn.xydzjnq.asmplugin

import com.android.build.api.transform.Context
import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import groovy.io.FileType
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils

class TrackTransform extends Transform {

    /**
     * 它会出现在app/build/intermediates/transforms目录下
     * @return
     */
    @Override
    String getName() {
        return "TrackTransform"
    }

    /**
     * 1.CONTENT_CLASS  处理class
     * 2.CONTENT_JARS   处理jars
     * 3.CONTENT_RESOURCES  处理resources
     * 4.CONTENT_NATIVE_LIBS    处理native_libs
     * 5.CONTENT_DEX    处理dex
     * 6.CONTENT_DEX_WITH_RESOURCES     处理dex和resources
     * @return
     */
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * SCOPE_FULL_PROJECT   扫描整个project
     * @return
     */
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    /**
     *
     * @return  是否增量构建
     */
    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        if (!incremental) {
            outputProvider.deleteAll()
        }

        /**Transform 的 inputs 有两种类型，一种是目录，一种是 jar 包，要分开遍历 */
        inputs.each { TransformInput input ->
            /**遍历目录*/
            input.directoryInputs.each { DirectoryInput directoryInput ->
                /**当前这个 Transform 输出目录*/
                File dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                File dir = directoryInput.file

                if (dir) {
                    HashMap<String, File> modifyMap = new HashMap<>()
                    /**遍历以某一扩展名结尾的文件*/
                    dir.traverse(type: FileType.FILES, nameFilter: ~/.*\.class/) {
                        File classFile ->
                            if (ClassModifier.isShouldModify(classFile.name)) {
                                File modified = ClassModifier.modifyClassFile(dir, classFile, context.getTemporaryDir())
                                if (modified != null) {
                                    /**key 为包名 + 类名，如：/cn/sensorsdata/autotrack/android/app/MainActivity.class*/
                                    String ke = classFile.absolutePath.replace(dir.absolutePath, "")
                                    modifyMap.put(ke, modified)
                                }
                            }
                    }
                    FileUtils.copyDirectory(directoryInput.file, dest)
                    modifyMap.entrySet().each {
                        Map.Entry<String, File> en ->
                            File target = new File(dest.absolutePath + en.getKey())
                            if (target.exists()) {
                                target.delete()
                            }
                            FileUtils.copyFile(en.getValue(), target)
                            en.getValue().delete()
                    }
                }
            }

            /**遍历 jar*/
            input.jarInputs.each { JarInput jarInput ->
                String destName = jarInput.file.name

                /**截取文件路径的 md5 值重命名输出文件,因为可能同名,会覆盖*/
                def hexName = DigestUtils.md5Hex(jarInput.file.absolutePath).substring(0, 8)
                /** 获取 jar 名字*/
                if (destName.endsWith(".jar")) {
                    destName = destName.substring(0, destName.length() - 4)
                }

                /** 获得输出文件*/
                File dest = outputProvider.getContentLocation(destName + "_" + hexName, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                def modifiedJar = ClassModifier.modifyJar(jarInput.file, context.getTemporaryDir(), true)
                if (modifiedJar == null) {
                    modifiedJar = jarInput.file
                }
                FileUtils.copyFile(modifiedJar, dest)
            }
        }
    }
}
