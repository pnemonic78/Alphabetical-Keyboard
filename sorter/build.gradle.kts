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
            java.srcDirs("src/main/java")
        }
    }

    mainClass.set("com.github.inputmethod.alphabetical.sorter.SortKeys")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}
