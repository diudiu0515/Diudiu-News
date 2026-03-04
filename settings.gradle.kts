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
    repositories {
        maven { url=uri("https://repo.huaweicloud.com/repository/maven") }// 阿里云主仓库
        maven { url=uri("https://maven.aliyun.com/repository/google") }// Google 专用仓库
        maven { url=uri("https://repo.huaweicloud.com/repository/maven")}
        mavenCentral()
    }
}


rootProject.name = "diudiunews"
include(":app")
 