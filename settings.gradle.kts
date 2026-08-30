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
        // TODO: 共通コアを社内 Maven に置いたら mavenLocal は外す（ローカル結線の暫定措置）
        mavenLocal()
        google()
        mavenCentral()
    }
}

rootProject.name = "android-app-template"
include(":app")
