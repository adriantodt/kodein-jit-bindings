package pw.aru.kodein.jit

import org.kodein.di.DKodein
import org.kodein.di.Kodein
import pw.aru.kodein.jit.internal.JITContainer

/**
 * Injector that allows to build objects just-in-time.
 *
 * @property kodein The kodein object to use to retrieve injections.
 */
class KodeinJIT internal constructor(private val kodein: DKodein, private val container: JITContainer) {

    /**
     * Creates a new instance of the given class.
     *
     * @param cls The type of object to create.
     */
    fun <T : Any> newInstance(cls: Class<T>) = container.newInstance(kodein, cls)

    /**
     * Creates a new instance of the given type.
     *
     * @param T The type of object to create.
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
