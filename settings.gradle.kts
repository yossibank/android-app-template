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

fun codeArtifact(name: String) = providers.gradleProperty("codeArtifact.$name").get()

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven {
            name = "CodeArtifact"
            url = uri(
                "https://${codeArtifact("domain")}-${codeArtifact("owner")}.d.codeartifact." +
                    "${codeArtifact("region")}.amazonaws.com/maven/${codeArtifact("repository")}/",
            )
            content {
                includeGroup("com.yossibank")
            }
            credentials {
                username = "aws"
                password = providers.gradleProperty("codeArtifactPassword").orNull
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
include(":feature:login")
