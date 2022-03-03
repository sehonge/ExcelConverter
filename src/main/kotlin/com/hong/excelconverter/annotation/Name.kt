package com.hong.excelconverter.annotation

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Name(val fieldName: String)
