package com.hong.excelconverter

import java.io.File

interface FileDeletable {
    fun fileDelete(file: File) : Boolean
}
