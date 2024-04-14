buildscript {
    dependencies {
        classpath(libs.gradle)
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt.android.gradle.plugin) apply false
    alias(libs.plugins.androidTest) apply false
    alias(libs.plugins.aboutlibraries.plugin) apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
    id("com.google.firebase.firebase-perf") version "1.4.2" apply false
}


allprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
//    tasks.wrapper {
//        distributionType = Wrapper.DistributionType.ALL
//        gradleVersion = Wrapper.gradleVersion
//    }
}
