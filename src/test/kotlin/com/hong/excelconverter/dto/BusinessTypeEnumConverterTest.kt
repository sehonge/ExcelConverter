package com.hong.excelconverter.dto

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@DisplayName("BusinessTypeEnumConverterTest")
internal class BusinessTypeEnumConverterTest {

    @ParameterizedTest(name = "String type의 {0}를 BusinessTypeEnumConverter로 convert 하면 {1} 값을 리턴한다.")
    @CsvSource(value = [
        "프랜차이즈, FRANCHISE",
        "개인사업자, INDIVIDUAL"
    ])
    fun test01(input: String, expectedData: BusinessType?) {
        // given
        val given: String = input

        // when
        val convertedData: BusinessType? = BusinessTypeEnumConverter.convert(given)

        // then
        Assertions.assertThat(convertedData).isEqualTo(expectedData)
    }

    @ParameterizedTest(name = "형식이 잘못된 input {0}를 BusinessTypeEnumConverter로 convert 하면 Null 값을 리턴한다.")
    @CsvSource(value = [
        "프랜차이",
        "개인",
        "1"
    ])
    fun test02(input: String) {
        // given
        val given: String = input

        // when
        val convertedData: BusinessType? = BusinessTypeEnumConverter.convert(given)

        // then
        Assertions.assertThat(convertedData).isNull()
    }
}
