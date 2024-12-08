plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false

}
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath (libs.google.services)
        classpath (libs.androidx.navigation.safe.args.gradle.plugin.v270)
    }

}