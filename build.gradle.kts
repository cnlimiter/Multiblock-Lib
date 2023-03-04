import java.util.*

plugins {
    id("maven-publish")
    id("fabric-loom") version "1.0-SNAPSHOT"

    id("org.cadixdev.licenser") version "0.6.1"
}

val modVersion: String by project
val minecraftVersion: String by project
val parchementVersion: String by project

group = "cn.evolvefield.mods"
version = modVersion

repositories {
    val mavenUrls = listOf(
        "https://ladysnake.jfrog.io/artifactory/mods",
        "https://maven.parchmentmc.org"
    )

    for (url in mavenUrls) {
        maven(url = url)
    }
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${minecraftVersion}:${parchementVersion}@zip")
    })

    modImplementation(libs.loader)
    modImplementation(libs.fabric.api)
    modImplementation(libs.cca.base)
    modImplementation(libs.cca.world)
}

sourceSets {
    val main = this.getByName("main")
    this.create("testmod") {
        this.compileClasspath += main.compileClasspath
        this.compileClasspath += main.output
        this.runtimeClasspath += main.runtimeClasspath
        this.runtimeClasspath += main.output
    }
}

loom {
    runs {
        this.create("testmodClient") {
            client()
            name("Testmod Client")
            source(sourceSets.getByName("testmod"))
        }

        this.create("testmodServer") {
            client()
            name("Testmod Server")
            source(sourceSets.getByName("testmod"))
        }
    }
}

java {
    withSourcesJar()
    //withJavadocJar()
}

tasks {
    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") {
            expand(
                mutableMapOf(
                    "version" to project.version
                )
            )
        }
    }

    build {
        dependsOn("updateLicenses")
    }

    jar {
        archiveBaseName.set("Multiblock-Lib")
    }

    remapJar {
        archiveBaseName.set("Multiblock-Lib")
    }

    withType<JavaCompile>().configureEach  {
        options.release.set(17)
        options.encoding = "UTF-8"
    }

    withType<Javadoc>().configureEach  {
        with(options as StandardJavadocDocletOptions) {
            source = "17"
            encoding = "UTF-8"
            docEncoding = "UTF-8"
            charSet = "UTF-8"

            addStringOption("Xdoclint:none", "-quiet")
            addStringOption("Xwerror", "-quiet")
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "cn.evolvefield.mods"
            artifactId = "multiblocklib"
            version = modVersion
            from(components["java"])
        }
    }

    repositories {
        if (project.rootProject.file("local.properties").exists()
            || (System.getenv()["MAVEN_USERNAME"] != null
                    && System.getenv()["MAVEN_PASSWORD"] != null)
        ) {
            maven {
                name = "Release"
                url = uri("https://maven.nova-committee.cn/releases")
                credentials {
                    if (project.rootProject.file("local.properties").exists()) {
                        val localProperties = Properties()
                        localProperties.load(project.rootProject.file("local.properties").inputStream())
                        username = localProperties["MAVEN_USERNAME"] as String
                        password = localProperties["MAVEN_PASSWORD"] as String
                    } else {
                        username = System.getenv()["MAVEN_USERNAME"]!!
                        password = System.getenv()["MAVEN_PASSWORD"]!!
                    }
                }
            }
        }
    }
}
