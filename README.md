# TrackAppClick
点击事件自动埋点
***

### App模块
各种点击事件埋点测试

### track-api模块
#### TrackAPI
用于客户端进行初始化
#### TrackHelper
用于gradle plugin调用
#### TrackPrivateAPI
一些track-api模块内部调用的api
#### TrackViewOnClick
用于对自定义方法埋点（对xml中的onClick方法注解，使其也能埋点）
***

### aspectJ方案
#### aspectjplugin模块
Step1：创建groovy目录，并新建一个实现Plugin<Project>接口的类（该类apply方法中会指定执行一些task，我会指定其在项目编译时执行ajc编译织入增强（Advice）代码）。
Step2：新建properties文件在resources/META-INF/grdle-plugins目录下创建一个aspectjplugin.properties文件，其中aspectjplugin就是以后引用的插件名。
#### aspectjapi
Step1：定义切面Aspect；
Step2：apply plugin: 'aspectjplugin'
#### App使用aspectJ方案
Step1：
    implementation 'cn.xydzjnq:track-api:1.0.0@aar'（这是所有方案所必须的）
    implementation 'cn.xydzjnq:aspectjapi:1.0.0@aar'
Step2：apply plugin: 'aspectjplugin'
***

### ASM方案
### asmplugin模块
Step1：创建groovy目录，并新建一个实现Plugin<Project>接口的类（该类apply方法中会使用自定义的Transform）。
Step2：新建properties文件在resources/META-INF/grdle-plugins目录下创建一个asmplugin.properties文件，其中asmplugin就是以后引用的插件名。
Step3：TrackTransform文件主要是transform()方法（在其中会对TransformInput进行处理，最终输出到outputProvider中）。
Step4：ClassModifier文件主要将.class文件或jar文件处理成字节流，最终调用自定义的ClassVisitor来修改文件。
Step5：ASMClassVisitor文件（自定义的ClassVisitor）在visitMethod()中访问需要修改的方法，并通过自定义AdviceAdapter的onMethodExit()修改符合特定条件的方法。
Step6：生成gradle plugin。
#### App使用ASM方案
Step1：
    implementation 'cn.xydzjnq:track-api:1.0.0@aar'（这是所有方案所必须的）
Step2：apply plugin: 'asmplugin'
***
