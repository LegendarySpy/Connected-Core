plugins {
    id("dev.isxander.modstitch.base") version "0.5.12"
}

fun prop(name: String, consumer: (prop: String) -> Unit) {
    (findProperty(name) as? String?)
        ?.let(consumer)
}

val minecraft = property("deps.minecraft") as String

modstitch {
    minecraftVersion = minecraft

    javaTarget = when (minecraft) {
        "1.21.1" -> 21
        else -> throw IllegalArgumentException("Please store the java version for ${property("deps.minecraft")} in build.gradle.kts!")
    }

    // If parchment doesnt exist for a version yet you can safely
    // omit the "deps.parchment" property from your versioned gradle.properties
    parchment {
        prop("deps.parchment") { mappingsVersion = it }
    }

    metadata {
        modId = "connectedcore"
        modName = "Connected Core"
        modVersion = "1.0.0"
        modGroup = "com.legendaryspy"
        modAuthor = "LegendarySpy"

        fun <K, V> MapProperty<K, V>.populate(block: MapProperty<K, V>.() -> Unit) {
            block()
        }

        replacementProperties.populate {
            put("mod_issue_tracker", "https://github.com/LegendarySpy/Connected-Core/issues")
            put("pack_format", when (property("deps.minecraft")) {
                "1.21.1" -> 48
                else -> throw IllegalArgumentException("Please store the resource pack version for ${property("deps.minecraft")} in build.gradle.kts! https://minecraft.wiki/w/Pack_format")
            }.toString())
        }
    }

    loom {
        fabricLoaderVersion = "0.17.3"

        configureLoom {

        }
    }

    moddevgradle {
        enable {
            prop("deps.neoform") { neoFormVersion = it }
            prop("deps.neoforge") { neoForgeVersion = it }
        }

        defaultRuns()

        configureNeoforge {
            runs.all {
                disableIdeRun()
            }
        }
    }

    mixin {
        addMixinsToModManifest = true

        configs.register("connectedcore")
    }
}

var constraint: String = name.split("-")[1]
stonecutter {
    consts(
        "fabric" to constraint.equals("fabric"),
        "neoforge" to constraint.equals("neoforge"),
        "vanilla" to constraint.equals("vanilla")
    )
}

dependencies {
    modstitch.loom {
        modstitchModImplementation("net.fabricmc.fabric-api:fabric-api:0.116.7+1.21.1")
    }

}

subprojects {
    dependencies {
        add("annotationProcessor", "org.spongepowered:mixin:0.8.5:processor")
    }
}