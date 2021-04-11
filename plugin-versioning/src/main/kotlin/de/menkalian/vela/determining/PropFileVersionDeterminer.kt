package de.menkalian.vela.determining

import java.io.File
import java.util.Properties

class PropFileVersionDeterminer(baseDir: File, successor: VersionDeterminer? = null) : VersionDeterminer(successor) {
    private val versionFile = File(baseDir, "versioning.properties")

    override fun canHandle(): Boolean = versionFile.exists()

    override fun determineBuildNo(): Int {
        val props = Properties()

        val inputStream = versionFile.inputStream()
        props.load(inputStream)
        inputStream.close()

        return props.getProperty("BUILD_NO", "0").toInt()
    }

    override fun incBuildNo() {
        val props = Properties()
        val version = determineBuildNo()
        props["BUILD_NO"] = "${version + 1}"

        val outputStream = versionFile.outputStream()
        props.store(outputStream, "Vela-Versioning Plugin")
        outputStream.close()
    }
}