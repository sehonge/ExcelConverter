package com.hong.excelconverter.dto

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle

interface Converter<In, Out> {
    fun convert(input: In) : Out
}

object LocalDateConverter : Converter<Int, LocalDate?> {
    override fun convert(input: Int): LocalDate? {
        var strTime = input.toString()

        return try {
            LocalDate.parse(strTime, DateTimeFormatter.BASIC_ISO_DATE.withResolverStyle(ResolverStyle.STRICT))
        } catch (e : DateTimeParseException) {
            null
        }
    }
}

object BusinessTypeEnumConverter : Converter<String, BusinessType?> {
    override fun convert(input: String): BusinessType? {
        return BusinessType.values().find {
            it.Desc == input
        }
    }

}
