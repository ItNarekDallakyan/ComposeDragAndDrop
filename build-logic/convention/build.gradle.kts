plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("AndroidApplication") {
            id = "plugins.android.application"
            implementationClass = "narek.dallakyan.dragging.convention.AndroidApplicationPlugin"
        }
        register("AndroidLibrary") {
            id = "plugins.android.library"
            implementationClass = "narek.dallakyan.dragging.convention.AndroidLibraryPlugin"
        }
        register("AndroidCompose") {
            id = "plugins.android.compose"
            implementationClass = "narek.dallakyan.dragging.convention.AndroidComposePlugin"
        }
    }
}

dependencies {
    implementation(gradleApi())
    implementation(libs.plgn.android.application)
    implementation(libs.plgn.android.library)
    implementation(libs.plgn.kotlin.compose.compiler)
    implementation(libs.plgn.kotlin)
}
