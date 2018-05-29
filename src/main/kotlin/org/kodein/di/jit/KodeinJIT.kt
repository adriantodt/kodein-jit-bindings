package org.kodein.di.jit

import org.kodein.di.DKodein
import org.kodein.di.Kodein
import org.kodein.di.jit.internal.JITContainer

/**
 * Injector that allows to build objects just-in-time.
 *
 * @property kodein The kodein object to use to retrieve injections.
 */
class KodeinJIT internal constructor(private val kodein: DKodein, private val container: JITContainer) {

    /** @suppress */
    fun <T : Any> newInstance(cls: Class<T>) = container.newInstance(kodein, cls)

    /**
     * Creates a new instance of the given type.
     *
     * @param T The type of object to create.
     * @param injectFields Whether to inject the fields & methods of he newly created instance before returning it.
     */
    inline fun <reified T : Any> newInstance() = newInstance(T::class.java)

    companion object {
        /**
         * Utility function that eases the retrieval of a [KodeinJIT].
         */
        @JvmStatic
        fun of(kodein: Kodein) = kodein.jit

        /**
         * Utility function that eases the retrieval of a [KodeinJIT].
         */
        @JvmStatic
        fun of(kodein: DKodein) = kodein.jit
    }
}
