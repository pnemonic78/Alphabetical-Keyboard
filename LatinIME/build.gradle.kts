plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    compileSdk = 33

    // Required if using classes in android.test.runner
    useLibrary("android.test.runner")

    // Required if using classes in android.test.base
    useLibrary("android.test.base")

    // Required if using classes in android.test.mock
    useLibrary("android.test.mock")

    defaultConfig {
        minSdk = 21
        targetSdk = 33
        versionCode = 210
        versionName = "2.10"

        applicationId = "com.github.inputmethod.alphabetical"
        testApplicationId = "com.github.inputmethod.alphabetical.tests"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = false

        signingConfig = signingConfigs.getByName("debug")
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("java/shared.keystore")

//            firebaseCrashlytics {
//                mappingFileUploadEnabled = false
//            }
        }
        create("release") {
            storeFile = file("../release.keystore")
            storePassword = project.property("STORE_PASSWORD_RELEASE") as String
            keyAlias = "release"
            keyPassword = project.property("KEY_PASSWORD_RELEASE") as String
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
        getByName("release") {
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard.flags")
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    flavorDimensions += "default"

    sourceSets {
        getByName("main") {
            manifest.srcFile("java/AndroidManifest.xml")
            java.srcDirs("common/src", "java/src")
            res.srcDir("java/res")
        }

        getByName("androidTest") {
            manifest.srcFile("tests/AndroidManifest.xml")
            java.srcDirs("tests/src")
            res.srcDirs("tests/res")
        }
    }

    externalNativeBuild {
        cmake {
            path("native/jni/CMakeLists.txt")
        }
    }

    androidResources {
        noCompress += "dict"
    }

    lint {
        checkReleaseBuilds = false
    }
    ndkVersion = "25.2.9519653"
}

dependencies {
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("com.google.code.findbugs:jsr305:3.0.2")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("org.mockito:mockito-core:3.10.0")
    androidTestImplementation("com.google.dexmaker:dexmaker:1.2")
    androidTestImplementation("com.google.dexmaker:dexmaker-mockito:1.2")
    androidTestImplementation("androidx.test:runner:1.5.1")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
    androidTestImplementation("androidx.annotation:annotation:1.5.0")

    // Logging
    implementation("com.google.firebase:firebase-crashlytics:18.4.0")
}
