plugins {
    alias(libs.plugins.android.library)
    id("maven-publish")
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "narek.dallakyan.dragging"

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.ItNarekDallakyan"
            artifactId = "ComposeDragAndDrop"
            version = "1.0.0"

            // Optionally, configure POM metadata if needed
            pom {
                name.set("ComposeDragAndDrop")
                description.set("A library for drag and drop in Compose.")
                url.set("https://github.com/ItNarekDallakyan/ComposeDragAndDrop")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("Narek")
                        name.set("Narek Dallakyan")
                        email.set("it.narek.dallakyan@gmail.com")
                    }
                }
            }
        }
    }
}