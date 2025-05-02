plugins {
    id("com.android.library")
    id("maven-publish")
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "narek.dallakyan.dragging"
    compileSdk = 35
    defaultConfig {
        minSdk = 21
    }
    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
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

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
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
                            id.set("narek")
                            name.set("Narek Dallakyan")
                            email.set("it.narek.dallakyan@gmail.com")
                        }
                    }
                }
            }
        }
    }
}
