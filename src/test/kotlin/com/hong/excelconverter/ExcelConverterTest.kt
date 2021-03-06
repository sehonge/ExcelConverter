package com.hong.excelconverter

import com.hong.excelconverter.dto.BusinessType
import com.hong.excelconverter.dto.CellError
import com.hong.excelconverter.exception.NullAnnotationException
import com.hong.excelconverter.exception.NullElementException
import com.hong.excelconverter.fixture.FixtureBigDecimal
import com.hong.excelconverter.fixture.FixtureBusinessType
import com.hong.excelconverter.fixture.FixtureDate
import com.hong.excelconverter.fixture.FixtureNoNameAnnotation
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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
        fun `Excel file에서 잘못된 데이터가 존재하면, Stream{CellError}로 반환한다`() {
            // given
            val path = "src/test/resources/ExcelWrongFile.xlsx"
            val file = File(path)
            val given = FixtureDate("check", "pong", LocalDate.of(1994,6,5))
            val workbook = ExcelReader().getWorkbook(file)

            // when
            val stream: Stream<Any> = ExcelConverter().getObjectStream(workbook, given::class)

            // then
            Assertions.assertThat(stream.map { any -> any::class })
                .contains(CellError::class)
        }

        @Test
        @DisplayName("Excel file의 각 row를 읽으면서 잘못된 데이터(CellError)와 문제가 없는 데이터(T)를 Stream<Any>로 반환한다.")
        fun `Excel file의 각 row를 읽으면서 잘못된 데이터{CellError}와 문제가 없는 데이터{T}를 Stream{Any}로 반환한다`() {
            // given
            val path = "src/test/resources/ExcelMixedFile.xlsx"
            val file = File(path)
            val given = FixtureBusinessType("check", "pong", BusinessType.FRANCHISE)
            val workbook = ExcelReader().getWorkbook(file)

            // when
            val stream: Stream<Any> = ExcelConverter().getObjectStream(workbook, given::class)

            // then
            stream.forEach {
                Assertions.assertThat(it::class).isIn(given::class, CellError::class)
            }
        }

        @Test
        @DisplayName("Excel file의 row에서 필수적인 property의 정보를 가진 cell이 공백이라면 CellError를 반환한다.")
        fun `Excel file의 row에서 필수적인 property의 정보를 가진 cell이 공백이라면 CellError를 반환한다`() {
            // given
            val path = "src/test/resources/ExcelMixedFile.xlsx"
            val file = File(path)
            val given = FixtureBusinessType("check", "pong", BusinessType.FRANCHISE)
            val workbook = ExcelReader().getWorkbook(file)

            // when
            val stream: Stream<Any> = ExcelConverter().getObjectStream(workbook, given::class)

            // then
            stream.forEach {
                Assertions.assertThat(it::class).isIn(given::class, CellError::class)
            }
        }

        @Test
        @DisplayName("Excel file에서 cell에 대응되는 property의 type이 BigDecimal인 경우, 이를 BigDecimal type으로 변환한다.")
        fun `Excel file에서 cell에 대응되는 property의 type이 BigDecimal인 경우, cell의 numericType data를 BigDecimal type으로 변환한다`() {
            // given
            val path = "src/test/resources/ExcelFileBigDecimal.xlsx"
            val file = File(path)
            val given = FixtureBigDecimal(BigDecimal("201.02"), "pong", LocalDate.of(1994,6,5))
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
            val path = "src/test/resources/ExcelFileWrongDate.xlsx"
            val file = File(path)
            val given = FixtureDate("check", "pong", LocalDate.of(1994,6,5))
            val workbook = ExcelReader().getWorkbook(file)

            // when
            val stream: Stream<Any> = ExcelConverter().getObjectStream(workbook, given::class)

            // then
            stream.forEach {
                Assertions.assertThat(it::class).isEqualTo(CellError::class)
            }
        }

        @Test
        @DisplayName("getObjectStream의 parameter인 metaData 클래스 선언에 Name Annotation이 없다면, NullAnnotationException을 던진다.")
        fun `getObjectStream의 parameter인 metaData 클래스 선언에 Name Annotation이 없다면, NullAnnotationException을 던진다`() {
            // given
            val path = "src/test/resources/ExcelFileFixtureDate.xlsx"
            val file = File(path)
            val given = FixtureNoNameAnnotation("check", "pong", LocalDate.of(1994,6,5))
            val workbook = ExcelReader().getWorkbook(file)

            // when
            // then
            assertThrows<NullAnnotationException> {
                ExcelConverter().getObjectStream(workbook, given::class)
            }
        }

        @Test
        @DisplayName("ExcelFile에 metaData 클래스의 Name Annotation에 대응되는 Column의 이름이 잘못된 경우 NullElementException을 던진다.")
        fun `ExcelFile에 metaData 클래스의 Name Annotation에 대응되는 Column의 이름이 잘못된 경우 NullElementException을 던진다`() {
            // given
            val path = "src/test/resources/ExcelFileWrongColumnName.xlsx"
            val file = File(path)
            val given = FixtureDate("check", "pong", LocalDate.of(1994,6,5))
            val workbook = ExcelReader().getWorkbook(file)

            // when
            // then
            val exception: NullElementException = assertThrows<NullElementException> {
                ExcelConverter().getObjectStream(workbook, given::class)
            }
            println(exception.message);
        }

        /*
        순회하는 과정에서 검출해야할듯.
         */
        @Test
        @DisplayName("ExcelFile에 metaData 클래스의 Name Annotation에 대응되는 Column이 Blank인 경우 NullElementException을 던진다.")
        fun `ExcelFile에 metaData 클래스의 Name Annotation에 대응되는 Column이 Blank인 경우 NullElementException을 던진다`() {
            // given
            val path = "src/test/resources/ExcelFileWrongColumn.xlsx"
            val file = File(path)
            val given = FixtureDate("check", "pong", LocalDate.of(1994,6,5))
            val workbook = ExcelReader().getWorkbook(file)

            // when
            // then
            val exception: NullElementException = assertThrows<NullElementException> {
                ExcelConverter().getObjectStream(workbook, given::class)
            }
            println(exception.message);
        }

    }
}
