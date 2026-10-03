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
        maven {
            name = "CodeArtifact"
            url = uri("https://yossibank-724669215656.d.codeartifact.ap-northeast-1.amazonaws.com/maven/kmp/")
            content {
                includeGroup("com.yossibank")
            }
            credentials {
                username = "aws"
                password = providers.environmentVariable("CODEARTIFACT_AUTH_TOKEN").orNull
            }
        }
        google()
        mavenCentral()
    }
}

providers
    .gradleProperty("shared.dir")
    .orElse(providers.environmentVariable("SHARED_DIR"))
    .orNull
    ?.let {
        includeBuild(it) {
            dependencySubstitution {
                substitute(module("com.yossibank:shared-android")).using(project(":shared"))
            }
        }
    }

rootProject.name = "android-app-template"
include(":app")
include(":core:screen")
include(":feature:home")
