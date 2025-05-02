package narek.dallakyan.dragging.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.dokka.gradle.DokkaExtension

class DokkaPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        with(pluginManager) {
            apply(libs.findPlugin("kotlin-dokka"))
        }

        extensions.configure<DokkaExtension> {
            dokkaSourceSets.configureEach {
                sourceLink {
                    remoteUrl("https://github.com/ItNarekDallakyan/ComposeDragAndDrop")
                }

                includes.from(
                    rootProject.file("README.md"),
                )
            }
        }

        with(dependencies) {
            add("dokkaPlugin", libs.findLibrary("kotlin-dokka-android").get())
        }
    }
}
