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
}


//allprojects {
//    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
//        kotlinOptions.jvmTarget = "1.8"
//    }
//}
