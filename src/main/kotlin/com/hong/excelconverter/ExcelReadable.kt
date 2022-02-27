package com.hong.excelconverter

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File

interface ExcelReadable {
    fun getWorkbook(file: File): XSSFWorkbook
    fun checkExtension(file : File) : Boolean
}
