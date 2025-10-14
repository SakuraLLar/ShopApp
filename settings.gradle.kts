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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ShopApp"
include(":app")
include(":core")
include(":feature_auth")
include(":feature_catalog")
include(":feature_cart")
include(":feature_favourites")
include(":feature_profile")
include(":data")
include(":models")
include(":common_ui")
