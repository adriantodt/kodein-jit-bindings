package pw.aru.libs.kodein.jit

import org.kodein.di.DKodein
import org.kodein.di.Kodein
import org.kodein.di.direct
import org.kodein.di.erased
import pw.aru.libs.kodein.jit.internal.LocalJIT

/**
 * Creates a new just-in-time instance of the given class.
 *
 * @param cls The type of object to create.
 */
fun <T : Any> DKodein.jitInstance(cls: Class<T>): T {
    val jit = Instance<LocalJIT>(erased(), null)
    return jit.newInstance(dkodein, cls)
}

/**
 * Creates a new just-in-time instance of the given class.
 *
 * @param T The type of object to create.
 */
inline fun <reified T : Any> DKodein.jitInstance(): T = jitInstance(T::class.java)

/**
 * Creates a new just-in-time instance of the given class.
 *
 * @param cls The type of object to create.
 */
fun <T : Any> Kodein.jitInstance(cls: Class<T>): T = direct.jitInstance(cls)

/**
 * Creates a new just-in-time instance of the given class.
 *
 * @param T The type of object to create.
 */
inline fun <reified T : Any> Kodein.jitInstance(): T = direct.jitInstance()

/**
 * Utility class for Java that allows to build objects just-in-time.
 */
object KodeinJIT {
    /**
     * Creates a new just-in-time instance of the given class.
     *
     * @param cls The type of object to create.
     */
    fun <T : Any> newInstance(kodein: DKodein, cls: Class<T>) = kodein.jitInstance(cls)

    /**
     * Creates a new just-in-time instance of the given class.
     *
     * @param T The type of object to create.
     */
    inline fun <reified T : Any> newInstance(kodein: DKodein) = kodein.jitInstance(T::class.java)
}