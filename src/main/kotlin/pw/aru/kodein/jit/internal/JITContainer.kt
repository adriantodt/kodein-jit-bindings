package pw.aru.kodein.jit.internal

import org.kodein.di.DKodein
import org.kodein.di.TT
import org.kodein.di.TypeToken
import pw.aru.kodein.jit.JIT
import pw.aru.kodein.jit.Singleton
import pw.aru.kodein.jit.rawType
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType

@Suppress("UNCHECKED_CAST")
internal class JITContainer(private val kodein: DKodein) {

    private interface Element : AnnotatedElement {
        override fun isAnnotationPresent(c: Class<out Annotation>) = getAnnotation(c) != null
        override fun getDeclaredAnnotations(): Array<out Annotation> = annotations
        override fun <T : Annotation?> getAnnotation(c: Class<T>) = annotations.firstOrNull { c.isAssignableFrom(it.javaClass) } as T?

        val classType: Class<*>
        val genericType: Type
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
                val boundType = TT(element.genericType) as TypeToken<out Any>
                getterFunction { Instance(boundType) }
            }
        }
    }

    private fun createConstructor(cls: Class<*>): DKodein.() -> Any {

        val constructor = cls.declaredConstructors.firstOrNull { it.isAnnotationPresent(JIT::class.java) }
            ?: if (cls.declaredConstructors.size == 1) cls.declaredConstructors[0]
            else throw IllegalArgumentException("Class ${cls.name} must either have only one constructor or an @JIT annotated constructor")

        class ConstructorElement(private val i: Int) : Element {
            override val classType: Class<*> get() = constructor.parameterTypes[i]
            override val genericType: Type get() = constructor.genericParameterTypes[i]
            override fun getAnnotations() = constructor.parameterAnnotations[i]
            override fun toString() = "Parameter ${i + 1} of $constructor"
        }

        val getters = (0 until constructor.parameterTypes.size).map { getter(ConstructorElement(it)) }

        return {
            constructor.isAccessible = true
            constructor.newInstance(
                *getters.map { it() }.toTypedArray()
            )
        }
    }

    private val constructors = object : ClassValue<DKodein.() -> Any>() {
        override fun computeValue(type: Class<*>) = createConstructor(type)
    }

    private val singletons = object : ClassValue<Any>() {
        override fun computeValue(type: Class<*>) = constructors[type]?.let { kodein.it() }
    }

    internal fun <T : Any> newInstance(cls: Class<T>): T {
        val instance = if (cls.isAnnotationPresent(Singleton::class.java)) singletons[cls] else (constructors[cls]).invoke(kodein)
        return instance as T
    }
}
