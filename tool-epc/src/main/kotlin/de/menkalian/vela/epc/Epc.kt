package de.menkalian.vela.epc

import java.io.File

@Suppress("unused")
class Epc(private val defaultStoreType: StoreType = StoreType.TEXT,
          private val binFileExtensions: List<String> = DEFAULT_BIN_FILES,
          private val textFileExtensions: List<String> = DEFAULT_TXT_FILES
) {
    companion object {
        val DEFAULT_BIN_FILES = listOf("exe", "jar", "zip", "class", "bin")
        val DEFAULT_TXT_FILES = listOf("txt", "java", "kt", "kts", "gradle", "md", "yml")
    }

    fun compress(toCompress: File, target: File = File.createTempFile("EPC", "epc")): File {
        val files = collectFiles(toCompress, toCompress)

        files.forEachIndexed { index, epcFile ->
            if (index == 0) {
                target.writeText(epcFile.toString(), Charsets.UTF_8)
            } else {
                target.appendText("---\n", Charsets.UTF_8)
                target.appendText(epcFile.toString(), Charsets.UTF_8)
            }
        }

        return target
    }

    fun decompress(source: File, target: File) {
        val text = source.readText(Charsets.UTF_8)
        text.split("---").forEach {
            decompress(target, it.trim().parseEpcFile())
        }
    }

    private fun collectFiles(base: File, current: File): List<EpcFile> {
        val toReturn: MutableList<EpcFile>

        if (current.isDirectory) {
            toReturn = mutableListOf()
            current.listFiles()!!.forEach {
                toReturn.addAll(collectFiles(base, it))
            }
        } else {
            val relativePath = current.relativeTo(base).toString().replace(File.separatorChar, '/')

            val epcFile = when {
                binFileExtensions.contains(current.extension.toLowerCase())  -> EpcFile(relativePath, StoreType.BINARY, current.readBytes().toHexString())
                textFileExtensions.contains(current.extension.toLowerCase()) -> EpcFile(
                    relativePath,
                    StoreType.TEXT,
                    current.readText(Charsets.UTF_8).replace("-", "\\-")
                )
                else                                                         -> {
                    when (defaultStoreType) {
                        StoreType.TEXT   -> EpcFile(relativePath, StoreType.TEXT, current.readText(Charsets.UTF_8).replace("-", "\\-"))
                        StoreType.BINARY -> EpcFile(relativePath, StoreType.BINARY, current.readBytes().toHexString())
                    }
                }
            }

            toReturn = mutableListOf(epcFile)
        }

        return toReturn
    }

    private fun decompress(base: File, epcData: EpcFile) {
        val targetFile = File(base, epcData.path)
        targetFile.parentFile.mkdirs()
        targetFile.createNewFile()

        when (epcData.type) {
            StoreType.TEXT   -> targetFile.writeText(epcData.content.replace("\\-", "-"), Charsets.UTF_8)
            StoreType.BINARY -> targetFile.writeBytes(epcData.content.parseHexBytes())
        }
    }
}

data class EpcFile(val path: String, val type: StoreType, val content: String) {
    override fun toString(): String {
        return "path=$path\n" +
                "type=$type\n" +
                "content=\n" +
                "$content\n"
    }
}

fun String.parseEpcFile(): EpcFile {
    val pathIndex = indexOf("path=") + 5
    val path = substring(pathIndex).split("\n")[0].trim().replace('/', File.separatorChar)

    val typeIndex = indexOf("type=") + 5
    val type = substring(typeIndex).split("\n")[0].trim()

    val contentIndex = indexOf("content=")
    val content = substring(contentIndex).split("\n", limit = 2)[1].trim()

    return EpcFile(path, StoreType.valueOf(type), content)
}

enum class StoreType {
    BINARY, TEXT
}
