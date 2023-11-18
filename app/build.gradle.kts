@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android.gradle.plugin)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sentry.android.gradle.plugin)
    id("io.sentry.kotlin.compiler.gradle") version "3.14.0"
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
    id("com.mikepenz.aboutlibraries.plugin")
    id("app.cash.sqldelight")
    alias(libs.plugins.androidx.baselineprofile)
    kotlin("kapt")
}

android {
    namespace = "com.ayitinya.englishdictionary"
    compileSdk = 34
    ndkVersion = "21.4.7075529"

    defaultConfig {
        applicationId = "com.ayitinya.englishdictionary"
        minSdk = 21
        targetSdk = 34
        versionCode = 24
        versionName = "1.2.3"


        testInstrumentationRunner = "com.ayitinya.englishdictionary.TestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            manifestPlaceholders["sentryEnvironment"] = "debug"
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }

        release {
            manifestPlaceholders += mapOf("sentryEnvironment" to "release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
            ndk {
                debugSymbolLevel = "FULL"
            }
        }

        create("beta") {
            initWith(getByName("release"))
            manifestPlaceholders += mapOf("sentryEnvironment" to "beta")
            versionNameSuffix = "-beta"
        }
    }

    androidResources {
        generateLocaleConfig = true
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    packaging {
        resources.excludes.add("META-INF/*")
    }
}



sentry {
    ignoredBuildTypes.set(setOf("debug", "nonMinifiedRelease"))
    includeNativeSources.set(true)
    includeSourceContext.set(true)
}


dependencies {

    testImplementation(libs.androidx.work.testing)
    implementation(libs.material)
    testImplementation(libs.junit.jupiter)
    "baselineProfile"(project(":baselineprofile"))
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    implementation(libs.asset.delivery.ktx)

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.perf.ktx)
    implementation(libs.profileinstaller)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.work.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    annotationProcessor(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)
    testImplementation(libs.androidx.room.testing)

    implementation(libs.androidx.datastore.preferences)

    implementation(libs.androidx.core.splashscreen)

    implementation(libs.dagger.hilt.android)
    ksp(libs.dagger.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    kapt(libs.androidx.lifecycle.compiler)

    implementation(libs.accompanist.systemuicontroller)

    implementation(libs.androidx.constraintlayout.compose)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.io.ktor.client.core)
    implementation(libs.io.ktor.client.android)
    implementation(libs.io.ktor.serialization.kotlinx.json)
    implementation(libs.io.ktor.client.logging)
    implementation(libs.io.ktor.client.content.negotiation)
    testImplementation(libs.io.ktor.client.mock)
    implementation(libs.org.jetbrains.kotlinx.serialization.json)

    implementation(libs.io.github.raamcosta.compose.destinations.animations.core)
    ksp(libs.io.github.raamcosta.compose.destinations.ksp)

    implementation(libs.accompanist.permissions)

    implementation(libs.androidx.hilt.work)
    kapt(libs.androidx.hilt.compiler)

    implementation(libs.androidx.glance)
    implementation(libs.androidx.glance.appwidget)

    androidTestImplementation(libs.hilt.android.testing)
    kaptAndroidTest(libs.dagger.hilt.android.compiler)

    implementation(libs.appcompat)

    implementation(libs.androidx.window)
    implementation(libs.review)
    implementation(libs.review.ktx)
    debugImplementation(libs.appwidget.viewer)

    implementation(libs.napier)

    implementation(libs.aboutlibraries.compose)
    implementation(libs.aboutlibraries.core)

    implementation(libs.anrwatchdog)
    implementation(libs.clarity)

    implementation(libs.sqlite.android)

    implementation(libs.sentry.android)
    implementation(libs.sentry.compose.android)
}



ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

kapt {
    correctErrorTypes = true
    useBuildCache = true
}

hilt {
    enableAggregatingTask = true
}