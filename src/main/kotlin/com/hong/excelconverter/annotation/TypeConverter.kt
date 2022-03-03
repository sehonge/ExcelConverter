package com.hong.excelconverter.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class TypeConverter(val convert: KClass<*>)
