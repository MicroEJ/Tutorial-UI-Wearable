/*
 *  Kotlin
 *
 *  Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 *  Use of this source code is governed by a BSD-style license that can be found with this software.
 *
 */

plugins {
    id("com.microej.gradle.application") version "0.14.0"
}

group = "com.microej.training.ui"
version = "1.2.0"

microej {
    applicationMainClass = "com.microej.exercise.ui.Main"
}

dependencies {
    // Foundation Libraries
    implementation("ej.api:edc:1.3.5")
    implementation("ej.api:bon:1.4.0")
    implementation("ej.api:microui:3.2.0")

    // Addon Libraries
    implementation("ej.library.ui:mwt:3.5.0")
    implementation("ej.library.ui:widget:5.0.0")
    implementation("ej.library.util:observable:2.0.0")

    // VEE Port
    microejVeePort("com.microej.veeport.generic:wearable:1.5.0")
}


repositories {
    /* MicroEJ Central repository for Maven/Gradle modules */
    maven {
        name = "microEJForgeCentral"
        url = uri("https://forge.microej.com/artifactory/microej-central-repository-release")
    }
    /* MicroEJ Developer repository for Maven/Gradle modules */
    maven {
        name = "microEJForgeDeveloper"
        url = uri("https://forge.microej.com/artifactory/microej-developer-repository-release")
    }
    /* MicroEJ SDK 6 repository for Maven/Gradle modules */
    maven {
        name = "microEJForgeSDK6"
        url = uri("https://forge.microej.com/artifactory/microej-sdk6-repository-release/")
    }
    /* MicroEJ Central repository for Ivy modules */
    ivy {
        name = "microEJForgeCentralIvy"
        url = uri("https://forge.microej.com/artifactory/microej-central-repository-release")
        patternLayout {
            artifact("[organisation]/[module]/[revision]/[artifact]-[revision](-[classifier])(.[ext])")
            ivy("[organisation]/[module]/[revision]/ivy-[revision].xml")
            setM2compatible(true)
        }
    }
    /* MicroEJ Developer repository for Ivy modules */
    ivy {
        name = "microEJForgeDeveloperIvy"
        url = uri("https://forge.microej.com/artifactory/microej-developer-repository-release")
        patternLayout {
            artifact("[organisation]/[module]/[revision]/[artifact]-[revision](-[classifier])(.[ext])")
            ivy("[organisation]/[module]/[revision]/ivy-[revision].xml")
            setM2compatible(true)
        }
    }
    /* MicroEJ SDK 6 repository for Ivy modules */
    ivy {
        name = "microEJForgeSDK6Ivy"
        url = uri("https://forge.microej.com/artifactory/microej-sdk6-repository-release/")
        patternLayout {
            artifact("[organisation]/[module]/[revision]/[artifact]-[revision](-[classifier])(.[ext])")
            ivy("[organisation]/[module]/[revision]/ivy-[revision].xml")
            setM2compatible(true)
        }
    }
}
