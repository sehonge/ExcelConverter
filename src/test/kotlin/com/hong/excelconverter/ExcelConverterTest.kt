package com.hong.excelconverter

import com.hong.excelconverter.dto.CellError
import com.hong.excelconverter.dto.TransferDateType
import com.hong.excelconverter.fixture.FixtureDate
import com.hong.excelconverter.fixture.TestBigDecimal
import com.hong.excelconverter.fixture.TestFixtureB
import com.hong.excelconverter.fixture.TestLocalDate
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.File
import java.math.BigDecimal
import java.time.LocalDate
import java.util.stream.Stream

internal class ExcelConverterTest {

    @Nested
    @DisplayName("Excel file를 읽어서 Stream을 뽑아내는 경우")
    inner class GetObjectStreamTest {

        @Test
        @DisplayName("Excel file의 Row에서 모든 데이터가 정상적으로 입력되어 있으면, Stream<T>를 반환한다.")
        fun `Excel file의 Row에서 모든 데이터가 정상적으로 입력되어 있으면, Stream{T}를 반환한다`() {
            // given
            val path = "src/test/resources/ExcelFileFixtureDate.xlsx"
            val file = File(path)
            val given = FixtureDate("check", "pong", LocalDate.of(1994,6,5))
            val workbook = ExcelReader().getWorkbook(file)

            // when
            val stream: Stream<Any> = ExcelConverter().getObjectStream(workbook, given::class)

            // then
            stream.forEach {
                Assertions.assertThat(it::class).isEqualTo(given::class)
            }
        }

        @Test
        @DisplayName("Excel file Row에서 잘못된 데이터가 존재하면, Stream<CellError>를 반환한다.")
        fun `Excel file에서 잘못된 데이터가 존재하면, Stream{CellError} 반환한다`() {
            // given
            val path = "src/test/resources/ExcelWrongFile.xlsx"
            val file = File(path)
            val given = TestFixtureB("check", "pong", TransferDateType.FIRST)
            val workbook = ExcelReader().getWorkbook(file)

            // when
            val stream: Stream<Any> = ExcelConverter().getObjectStream(workbook, given::class)

            // then
            stream.forEach {
                Assertions.assertThat(it::class).isEqualTo(CellError::class)
            }
        }

        @Test
        @DisplayName("Excel file의 각 row를 읽으면서 잘못된 데이터(CellError)와 문제가 없는 데이터(T)를 Stream<Any>로 반환한다.")
        fun `Excel file의 각 row를 읽으면서 잘못된 데이터{CellError}와 문제가 없는 데이터{T}를 Stream{Any}로 반환한다`() {
            // given
            val path = "src/test/resources/ExcelMixedFile.xlsx"
            val file = File(path)
            val given = TestFixtureB("check", "pong", TransferDateType.FIRST)
            val workbook = ExcelReader().getWorkbook(file)

            // when
            val stream: Stream<Any> = ExcelConverter().getObjectStream(workbook, given::class)

            // then
            stream.forEach {
                Assertions.assertThat(it::class).isIn(TestFixtureB::class, CellError::class)
            }
        }

        @Test
        @DisplayName("Excel file의 row에서 필수적인 property의 정보를 가진 cell이 공백이라면 CellError를 반환한다.")
        fun `Excel file의 row에서 필수적인 property의 정보를 가진 cell이 공백이라면 CellError를 반환한다`() {
            // given
            val path = "src/test/resources/ExcelBlank.xlsx"
            val file = File(path)
            val given = TestFixtureB("check", "pong", TransferDateType.FIRST)
            val workbook = ExcelReader().getWorkbook(file)

            // when
            val stream: Stream<Any> = ExcelConverter().getObjectStream(workbook, given::class)

            // then
            stream.forEach {
                Assertions.assertThat(it::class).isIn(TestFixtureB::class, CellError::class)
            }
        }

        @Test
        @DisplayName("Excel file에서 cell에 대응되는 property의 type이 BigDecimal인 경우, 이를 BigDecimal type으로 변환한다.")
        fun `Excel file에서 cell에 대응되는 property의 type이 BigDecimal인 경우, cell의 numericType data를 BigDecimal type으로 변환한다`() {
            // given
            val path = "src/test/resources/ExcelFileBigDecimal.xlsx"
            val file = File(path)
            val given = TestBigDecimal(BigDecimal("201.02"), "pong", TransferDateType.FIRST)
            val workbook = ExcelReader().getWorkbook(file)

            // when
            val stream: Stream<Any> = ExcelConverter().getObjectStream(workbook, given::class)

            // then
            stream.forEach {
                Assertions.assertThat(it::class).isEqualTo(given::class)
            }
        }

        @Test
        @DisplayName("Excel file에서 cell의 날짜 정보가 잘못 입력된 경우, CellError로 처리한다.")
        fun `Excel file에서 cell의 날짜 정보가 잘못 입력된 경우, CellError로 처리한다`() {
            // given
            val path = "src/test/resources/ExcelWrongDateFile.xlsx"
            val file = File(path)
            val given = TestLocalDate("201.02", "pong", TransferDateType.FIRST, LocalDate.of(1994,6,5))
            val workbook = ExcelReader().getWorkbook(file)

            // when
            val stream: Stream<Any> = ExcelConverter().getObjectStream(workbook, given::class)

            // then
            stream.forEach {
                Assertions.assertThat(it::class).isEqualTo(CellError::class)
            }
        }

    }
}
