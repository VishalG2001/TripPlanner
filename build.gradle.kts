import org.gradle.kotlin.dsl.execution.Program

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false

}
buildscript{
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath ("io.realm:realm-gradle-plugin:10.17.0")
        classpath ("com.android.tools.build:gradle:4.2.2")
    }
}
allprojects {

}