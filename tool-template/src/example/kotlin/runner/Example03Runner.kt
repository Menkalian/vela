package runner

import ResourceHelper
import printExampleResult
import java.io.File
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.createTempDirectory

@OptIn(ExperimentalPathApi::class)
fun main() {
    val tmpDir = createTempDirectory().toFile()
    loadResourcePath(tmpDir, "03/hint.vtp")
    loadResourcePath(tmpDir, "03/SetVariable.vtp")
    System.setProperty("user.dir", tmpDir.absolutePath)

    printExampleResult("03_include_files.vtp")

    tmpDir.deleteRecursively()
}

private fun loadResourcePath(targetDir: File, path: String) {
    val inStream = ResourceHelper::class.java
        .classLoader.getResourceAsStream(path)!!
    val target = File(targetDir, path)
    target.parentFile.mkdirs()
    target.writeBytes(inStream.readAllBytes())
    inStream.close()
}