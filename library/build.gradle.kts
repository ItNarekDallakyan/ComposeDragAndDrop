plugins {
    id("plugins.android.library")
    id("plugins.dokka")
    alias(libs.plugins.vanniktech.publish)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "narek.dallakyan.dragging"
}

mavenPublishing {
    coordinates(
        groupId = "io.github.ItNarekDallakyan",
        artifactId = "composedraganddrop",
        version = "1.0.0"
    )

    pom {
        name.set("ComposeDragAndDrop")
        description.set("A lightweight and customizable Jetpack Compose library for drag-and-drop functionality")
        url.set("https://github.com/ItNarekDallakyan/ComposeDragAndDrop")
        licenses {
            license {
                name.set("Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0")
            }
        }
        developers {
            developer {
                id.set("ItNarekDallakyan")
                name.set("Narek Harutyunyan")
                email.set("it.narek.dallakyan@gmail.com")
            }
        }
        scm {
            url.set("https://github.com/ItNarekDallakyan/ComposeDragAndDrop")
        }
    }
}

dependencies {
    implementation(libs.compose.ui)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.compose)
}