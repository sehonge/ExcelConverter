package com.hong.excelconverter.dto

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Name(val fieldName: String)