pluginManagement {
    repositories {
        maven {
            url = uri("https://mirrors.cloud.tencent.com/gradle-plugin/")
        }
        maven {
            url = uri("https://mirrors.cloud.tencent.com/maven/")
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
            url = uri("https://mirrors.cloud.tencent.com/maven/")
        }
        mavenCentral()
    }
}

rootProject.name = "My Application"
include(":app")
 