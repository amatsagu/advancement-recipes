plugins {
    id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT"
}

fun prop(name: String): String = project.property(name) as String

val minecraft_version = prop("minecraft_version")
val loader_version = prop("loader_version")
val fabric_api_version = prop("fabric_api_version")
val jei_version = prop("jei_version")
val modern_advancements_version = prop("modern_advancements_version")
val nemos_backpacks_version = prop("nemos_backpacks_version")
val nemos_upgrade_templates_version = prop("nemos_upgrade_templates_version")
val farmers_delight_version = prop("farmers_delight_version")
val mod_id = prop("mod_id")
val mod_name = prop("mod_name")
val mod_description = prop("mod_description")
val mod_version = prop("mod_version")
val version = prop("version")
val group = prop("group")

base {
    archivesName.set(mod_id)
}

repositories {
    maven("https://maven.fabricmc.net") {
        name = "FabricMC"
    }
    maven("https://api.modrinth.com/maven") {
        name = "Modrinth"
    }
    maven("https://maven.blamejared.com") {
        name = "Mezz Maven"
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft_version")
    implementation("net.fabricmc:fabric-loader:$loader_version")
    implementation("net.fabricmc.fabric-api:fabric-api:$fabric_api_version")

    compileOnly("mezz.jei:jei-26.3-fabric-api:$jei_version")
    compileOnly("mezz.jei:jei-26.3-fabric:$jei_version")
    compileOnly("maven.modrinth:modern-advancement-screen:$modern_advancements_version")
    compileOnly("maven.modrinth:nemos-backpacks:$nemos_backpacks_version")
    compileOnly("maven.modrinth:nemos-upgrade-templates:$nemos_upgrade_templates_version")
    compileOnly("maven.modrinth:farmers-delight-refabricated:$farmers_delight_version")

    runtimeOnly("mezz.jei:jei-26.3-fabric:$jei_version")
    runtimeOnly("maven.modrinth:modern-advancement-screen:$modern_advancements_version")
    runtimeOnly("maven.modrinth:nemos-backpacks:$nemos_backpacks_version")
    runtimeOnly("maven.modrinth:nemos-upgrade-templates:$nemos_upgrade_templates_version")
    runtimeOnly("maven.modrinth:farmers-delight-refabricated:$farmers_delight_version")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.processResources {
    filteringCharset = "UTF-8"
    val props = mapOf(
        "project_name" to rootProject.name,
        "mod_id" to mod_id,
        "mod_name" to mod_name,
        "mod_description" to mod_description,
        "version" to version,
        "jei_version" to jei_version,
    )
    inputs.properties(props)
    filesMatching("fabric.mod.json") {
        expand(props)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(25)
    options.compilerArgs.add("-Xlint:deprecation")
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks.withType<AbstractArchiveTask>().configureEach {
    isReproducibleFileOrder = true
    isPreserveFileTimestamps = false
}

tasks.jar {
    exclude(".cache/")
}
