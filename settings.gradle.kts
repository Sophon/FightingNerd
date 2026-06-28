rootProject.name = "FightingNerd"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":core")
include(":composeApp")
include(":bot:discord")
include(":feat:glossaryInfil")
include(":feat:wikiWavu")
include(":feat:wikiSupercombo")
include(":feat:xko")
include(":feat:wikiDreamCancel")
include(":feat:wikiDustLoop")
include(":feat:admin")
include(":feat:wikiMizuumi")
include(":feat:ewgf")
include(":feat:stats")
include(":feat:wikiDragDown")
