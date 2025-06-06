import io.gitlab.arturbosch.detekt.Detekt

plugins {
  idea
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.unimined)
  alias(libs.plugins.detekt)
  alias(libs.plugins.shaddow)
}


repositories {
  unimined.neoForgedMaven()
  unimined.spongeMaven()
  unimined.modrinthMaven()
  unimined.wagYourMaven("releases")
}


val modid: String by project;
val archivesName: String by project;
val mcVersion: String by project;
val modVersion = properties["version"].toString();
val fabricVersion: String by project;
val neoForgeVersion: String by project;

version = "${modid}-1.20.5"
base.archivesName.set(archivesName)

// Source Sets
val SourceSetContainer.main: SourceSet by sourceSets.getting
val SourceSetContainer.fabric: SourceSet by sourceSets.creating
val SourceSetContainer.forge: SourceSet by sourceSets.creating


unimined.minecraft {
  version = mcVersion;
  mappings {
    intermediary()
    mojmap()
    devFallbackNamespace("official")
  }

  mods.modImplementation {
    namespace("intermediary")
  }

  // if you don't want to build/remap a "common" jar
  if (sourceSet == sourceSets.main) {
    defaultRemapJar = false
    runs.off = true
  }

  if (sourceSet == sourceSets.forge || sourceSet == sourceSets.fabric) {
    runs.config("server") {
      enabled = false
    }
  }
}

unimined.minecraft(sourceSets.fabric) {
  combineWith(sourceSets.main)

  fabric {
    loader(fabricVersion)
  }


}

unimined.minecraft(sourceSets.forge) {
  combineWith(sourceSets.main)

  neoForge {
    loader(neoForgeVersion)
  }
}


// Libraries

val minecraftLibraries by configurations.getting
val forgeMinecraftLibraries by configurations.getting
val fabricMinecraftLibraries by configurations.getting

dependencies {
  minecraftLibraries(libs.weblite)
  fabricMinecraftLibraries(libs.weblite)
  forgeMinecraftLibraries(libs.weblite)
  implementation(libs.mixins)
  shadow(libs.mixins)
  shadow(libs.kotlin.stllib)
}

tasks {
  withType<ProcessResources> {
    inputs.property("version", project.version)
    inputs.property("modId", modid)
    inputs.property("fabricVerson", fabricVersion)
    inputs.property("mcVersion", mcVersion)


    filesMatching(listOf("mixins.$modid.json")) {
      expand(inputs.properties)
    }
  }
  named<ProcessResources>("processFabricResources") {
    from(sourceSets.fabric.resources)
    inputs.property("version", project.version)
    inputs.property("modId", modid)
    inputs.property("fabricVerson", fabricVersion)
    inputs.property("mcVersion", mcVersion)


    filesMatching(listOf("fabric.mod.json", "mixins.$modid.json")) {
      expand(inputs.properties)
    }
  }


  named<ProcessResources>("processForgeResources") {
    from(sourceSets.forge.resources)
    inputs.property("version", modVersion)
    inputs.property("mod_id", modid)
    inputs.property("neoForgeVersion", neoForgeVersion)
    inputs.property("mcVersion", modid)


    filesMatching(listOf("META-INF/neoforge.mods.toml", "mixins.$modid.json")) {
      expand(inputs.properties)
    }
  }

  withType<Detekt>().configureEach {
    reports {
      xml.required.set(true)
      html.required.set(true)
      sarif.required.set(true)
      md.required.set(true)
    }
  }

  test {
    useJUnitPlatform()
  }
}

// Detekt

detekt {
  toolVersion = "1.23.8"
  config.setFrom(file("config/detekt/detekt.yml"))
  buildUponDefaultConfig = true
}


// Java versions

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(21))
  withSourcesJar()
}

kotlin { jvmToolchain(21) }

