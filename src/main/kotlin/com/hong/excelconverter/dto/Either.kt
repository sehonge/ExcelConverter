package com.hong.excelconverter.dto

sealed class Either<out L, out R>
class Left<out L> (val left: L) : Either<L, Nothing>()
class Right<out R> (val right: R) : Either<Nothing, R>()
