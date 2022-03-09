package com.hong.excelconverter

import com.hong.excelconverter.fixture.FixtureDate
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
        val path = "src/test/resources/ExcelWrongFile.xlsx"
        val file: File = File(path)
        val workbook: XSSFWorkbook = ExcelReader().getWorkbook(file)
        val stream: Stream<Any> = ExcelConverter().getObjectStream(workbook, FixtureDate::class)

        // when
        val outputFile = ExcelWriter().writeExcel(file, stream)
        val isDeleted: Boolean = FileDeleter().fileDelete(outputFile)

        // then
        Assertions.assertEquals(true, isDeleted)
    }

    @Test
    @DisplayName("Local에 존재하지않는 file을 삭제하려 하면 NoSuchFileException를 반환한다.")
    fun testNoSuckFileException() {
        val path = "src/test/resources/noFile.xlsx"
        val file: File = File(path)

        // when
        // then
        Assertions.assertThrows(NoSuchFileException::class.java) {
            FileDeleter().fileDelete(file)
        }
    }

}
