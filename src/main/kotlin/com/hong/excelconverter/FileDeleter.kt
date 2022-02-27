package com.hong.excelconverter

import java.io.File
import java.nio.file.NoSuchFileException

class FileDeleter : FileDeletable {

    override fun fileDelete(excelFile: File) : Boolean {
        isExist(excelFile)

        return excelFile.delete()
    }

    private fun isExist(excelFile: File) {
        if (!excelFile.exists()) {
            throw NoSuchFileException("file(${excelFile.absolutePath}) does not exist")
        }
    }
}