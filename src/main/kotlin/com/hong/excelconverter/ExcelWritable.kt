package com.hong.excelconverter

import java.io.File
import java.util.stream.Stream

interface ExcelWritable {
    fun writeExcel(originFile: File, input: Stream<Any>) : File
}
