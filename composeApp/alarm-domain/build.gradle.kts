plugins {
    alias(libs.plugins.multiplatform)
}

kotlin {
    sourceSets {
        jvm()
        iosX64()
        iosArm64()
        iosSimulatorArm64()

        val commonMain by getting {
            dependencies {

                implementation(libs.kermit)
                implementation(libs.koin.core)
                implementation(libs.koin.annotations)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.androidx.paging3.common)
                implementation(libs.kotlinx.datetime)
            }
        }
    }
}