package com.hong.excelconverter

import com.hong.excelconverter.dto.CellError
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.stream.Stream

class ExcelWriter: ExcelWritable {

    companion object {
        private val BLUE = IndexedColors.LIGHT_BLUE.index
        private val RED = IndexedColors.RED1.index
        private val ORANGE = IndexedColors.LIGHT_ORANGE.index
        private val GREEN = IndexedColors.LIGHT_GREEN.index
    }

    /*
        originFile을 parameter로 받을때 originFile의 크기가 매우 커진다면 이 로직은 매우 비효율적이다.
        어차피 실제 데이터가 시작되기 전의 셀의 내용은 변동이 없는 내용이므로 하드 코딩이 가능하다.
        따라서 실제 데이터가 시작되기 전의 셀은 직접 하드코딩으로 작성을 하고 그 이후에는 input: Stream<Any>에서 stream에서 instance를 하나씩 뽑으면서
        instance가 CellError라면 style을 적용해 색을 칠하고, CellError가 아니라면 style없이 그대로 작성하면 될 것 같다.
     */
    override fun writeExcel(originFile: File, input: Stream<Any>) : File {
        val workbook = paintErrorCell(XSSFWorkbook(originFile), input)

        val fileName: String = "${originFile.path.substringBefore(".xlsx")}Reject.${originFile.extension}"
        val file: File = File(fileName)
        val outputStream: FileOutputStream = FileOutputStream(file)
        val bufferedOutputStream: BufferedOutputStream = BufferedOutputStream(outputStream)

        workbook.write(bufferedOutputStream)
        bufferedOutputStream.flush()
        bufferedOutputStream.close()
        outputStream.close()

        return file // file을 넘긴 후, local에 저장되어있는 file을 삭제해야 한다.
    }

    private fun paintErrorCell(workbook: XSSFWorkbook, input: Stream<Any>) : XSSFWorkbook {
        val style: XSSFCellStyle = workbook.createCellStyle()
        style.fillPattern = FillPatternType.SOLID_FOREGROUND
        style.fillForegroundColor = BLUE

        input.forEach {
                instance: Any -> if (instance is CellError) {
                    val cell: Cell = workbook.getSheetAt(0).getRow(instance.row).getCell(instance.column)
                    cell.cellStyle = style
                }
        }
        return workbook
    }
}
