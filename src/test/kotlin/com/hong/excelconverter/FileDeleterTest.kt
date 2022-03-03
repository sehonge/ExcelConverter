package com.hong.excelconverter

import com.hong.excelconverter.fixture.TestFixtureB
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File
import java.util.stream.Stream

internal class FileDeleterTest {

    @Test
    @DisplayName("Local에 존재하는 file을 excelFileDelete를 사용하면 삭제가 되고 true를 반환한다.")
    fun `Local에 존재하는 file을 excelFileDelete를 사용하면 삭제가 되고 true를 반환한다`() {
        val path = "src/test/resources/ExcelMixedFile.xlsx"
        val file: File = File(path)
        val workbook: XSSFWorkbook = ExcelReader().getWorkbook(file)
        val stream: Stream<Any> = ExcelConverter().getObjectStream(workbook, TestFixtureB::class)

        // when
        val outputFile = ExcelWriter().writeExcel(file, stream)
        val isDeleted: Boolean = FileDeleter().fileDelete(outputFile)

        // then
        Assertions.assertEquals(true, isDeleted)
    }
}
