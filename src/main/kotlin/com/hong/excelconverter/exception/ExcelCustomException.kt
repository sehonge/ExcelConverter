package com.hong.excelconverter.exception

import java.lang.RuntimeException

open class ExcelCustomException(msg: String) : RuntimeException(msg)
class ExtensionNotValidException(msg: String) : ExcelCustomException(msg)