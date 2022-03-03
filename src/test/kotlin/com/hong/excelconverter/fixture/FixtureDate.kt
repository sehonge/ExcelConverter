package com.hong.excelconverter.fixture

import com.hong.excelconverter.dto.Essential
import com.hong.excelconverter.dto.LocalDateConverter
import com.hong.excelconverter.dto.Name
import com.hong.excelconverter.dto.TypeConverter
import java.time.LocalDate

data class FixtureDate(
    @Name("checksum") @Essential val checksum: String,
    @Name("BrandName") val brandName: String?,
    @Name("BirthDay") @Essential @TypeConverter(convert = LocalDateConverter::class) val birthDay: LocalDate
    )
