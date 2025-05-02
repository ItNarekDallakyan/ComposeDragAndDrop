package narek.dallakyan.dragging.convention

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

class AndroidApplicationPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        with(pluginManager) {
            apply(libs.findPlugin("android-application"))
            apply(libs.findPlugin("kotlin-android"))
        }

        with(extensions.getByType<AppExtension>()) {
            namespace = "narek.dallakyan.dragging"
            compileSdkVersion = libs.findVersionString("compileSdk")

            with(defaultConfig) {
                applicationId = "narek.dallakyan.dragging"
                minSdk = libs.findVersionInt("minSdk")
                targetSdk = libs.findVersionInt("targetSdk")
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }

            with(compileOptions) {
                sourceCompatibility = JDK_VERSION
                targetCompatibility = JDK_VERSION
            }
        }

        with(extensions.getByType<KotlinAndroidProjectExtension>()) {
            compilerOptions.jvmTarget = JVM_TARGET
        }
    }
}
