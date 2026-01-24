plugins {
    id("com.gtnewhorizons.gtnhconvention") apply false
}

// Only apply convention to root project
apply(plugin = "com.gtnewhorizons.gtnhconvention")

subprojects {
    // Add common repositories for all subprojects
    repositories {
        mavenCentral()
        maven {
            name = "GeckoLib Maven"
            url = uri("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
        }
        maven {
            name = "EngineHub"
            url = uri("https://maven.enginehub.org/repo/")
        }
    }
}