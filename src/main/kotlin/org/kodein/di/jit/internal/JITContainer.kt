package org.kodein.di.jit.internal

import org.kodein.di.DKodein
import org.kodein.di.TT
import org.kodein.di.TypeToken
import org.kodein.di.jit.JIT
import org.kodein.di.jit.rawType
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import java.util.concurrent.ConcurrentHashMap

internal class JITContainer {

    private val _constructors = ConcurrentHashMap<Class<*>, DKodein.() -> Any>()

    private interface Element : AnnotatedElement {
        val classType: Class<*>
        val genericType: Type
        override fun isAnnotationPresent(annotationClass: Class<out Annotation>) = getAnnotation(annotationClass) != null
        override fun getDeclaredAnnotations(): Array<out Annotation> = annotations
        @Suppress("UNCHECKED_CAST")
        override fun <T : Annotation?> getAnnotation(annotationClass: Class<T>) = annotations.firstOrNull { annotationClass.isAssignableFrom(it.javaClass) } as T?

        override fun toString(): String
    }

    private fun getter(element: Element): DKodein.() -> Any? {

        fun Type.boundType() = when {
            this is WildcardType -> upperBounds[0]
            else -> this
        }

        fun getterFunction(getter: DKodein.() -> Any?) = getter

        return when {
            element.classType == Lazy::class.java -> { // Must be first
                val boundType = (element.genericType as ParameterizedType).actualTypeArguments[0].boundType()

                class LazyElement : Element by element {
                    override val classType: Class<*> get() = boundType.rawType()
                    override val genericType: Type get() = boundType
                    override fun toString() = element.toString()
                }

                val getter = getter(LazyElement())

                getterFunction { lazy { getter() } }
            }
            else -> {
                @Suppress("UNCHECKED_CAST")
                val boundType = TT(element.genericType) as TypeToken<out Any>
                getterFunction { Instance(boundType) }
            }
        }
    }

    private fun createConstructor(cls: Class<*>): DKodein.() -> Any {

        val constructor = cls.declaredConstructors.firstOrNull { it.isAnnotationPresent(JIT::class.java) }
            ?: if (cls.declaredConstructors.size == 1) cls.declaredConstructors[0]
            else throw IllegalArgumentException("Class ${cls.name} must either have only one constructor or an @JIT annotated constructor")

        class ConstructorElement(private val _index: Int) : Element {
            override val classType: Class<*> get() = constructor.parameterTypes[_index]
            override val genericType: Type get() = constructor.genericParameterTypes[_index]
            override fun getAnnotations() = constructor.parameterAnnotations[_index]
            override fun toString() = "Parameter ${_index + 1} of $constructor"
        }

        val getters = (0 until constructor.parameterTypes.size).map { getter(ConstructorElement(it)) }

        val isAccessible = constructor.isAccessible

        return {
            val arguments = Array<Any?>(getters.size) { null }
            getters.forEachIndexed { i, getter -> arguments[i] = getter() }

            if (!isAccessible) constructor.isAccessible = true
            try {
                constructor.newInstance(*arguments)
            } finally {
                if (!isAccessible) constructor.isAccessible = false
            }
        }
    }

    private fun findConstructor(cls: Class<*>) = _constructors.getOrPut(cls) { createConstructor(cls) }

    /** @suppress */
    internal fun <T : Any> newInstance(kodein: DKodein, cls: Class<T>): T {
        val constructor = findConstructor(cls)

        @Suppress("UNCHECKED_CAST")
        val instance = kodein.constructor() as T

        kodein.container

        return instance
    }
}
