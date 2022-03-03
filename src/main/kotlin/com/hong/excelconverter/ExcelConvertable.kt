package com.hong.excelconverter


import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.util.stream.Stream
import kotlin.reflect.KClass

interface ExcelConvertable {
    fun <T : Any> getObjectStream(workbook: XSSFWorkbook, metaData: KClass<T>): Stream<Any>
}
