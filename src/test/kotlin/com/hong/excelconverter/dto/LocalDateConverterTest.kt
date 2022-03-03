package com.hong.excelconverter.dto

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.LocalDate

@DisplayName("LocalDateConverterTest")
internal class LocalDateConverterTest {

    @ParameterizedTest(name="Int type의 {0}를 LocalDateConverter로 convert 하면 {1} 값을 리턴한다.")
    @CsvSource(value=[
        "19940605,1994-06-05",
        "20210228,2021-02-28",
        "20200229,2020-02-29",
        ])
    fun `Int type의 {0}를 LocalDateConverter로 convert 하면 {1} 값을 던진다`(input: Int, expectedDate: LocalDate?) {
        // given
        val given: Int = input

        // when
        val convertedData = LocalDateConverter.convert(given)

        // then
        Assertions.assertThat(convertedData).isEqualTo(expectedDate)
    }

    @ParameterizedTest(name = "형식이 잘못된 input : {0}를 LocalDateConverter로 convert하면 Null 값을 반환한다.")
    @CsvSource(value = [
        "2014",
        "20140400",
        "1994065",
        "20101040",
        "20021320",
        "19940540",
        "20210231",
        "20210232"
    ])
    fun `형식이 잘못된 input을 LocalDateConverter로 convert하면 DateTimeParseException을 던진다`(input: Int) {
        // given
        val given: Int = input

        // when
        val date = LocalDateConverter.convert(given)

        // then
        Assertions.assertThat(date).isNull()
    }
}
