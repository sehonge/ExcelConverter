package com.hong.excelconverter

import com.hong.excelconverter.exception.ExtensionNotValidException
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File

class ExcelReader : ExcelReadable {

    override fun checkExtension(file: File) : Boolean {
        return when (file.extension) {
            "xls" -> true
            "xlsx" -> true
            else -> throw ExtensionNotValidException("fileName: ${file.name}, Wrong extension : ${file.extension}")
        }
    }

    override fun getWorkbook(file: File): XSSFWorkbook {
        checkExtension(file)

        return XSSFWorkbook(file)
    }
}
