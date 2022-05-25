buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.2.0")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.4.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.0")
        classpath("com.amazonaws:aws-android-sdk-appsync-gradle-plugin:3.3.1")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

