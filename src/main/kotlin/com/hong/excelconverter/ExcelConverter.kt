package com.hong.excelconverter

import com.hong.excelconverter.annotation.Essential
import com.hong.excelconverter.annotation.Name
import com.hong.excelconverter.annotation.TypeConverter
import com.hong.excelconverter.dto.CellError
import com.hong.excelconverter.dto.Either
import com.hong.excelconverter.dto.Left
import com.hong.excelconverter.dto.Right
import com.hong.excelconverter.exception.NullAnnotationException
import com.hong.excelconverter.exception.NullElementException
import com.hong.excelconverter.exception.NullInstanceException
import lombok.extern.slf4j.Slf4j
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.lang.reflect.Method
import java.math.BigDecimal
import java.util.stream.Stream
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

@Slf4j
class ExcelConverter : ExcelConvertable {

    companion object {
        private const val DATA_START_ROW = 9
        private const val META = "KYC"
    }

    override fun <T : Any> getObjectStream(workbook: XSSFWorkbook, metaData: KClass<T>): Stream<Any> {
        val annotationToProperty : Map<String, String> = extractAnnotationAndProperties(metaData)
        var columnIdxToPropertyName : Map<Int, String> = HashMap<Int, String>()
        val instances: MutableList<T> = ArrayList<T>()
        val errors: MutableList<CellError> = ArrayList<CellError>()
        val propertyNameToProperty = metaData.declaredMemberProperties.associateBy { properties: KProperty1<T, *> -> properties.name }

        workbook.getSheetAt(0).forEach { row: Row ->
            if (row.rowNum >= DATA_START_ROW) {
                when (val result: Either<List<CellError>, T> = getInstance(row, metaData, columnIdxToPropertyName, propertyNameToProperty)) {
                    is Left -> errors.addAll(result.left)
                    is Right -> instances.add(result.right)
                }
            }
            if (row.getCell(0).stringCellValue.uppercase() == META) {
                columnIdxToPropertyName = extractColumnIndexFromExcel(row, annotationToProperty)
            }
        }

        return Stream.concat(errors.stream(), instances.stream())
    }

    private fun <T : Any> extractAnnotationAndProperties(metaData: KClass<T>) : Map<String, String> {
        val properties: Collection<KProperty1<T, *>> = metaData.declaredMemberProperties

        return properties.associate { property: KProperty1<T, *> ->
            val annotation = property.findAnnotation<Name>() ?: throw NullAnnotationException("Name Annotation is Not Exist at property(${property.name})")
            annotation.fieldName to property.name
        }
    }

    private fun <T : Any> getInstance(row: Row, metadata: KClass<T>, columnIdxToPropertyName : Map<Int, String>, propertyNameToProperty : Map<String, KProperty1<T, *>>) : Either<List<CellError>, T> {
        val propertyNameToValue: MutableMap<String, Any?> = HashMap<String, Any?>()
        val cellErrorList: MutableList<CellError> = ArrayList<CellError>()

        val columnIdxToProperty: MutableMap<Int, KProperty1<T, *>> = HashMap<Int, KProperty1<T, *>>()
        columnIdxToPropertyName.forEach { it: Map.Entry<Int, String> ->
            columnIdxToProperty[it.key] = propertyNameToProperty[it.value] ?: throw NullElementException("Value of ${it.value} is Not Exist in propertyNameToProperty")
        }

        row.forEach {
                cell: Cell ->
            if (columnIdxToPropertyName.contains(cell.columnIndex)) {
                when (val getData = getCellData(cell, row.rowNum, columnIdxToProperty)) {
                    is Left -> cellErrorList.add(getData.left)
                    is Right -> propertyNameToValue[columnIdxToPropertyName[cell.columnIndex]?:
                        throw NullElementException("Value of ${cell.columnIndex} is Not Exist in columnIdxToPropertyName")] = getData.right
                }
            }
        }

        return if (cellErrorList.isEmpty()) Right(makeInstance(metadata, propertyNameToValue))
        else Left(cellErrorList)
    }

    private fun <T : Any> makeInstance(metaData: KClass<T>, propertyNameToValue: Map<String, Any?>): T {
        val constructor = metaData.primaryConstructor ?: throw NullInstanceException("No PrimaryConstructor")
        val parameters: List<KParameter> = constructor.parameters
        val argsParameters: MutableMap<KParameter, Any?> = HashMap<KParameter, Any?>()

        parameters.forEach { parameter: KParameter ->
            argsParameters[parameter] = propertyNameToValue[parameter.name]
        }
        return constructor.callBy(argsParameters)
    }

    private fun <T : Any> getCellData(cell: Cell, rowIdx: Int, columnIdxToProperty: Map<Int, KProperty1<T, *>>) : Either<CellError, Any?> {
        val property = columnIdxToProperty[cell.columnIndex] ?: throw NullElementException("Value of ${cell.columnIndex} is Not Exist in columnIdxToPropertyName")
        val converter: TypeConverter? = property.findAnnotation<TypeConverter>()

        return when (cell.cellType) {
            CellType.STRING -> {
                getConvertedData(cell.stringCellValue, converter, cell.columnIndex, rowIdx)
            }
            CellType.NUMERIC -> {
                if (property.returnType.jvmErasure.createType() == BigDecimal::class.createType()) {
                    getConvertedData(cell.numericCellValue.toBigDecimal(), converter, cell.columnIndex, rowIdx)
                } else {
                    getConvertedData(cell.numericCellValue.toInt(), converter, cell.columnIndex, rowIdx)
                }
            }
            CellType.BLANK -> {
                if (property.findAnnotation<Essential>() != null) Left(CellError(row = rowIdx, column = cell.columnIndex, "${property.name} must not have null value"))
                else Right(null)
            }
            else -> Left(CellError(row = rowIdx, column = cell.columnIndex, "Not Valid CellType"))
        }
    }

    private fun getConvertedData(cellData: Any, converter: TypeConverter?, columnIdx: Int, rowIdx: Int) : Either<CellError, Any> {
        return if (converter != null) {
            val convertedValue = convertData(cellData, converter.convert.objectInstance)

            if (convertedValue == null) Left(CellError(row = rowIdx, column = columnIdx, "There is no converted data from ${cellData} at ${converter.convert}"))
            else Right(convertedValue)
        } else {
            Right(cellData)
        }
    }

    private fun <T> convertData(input: T, instance : Any?) : Any? {
        instance ?: throw NullInstanceException("Object Instance is Not Exist")
        val method = instance.javaClass.declaredMethods.find { it: Method -> it.name.equals("convert") }
        return method?.invoke(instance, input)
    }

    private fun extractColumnIndexFromExcel(row: Row, annotationToProperty: Map<String, String>): Map<Int, String> {
        val columnIdxToPropertyName: MutableMap<Int, String> = HashMap<Int, String>()
        var colIdx: Int = 0

        row.forEach { cell: Cell ->
            if (cell.cellType == CellType.STRING && cell.columnIndex > 0) {
                columnIdxToPropertyName[colIdx++] = annotationToProperty[cell.stringCellValue].toString()
            }
            else colIdx++
        }

        return columnIdxToPropertyName
    }
}
