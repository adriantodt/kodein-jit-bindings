package org.kodein.di.jit

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType

internal fun Type.rawType(): Class<*> = when (this) {
    is Class<*> -> this
    is ParameterizedType -> rawType.rawType()
    is WildcardType -> upperBounds[0].rawType()
    else -> throw IllegalStateException("Cannot get raw type of $this")
}