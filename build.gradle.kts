import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("jvm") version "1.3.41"

    id("org.springframework.boot") version "2.1.6.RELEASE" apply false
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    kotlin("plugin.jpa") version "1.3.41" apply false
    kotlin("plugin.spring") version "1.3.41" apply false

    idea
    eclipse
}

allprojects {
    group = "io.brainage.flask"
    version = "0.0.1-SNAPSHOT"

    repositories {
        jcenter()
        mavenLocal()
        mavenCentral()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
        }
    }
}

java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
        excludeDirs = files(
                "src/main",
                "src/test",
                "adapters/src/main",
                "adapters/src/test"
        ).toSet()
    }
}

eclipse.classpath.isDownloadJavadoc = true
eclipse.classpath.isDownloadSources = true

extra["springCloudVersion"] = "Greenwich.SR1"
extra["junit-jupiter.version"] = "5.4.2"
extra["mockito.version"] = "2.28.2"
extra["archunit.version"] = "0.10.2"
extra["junit-insights.version"] = "1.1.0"
extra["junit-platform.version"] = "1.4.2"

subprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")

    dependencyManagement {
        imports {
            mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
        }
    }

    val developmentOnly by configurations.creating
    configurations {
        runtimeClasspath {
            extendsFrom(developmentOnly)
        }
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    dependencies {
        // --------------------------------------------------
        // for kotlin
        // --------------------------------------------------
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

        // --------------------------------------------------
        // for spring tools
        // --------------------------------------------------
        annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")
        developmentOnly("org.springframework.boot:spring-boot-devtools")

        // --------------------------------------------------
        // for test
        // --------------------------------------------------
        testImplementation("org.springframework.boot:spring-boot-starter-test") {
            exclude(group = "junit")
        }
        testImplementation("org.junit.jupiter:junit-jupiter")
        testImplementation("org.mockito:mockito-junit-jupiter")
        testImplementation("com.tngtech.archunit:archunit:${property("archunit.version")}")
        testImplementation("de.adesso:junit-insights:${property("junit-insights.version")}")
        testImplementation("org.junit.platform:junit-platform-launcher:${property("junit-platform.version")}")

    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
        systemProperty("de.adesso.junitinsights.enabled", "true")
    }

}

project(":common") {
    dependencies {
        implementation("org.springframework:spring-context-support")
        implementation("javax.validation:validation-api")
    }
}

project(":application") {
    dependencies {
        implementation(project(":common"))

        implementation("org.springframework.data:spring-data-commons")
        implementation("org.springframework.boot:spring-boot-starter-validation")

    }
}

project(":adapters:persistence") {
    dependencies {
        implementation(project(":application"))
        implementation("org.springframework.boot:spring-boot-starter-data-jdbc")

        // implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        // implementation("org.springframework.boot:spring-boot-starter-jooq")
        // implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:2.0.1")
        // implementation("org.flywaydb:flyway-core")
        runtimeOnly("com.h2database:h2")
        runtimeOnly("mysql:mysql-connector-java")
        runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

    }
}

project(":adapters:api") {
    dependencies {
        implementation(project(":application"))
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    }
}

project(":configuration") {
    apply(plugin = "org.springframework.boot")

    tasks.getByName<BootJar>("bootJar") {
        // classifier = "boot"
        archiveClassifier.set("boot")
    }

    dependencies {
        implementation(project(":common"))
        implementation(project(":application"))
        implementation(project(":adapters:persistence"))
        implementation(project(":adapters:api"))

        implementation("org.springframework.boot:spring-boot-starter")
    }
}

// implementation("org.springframework.boot:spring-boot-starter-actuator")
// implementation("org.springframework.boot:spring-boot-starter-data-redis")
// implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
// implementation("org.springframework.boot:spring-boot-starter-mail")
// implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
// implementation("org.springframework.boot:spring-boot-starter-security")
// implementation("org.springframework.boot:spring-boot-starter-web")
// implementation("org.springframework.boot:spring-boot-starter-webflux")
// implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
//
// implementation("org.springframework.cloud:spring-cloud-config-server")
// implementation("org.springframework.cloud:spring-cloud-starter-config")
// implementation("org.springframework.cloud:spring-cloud-starter-gateway")
// implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
// implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server")
// implementation("org.springframework.cloud:spring-cloud-starter-netflix-hystrix")
// implementation("org.springframework.cloud:spring-cloud-starter-netflix-ribbon")
// implementation("org.springframework.cloud:spring-cloud-starter-netflix-turbine")
// implementation("org.springframework.cloud:spring-cloud-starter-netflix-zuul")
// implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
// implementation("org.springframework.cloud:spring-cloud-starter-sleuth")
// implementation("org.springframework.cloud:spring-cloud-starter-vault-config")
// implementation("org.springframework.cloud:spring-cloud-starter-zipkin")
//
// implementation("org.springframework.boot:spring-boot-starter-data-jpa")
// implementation("org.springframework.boot:spring-boot-starter-jdbc")
// implementation("org.springframework.boot:spring-boot-starter-jooq")
// implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:2.0.1")
// implementation("org.flywaydb:flyway-core")
// runtimeOnly("com.h2database:h2")
// runtimeOnly("org.postgresql:postgresql")
//
// testImplementation("io.projectreactor:reactor-test")
// testImplementation("org.springframework.security:spring-security-test")
// testRuntimeOnly("com.h2database:h2")
//