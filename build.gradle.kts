import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.net.URI
import java.net.URLConnection

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
val simple_magnets_version = prop("simple_magnets_version")
val supermartijn_core_version = prop("supermartijn_core_version")
val supermartijn_config_version = prop("supermartijn_config_version")
val glow_paste_version = prop("glow_paste_version")
val storage_drawers_version = prop("storage_drawers_version")
val toms_storage_version = prop("toms_storage_version")
val essentialis_version = prop("essentialis_version")
val trinkets_version = prop("trinkets_version")
val mod_id = prop("mod_id")
val mod_name = prop("mod_name")
val mod_description = prop("mod_description")
val mod_version = prop("mod_version")
val version = prop("version")
val group = prop("group")

// Essentialis is required at runtime but is only distributed as GitHub releases (it has no Maven
// artifact). Dev runs fetch the pinned release into a local cache; end users get it from the modpack.
val essentialisAsset = "essentialis-$essentialis_version.jar"
val essentialisJar = file("${rootDir}/maven-local/amatsagu/essentialis/$essentialis_version/$essentialisAsset")
val essentialisDownloadUrls = listOf(
    "https://github.com/amatsagu/essentialis/releases/download/$essentialis_version/$essentialisAsset",
    "https://github.com/amatsagu/essentialis/releases/latest/download/$essentialisAsset"
)

fun downloadTo(url: String, target: File): Boolean {
    target.parentFile?.mkdirs()
    return try {
        val connection: URLConnection = URI(url).toURL().openConnection()
        connection.connectTimeout = 15_000
        connection.readTimeout = 60_000
        connection.setRequestProperty("User-Agent", "advancement-recipes-build")
        val input: InputStream = connection.getInputStream()
        input.use { source ->
            val output: OutputStream = target.outputStream()
            output.use { sink -> source.copyTo(sink) }
        }
        true
    } catch (e: Exception) {
        target.delete()
        false
    }
}

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
    maven("https://maven.nucleoid.xyz/releases") {
        name = "Nucleoid"
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
    compileOnly("maven.modrinth:simple-magnets:$simple_magnets_version")
    compileOnly("maven.modrinth:supermartijn642s-core-lib:$supermartijn_core_version")
    compileOnly("maven.modrinth:supermartijn642s-config-lib:$supermartijn_config_version")
    compileOnly("maven.modrinth:glow-paste:$glow_paste_version")
    compileOnly("maven.modrinth:storagedrawers:$storage_drawers_version")
    compileOnly("maven.modrinth:toms-storage:$toms_storage_version")

    runtimeOnly("mezz.jei:jei-26.3-fabric:$jei_version")
    runtimeOnly("maven.modrinth:modern-advancement-screen:$modern_advancements_version")
    runtimeOnly("maven.modrinth:nemos-backpacks:$nemos_backpacks_version")
    runtimeOnly("maven.modrinth:nemos-upgrade-templates:$nemos_upgrade_templates_version")
    runtimeOnly("maven.modrinth:farmers-delight-refabricated:$farmers_delight_version")
    runtimeOnly("maven.modrinth:simple-magnets:$simple_magnets_version")
    runtimeOnly("maven.modrinth:supermartijn642s-core-lib:$supermartijn_core_version")
    runtimeOnly("maven.modrinth:supermartijn642s-config-lib:$supermartijn_config_version")
    runtimeOnly("maven.modrinth:glow-paste:$glow_paste_version")
    runtimeOnly("maven.modrinth:storagedrawers:$storage_drawers_version")
    runtimeOnly("maven.modrinth:toms-storage:$toms_storage_version")
    // Essentialis is a hard dependency but has no Maven artifact, so dev runs download its pinned
    // GitHub release into a local cache. Trinkets (required by Essentialis, bundles Yumi via JiJ)
    // comes from the Nucleoid Maven for dev runs; end users get both from the modpack.
    localRuntime(files(essentialisJar))
    runtimeOnly("eu.pb4:trinkets:$trinkets_version")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    systemProperty("joml.nounsafe", "true")
}

// Dev runs need the pinned Essentialis jar. Fetch it from its GitHub release (cached after the first
// download) so the build never depends on a sibling project's local path.
val downloadEssentialis = tasks.register("downloadEssentialis") {
    outputs.file(essentialisJar)
    doLast {
        if (essentialisJar.isFile && essentialisJar.length() > 0L) {
            return@doLast
        }
        for (url in essentialisDownloadUrls) {
            if (downloadTo(url, essentialisJar)) {
                logger.lifecycle("Downloaded Essentialis $essentialis_version from $url")
                return@doLast
            }
        }
        throw GradleException(
            "Could not download Essentialis $essentialis_version ($essentialisAsset).\n" +
                "Make sure the GitHub release exists, or place the jar at ${essentialisJar.absolutePath}.\n" +
                "Tried:\n" + essentialisDownloadUrls.joinToString("\n") { "  $it" }
        )
    }
}

tasks.matching { it.name.startsWith("run") }.configureEach {
    dependsOn(downloadEssentialis)
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
