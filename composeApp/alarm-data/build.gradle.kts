plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            isStatic = true
            baseName = "composeApp"
            linkerOpts.add("-lsqlite3")
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":composeApp:alarm-domain"))
                implementation(libs.multiplatformSettings)
                implementation(libs.koin.core)
                implementation(libs.koin.annotations)
                implementation(libs.androidx.room.paging)
                implementation(libs.androidx.room.runtime)
                implementation(libs.androidx.sqlite.bundled)
                implementation(libs.kotlinx.datetime)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.appcompat)
                implementation(libs.material)
                implementation(libs.androidx.paging3.common)
                implementation(libs.androidx.paging3.runtime)
                implementation(libs.androidx.paging3.compose)
                implementation(project.dependencies.platform(libs.firebase.bom))
                implementation(libs.firebase.ai)
                implementation(libs.kotlinx.serialization.json.v163)
            }
        }
//        val iosMain by creating { dependsOn(commonMain) }
//        val iosTest by creating { dependsOn(commonTest) }

//        val iosX64Main by getting { dependsOn(iosMain) }
//        val iosArm64Main by getting { dependsOn(iosMain) }
//        val iosSimulatorArm64Main by getting { dependsOn(iosMain) }
//
//        val iosX64Test by getting { dependsOn(iosTest) }
//        val iosArm64Test by getting { dependsOn(iosTest) }
//        val iosSimulatorArm64Test by getting { dependsOn(iosTest) }
    }
}

android {
    namespace = "dev.loki.alarm_data"
    compileSdk = 36

    defaultConfig {
        minSdk = 29

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosX64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}