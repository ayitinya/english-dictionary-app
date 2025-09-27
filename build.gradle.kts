buildscript {
    dependencies {
        classpath(libs.gradle)
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt.android.gradle.plugin) apply false
    alias(libs.plugins.androidTest) apply false
    alias(libs.plugins.aboutlibraries.plugin) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.google.firebase.plugin) apply false
    alias(libs.plugins.perf.plugin) apply false
    id("org.mozilla.rust-android-gradle.rust-android") version "0.9.6" apply false
    id("io.github.MatrixDev.android-rust") version "0.4.0" apply false
}

//cargo {
//    module = "../sqlite-zstd"       // Or whatever directory contains your Cargo.toml
//    libname = "sqlite-zstd"          // Or whatever matches Cargo.toml's [package] name.
//    targets = listOf("arm", "x86", "arm64", "x86_64")  // See bellow for a longer list of options
//    verbose = true
//    prebuiltToolchains = true
//    profile = "release"
//}
//
//tasks.whenTaskAdded {
//    if (this.name.contains("javaPreCompile")) {
//        this.dependsOn("cargoBuild")
//    }
//}



//allprojects {
//    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
//        kotlinOptions.jvmTarget = "1.8"
//    }
//}
