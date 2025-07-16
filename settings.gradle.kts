pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
val androidxSnapshotBuildId: String by settings // For Kotlin DSL

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Add the AndroidX Snapshot repository HERE
//        maven { url = uri("https://androidx.dev/snapshots/builds/${androidxSnapshotBuildId}/artifacts/repository/") }
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "Quran-Vector-Serach-Android"
include(":app")
 