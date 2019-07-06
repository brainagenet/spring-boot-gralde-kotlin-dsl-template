pluginManagement {
	repositories {
		gradlePluginPortal()
	}
}
rootProject.name = "demo"

include("common", "application", "adapters:persistence", "adapters:api", "configuration")