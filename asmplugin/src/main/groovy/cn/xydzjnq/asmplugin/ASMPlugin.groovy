package cn.xydzjnq.asmplugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class ASMPlugin implements Plugin<Project> {
    void apply(Project project) {
        AppExtension appExtension = project.extensions.findByType(AppExtension.class)
        appExtension.registerTransform(new TrackTransform())
    }
}