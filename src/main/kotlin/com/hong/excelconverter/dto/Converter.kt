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

object FrontPhotoUrlEnumConverter : Converter<Int, FrontPhotoUrlType?> {
    override fun convert(input: Int): FrontPhotoUrlType? {
        return FrontPhotoUrlType.values().find {
            it.codeNumber == input
        }
    }
}

object AlipayFeeContractIdEnumConverter : Converter<Int, AlipayFeeContractIdType?> {
    override fun convert(input: Int): AlipayFeeContractIdType? {
        return AlipayFeeContractIdType.values().find {
            it.codeNumber == input
        }
    }
}

object TransferDateEnumConverter : Converter<Int, TransferDateType?> {
    override fun convert(input: Int): TransferDateType? {
        return TransferDateType.values().find {
            it.codeNumber == input
        }
    }
}

object BankcorpCdEnumConverter : Converter<String, BankcorpCdType?> {
    override fun convert(input: String): BankcorpCdType? {
        return BankcorpCdType.values().find {
            it.codeDesc.contains(input)
        }
    }
}
