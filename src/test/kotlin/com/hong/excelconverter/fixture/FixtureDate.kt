package com.hong.excelconverter.fixture

import com.hong.excelconverter.annotation.Essential
import com.hong.excelconverter.annotation.Name
import com.hong.excelconverter.annotation.TypeConverter
import com.hong.excelconverter.dto.BusinessType
import com.hong.excelconverter.dto.BusinessTypeEnumConverter
import com.hong.excelconverter.dto.LocalDateConverter
import java.math.BigDecimal
import java.time.LocalDate

data class FixtureDate(
    @Name("checksum") @Essential val checksum: String,
    @Name("BrandName") val brandName: String?,
    @Name("BirthDay") @Essential @TypeConverter(convert = LocalDateConverter::class) val birthDay: LocalDate
    )

data class FixtureBusinessType(
    @Name("checksum") @Essential val checksum: String,
    @Name("BrandName") val brandName: String?,
    @Name("BusinessType") @TypeConverter(convert = BusinessTypeEnumConverter::class) val businessType: BusinessType
)

data class TestBigDecimal (
    @Name("checksum") @Essential val checksum: BigDecimal,
    @Name("BrandName") @Essential val brandName: String,
    @Name("BirthDay") @TypeConverter(convert = LocalDateConverter::class) val birthDay: LocalDate?
)

data class FixtureNoNameAnnotation (
    @Name("checksum") @Essential val checksum: String,
    @Essential val brandName: String,
    @Name("BirthDay") @TypeConverter(convert = LocalDateConverter::class) val birthDay: LocalDate?
    )
