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
                target.writeText(epcFile.toString())
            } else {
                target.appendText("---\n")
                target.appendText(epcFile.toString())
            }
        }

        return target
    }

    fun decompress(source: File, target: File) {
        val text = source.readText()
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
            val relativePath = current.relativeTo(base).toString()

            val epcFile = when {
                binFileExtensions.contains(current.extension.toLowerCase())  -> EpcFile(relativePath, StoreType.BINARY, current.readBytes().toHexString())
                textFileExtensions.contains(current.extension.toLowerCase()) -> EpcFile(relativePath, StoreType.TEXT, current.readText().replace("-", "\\-"))
                else                                                         -> {
                    when (defaultStoreType) {
                        StoreType.TEXT   -> EpcFile(relativePath, StoreType.TEXT, current.readText().replace("-", "\\-"))
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
            StoreType.TEXT   -> targetFile.writeText(epcData.content.replace("\\-", "-"))
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
    val path = substring(pathIndex).split("\n")[0].trim()

    val typeIndex = indexOf("type=") + 5
    val type = substring(typeIndex).split("\n")[0].trim()

    val contentIndex = indexOf("content=")
    val content = substring(contentIndex).split("\n", limit = 2)[1].trim()

    return EpcFile(path, StoreType.valueOf(type), content)
}

enum class StoreType {
    BINARY, TEXT
}
