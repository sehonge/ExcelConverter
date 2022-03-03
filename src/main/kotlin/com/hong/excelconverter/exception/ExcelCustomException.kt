package com.hong.excelconverter.exception

open class ExcelCustomException(msg: String) : RuntimeException(msg)
class ExtensionNotValidException(msg: String) : ExcelCustomException(msg)
