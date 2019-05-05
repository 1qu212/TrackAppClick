package cn.xydzjnq.asmplugin

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class ClassModifier {
    private static HashSet<String> exclude = new HashSet<>()
    /**
     * 可以自定义一些过滤文件
     */
    static {
        exclude = new HashSet<>()
        exclude.add('android.support')
        exclude.add('cn.xydzjnq.track.api')
    }

    /**
     * .class文件是否可能需要修改
     * @param className
     * @return
     */
    protected static boolean isShouldModify(String className) {
        Iterator<String> iterator = exclude.iterator()
        while (iterator.hasNext()) {
            String packageName = iterator.next()
            if (className.startsWith(packageName)) {
                return false
            }
        }

        if (className.contains('R$') ||
                className.contains('R2$') ||
                className.contains('R.class') ||
                className.contains('R2.class') ||
                className.contains('BuildConfig.class')) {
            return false
        }

        return true
    }

    /**
     * 处理.class文件
     * @param dir
     * @param classFile
     * @param tempDir
     * @return
     */
    static File modifyClassFile(File dir, File classFile, File tempDir) {
        File modified = null
        try {
            String className = path2ClassName(classFile.absolutePath.replace(dir.absolutePath + File.separator, ""))
            byte[] sourceClassBytes = IOUtils.toByteArray(new FileInputStream(classFile))
            byte[] modifiedClassBytes = modifyClass(sourceClassBytes)
            if (modifiedClassBytes) {
                modified = new File(tempDir, className.replace('.', '') + '.class')
                if (modified.exists()) {
                    modified.delete()
                }
                modified.createNewFile()
                new FileOutputStream(modified).write(modifiedClassBytes)
            }
        } catch (Exception e) {
            e.printStackTrace()
            modified = classFile
        }
        return modified
    }

    static String path2ClassName(String pathName) {
        pathName.replace(File.separator, ".").replace(".class", "")
    }

    /**
     * 处理jar文件
     * @param jarFile
     * @param tempDir
     * @param nameHex
     * @return
     */
    static File modifyJar(File jarFile, File tempDir, boolean nameHex) {
        /**
         * 读取原 jar
         */
        def file = new JarFile(jarFile)

        /**
         * 设置输出到的 jar
         */
        def hexName = ""
        if (nameHex) {
            hexName = DigestUtils.md5Hex(jarFile.absolutePath).substring(0, 8)
        }
        def outputJar = new File(tempDir, hexName + jarFile.name)
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(outputJar))
        Enumeration enumeration = file.entries()

        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement()
            InputStream inputStream = file.getInputStream(jarEntry)

            String entryName = jarEntry.getName()
            String className

            ZipEntry zipEntry = new ZipEntry(entryName)

            jarOutputStream.putNextEntry(zipEntry)

            byte[] modifiedClassBytes = null
            byte[] sourceClassBytes = IOUtils.toByteArray(inputStream)
            if (entryName.endsWith(".class")) {
                className = entryName.replace("/", ".").replace(".class", "")
                if (isShouldModify(className)) {
                    modifiedClassBytes = modifyClass(sourceClassBytes)
                }
            }
            if (modifiedClassBytes == null) {
                modifiedClassBytes = sourceClassBytes
            }
            jarOutputStream.write(modifiedClassBytes)
            jarOutputStream.closeEntry()
        }
        jarOutputStream.close()
        file.close()
        return outputJar
    }

    /**
     * 处理class字节流
     * @param srcClass
     * @return
     * @throws IOException
     */
    private static byte[] modifyClass(byte[] srcClass) throws IOException {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS)
        ClassVisitor classVisitor = new ASMClassVisitor(classWriter)
        ClassReader cr = new ClassReader(srcClass)
        cr.accept(classVisitor, ClassReader.EXPAND_FRAMES)
        return classWriter.toByteArray()
    }
}
