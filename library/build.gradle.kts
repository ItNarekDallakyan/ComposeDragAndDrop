plugins {
    id("plugins.android.library")
    id("plugins.dokka")
    id("maven-publish")
    id("signing")
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "narek.dallakyan.dragging"


    publishing {
        singleVariant("release") {}
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
            create<MavenPublication>("maven") {
                from(components["release"])

                groupId = "com.github.narek.dallakyan"
                artifactId = "android-dragging-compose"
                version = "1.0.0"
            }
        }
    }

    signing {
        isRequired = false
    }
}


