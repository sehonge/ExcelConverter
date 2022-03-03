package com.hong.excelconverter.fixture

import com.hong.excelconverter.annotation.Essential
import com.hong.excelconverter.annotation.Name
import com.hong.excelconverter.annotation.TypeConverter
import com.hong.excelconverter.dto.LocalDateConverter
import java.time.LocalDate

data class FixtureDate(
    @Name("checksum") @Essential val checksum: String,
    @Name("BrandName") val brandName: String?,
    @Name("BirthDay") @Essential @TypeConverter(convert = LocalDateConverter::class) val birthDay: LocalDate
    )
