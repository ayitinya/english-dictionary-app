import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android.gradle.plugin)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.google.firebase.plugin)
    id("com.google.firebase.firebase-perf")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.mikepenz.aboutlibraries.plugin")
}

android {
    namespace = "com.ayitinya.englishdictionary"
    compileSdk = 34
    ndkVersion = "21.4.7075529"

    defaultConfig {
        val versionProperties = readProperties(file("../version.properties"))

        applicationId = "com.ayitinya.englishdictionary"
        minSdk = 21
        targetSdk = 34
        versionCode = (versionProperties?.getProperty("VERSION_CODE")
            ?.toInt() ?: 0) + 39 // 39 is the last version code before migrating to GHA for builds
        versionName = "2.1.1"


        testInstrumentationRunner = "com.ayitinya.englishdictionary.TestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures {
        buildConfig = true
    }

    signingConfigs {
        create("release") {
            val secretProperties = readProperties(file("../secret.properties"))
            storeFile = file("../keystore/keys.jks")
            storePassword = secretProperties?.getProperty("SIGNING_STORE_PASSWORD")
            keyAlias = secretProperties?.getProperty("SIGNING_KEY_ALIAS")
            keyPassword = secretProperties?.getProperty("SIGNING_KEY_PASSWORD")
        }
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
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")

            ndk {
                debugSymbolLevel = "FULL"
            }

            configure<CrashlyticsExtension> {
                nativeSymbolUploadEnabled = true
            }

            packaging {
                resources {
                    excludes += "/META-INF/{AL2.0,LGPL2.1}"
                }
            }
        }

        create("beta") {
            initWith(getByName("release"))
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
        jvmTarget = "19"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }


    packaging {
        resources.excludes.add("META-INF/*")
    }

    sourceSets {
        getByName("androidTest").assets.srcDir("$projectDir/schemas")
    }
}


dependencies {

    testImplementation(libs.androidx.work.testing)
    implementation(libs.material)
    testImplementation(libs.junit.jupiter)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.perf)
    implementation(libs.firebase.crashlytics.ndk)
    implementation(libs.profileinstaller)
    implementation(libs.firebase.messaging)
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
    ksp(libs.androidx.lifecycle.compiler)

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

    implementation(libs.accompanist.permissions)

    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    implementation(libs.androidx.glance)
    implementation(libs.androidx.glance.appwidget)

    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.dagger.hilt.android.compiler)

    implementation(libs.appcompat)

    implementation(libs.androidx.window)
    implementation(libs.review)
    implementation(libs.review.ktx)
    debugImplementation(libs.appwidget.viewer)

    implementation(libs.napier)

    implementation(libs.aboutlibraries.compose)
    implementation(libs.aboutlibraries.core)

    implementation(libs.sqlite.android)

    implementation(libs.billing.ktx)
}


ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

hilt {
    enableAggregatingTask = true
}

fun readProperties(propertiesFile: File): Properties? {
    return try {
        Properties().apply {
            propertiesFile.inputStream().use { load(it) }
        }
    } catch (e: Exception) {
        null
    }
}