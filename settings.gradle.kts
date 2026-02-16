pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}
rootProject.name = "distributed-tracing"

include("service-a")
include("config-server")
