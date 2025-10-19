import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

plugins {
    id("dev.kikugie.stonecutter")
}
stonecutter active "1.21.1-neoforge"

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) { 
    group = "project"
    ofTask("build")

    doLast {
        fun renameArtifact(dir: String, label: String) {
            val libsDir = file(dir)
            if (!libsDir.exists()) return

            val candidate = libsDir.listFiles { child: File ->
                child.isFile && child.extension == "jar" && !child.name.contains("-sources", ignoreCase = true)
            }?.maxByOrNull { it.lastModified() } ?: return

            val versionToken = candidate.nameWithoutExtension.substringAfterLast('-', "")
            if (versionToken.isEmpty()) return

            val desiredName = "connected-core-$label-$versionToken.jar"
            if (candidate.name == desiredName) return

            val target = File(libsDir, desiredName)
            Files.move(candidate.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)

            libsDir.listFiles { sibling: File ->
                sibling.isFile && sibling.extension == "jar" && !sibling.name.startsWith("connected-core-")
            }?.forEach { leftover -> leftover.delete() }
        }

        renameArtifact("versions/1.21.1-fabric/build/libs", "fabric")
        renameArtifact("versions/1.21.1-neoforge/build/libs", "NeoForge")
    }
}

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.fabricmc.net/")
    }
}