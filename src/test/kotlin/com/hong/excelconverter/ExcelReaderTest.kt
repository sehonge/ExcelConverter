package com.hong.excelconverter

import com.hong.excelconverter.exception.ExtensionNotValidException
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File

@DisplayName("Excel Read Test")
internal class ExcelReaderTest {

    @Test
    @DisplayName("ExcelFileFixture를 읽어 들이면 SheetAt(0)의 Row가 0이 아니다.")
    fun `ExcelFileFixture를 읽어 들이면 SheetAt(0)의 Row가 0이 아니다`() {
        val path = "src/test/resources/ExcelFileFixtureDate.xlsx"
        val file: File = File(path)
        val workbook: XSSFWorkbook = ExcelReader().getWorkbook(file)
        Assertions.assertThat(workbook.getSheetAt(0).count()).isGreaterThan(0)
    }

    @Test
    @DisplayName("ExcelFile의 extension이 .xlsx가 아니라면 ExtensionNotValidException를 던진다.")
    fun `ExcelFile의 extension이 xlsx가 아니라면 ExtensionNotValidException를 던진다`() {
        // given
        val path = "src/test/resources/ExcelFileWrongExtension.txt"
        val file: File = File(path)

        // when
        // then
        assertThrows<ExtensionNotValidException> {
            ExcelReader().checkExtension(file)
        }
    }

    @Test
    @DisplayName("ExcelFile의 extension이 .xlsx가 라면 true를 반환한다.")
    fun `ExcelFile의 extension이 xlsx가 라면 true를 반환한다`() {
        // given
        val path = "src/test/resources/ExcelFileFixtureDate.xlsx"
        val file: File = File(path)

        // when
        val checkExtension = ExcelReader().checkExtension(file)

        // then
        Assertions.assertThat(checkExtension).isTrue
    }
}
