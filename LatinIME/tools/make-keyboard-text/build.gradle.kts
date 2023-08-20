plugins {
    application
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

application {
    sourceSets {
        main {
            java.srcDirs("src")
            resources.srcDirs("res")
        }
    }

    mainClass.set("com.android.inputmethod.keyboard.tools.MakeKeyboardText")
}
