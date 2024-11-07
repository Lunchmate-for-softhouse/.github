pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
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
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("androidx.appcompat", "androidx.appcompat:appcompat:1.5.1")
            library("androidx.constraintlayout", "androidx.constraintlayout:constraintlayout:2.1.4")
            library("androidx.navigation", "androidx.navigation:navigation-compose:2.5.3")
            // Add other dependencies here
        }
    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "LunchMate"
include(":app")
 