package pw.aru.kodein.jit.internal

import org.kodein.di.DKodein
import pw.aru.kodein.jit.Singleton
import java.util.*

@Suppress("UNCHECKED_CAST")
internal class LocalJIT {
    private val singletons = WeakHashMap<Class<*>, Any>()

    private fun <T> getOrCreateSingleton(kodein: DKodein, cls: Class<T>): T {
        return singletons.getOrPut(cls) {
            JIT.instantiate(kodein, cls)
        } as T
    }

    fun <T : Any> newInstance(kodein: DKodein, cls: Class<T>): T {
        if (cls.isAnnotationPresent(Singleton::class.java)) {
            return getOrCreateSingleton(kodein, cls)
        }

        return JIT.instantiate(kodein, cls)
    }
}