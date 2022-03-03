package com.hong.excelconverter

import com.hong.excelconverter.fixture.TestFixtureB
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.File
import java.util.stream.Stream

internal class ExcelWriterTest {

    @Nested
    @DisplayName("엑셀파일을 생성할 때")
    inner class WriteExcelTest {

        @Test()
        @DisplayName("ExcelConverter의 getObjectStream으로부터 나온 Stream<Any>의 정보와 origin file을 사용해 잘못 작성된 cell에 색을 칠한 excelFile을 반환한다. ")
        fun `ExcelConverter의 getObjectStream으로부터 나온 Stream{Any}의 CellError들의 정보와 originFile을 사용해 잘못 작성된 cell에 색을 칠한 excelFile을 반환한다` () {
            // given
            val path = "src/test/resources/ExcelMixedFile.xlsx"
            val file: File = File(path)
            val workbook: XSSFWorkbook = ExcelReader().getWorkbook(file)
            val stream: Stream<Any> = ExcelConverter().getObjectStream(workbook, TestFixtureB::class)

            // when
            val outputFile = ExcelWriter().writeExcel(file, stream)

            // then
            val workbook2 = ExcelReader().getWorkbook(outputFile)
            val cellForegroundColor: Short = workbook2.getSheetAt(0).getRow(10).getCell(1).cellStyle.fillForegroundColor
            assertEquals(cellForegroundColor ,IndexedColors.LIGHT_BLUE.index)

            FileDeleter().fileDelete(outputFile) // Local에 저장되어 있는 file을 삭제
        }
    }
}
