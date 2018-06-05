package pw.aru.kodein.jit

import org.kodein.di.*
import org.kodein.di.bindings.ExternalSource
import org.kodein.di.bindings.NoScope
import org.kodein.di.bindings.Singleton
import org.kodein.di.bindings.externalFactory
import pw.aru.kodein.jit.internal.JITContainer

/**
 * Install [KodeinJIT] module and integration.
 */
fun Kodein.MainBuilder.installJit() {
    import(jitModule)
    externalSource = jitIntegration
}

/**
 * Module that must be imported in order to use [KodeinJIT].
 */
val jitModule = Kodein.Module {
    Bind() from Singleton(NoScope(), erased(), erased()) { JITContainer() }
}

/**
 * Integration with Kodein to create dependencies at runtime.
 */
val jitIntegration = ExternalSource { key ->
    externalFactory { jit.newInstance(key.type.jvmType.rawType()) }
}

/**
 * Utility function to retrieve a [KodeinJIT].
 */
val Kodein.jit: KodeinJIT get() = KodeinJIT(direct, direct.Instance(erased(), null))

/**
 * Utility function to retrieve a [KodeinJIT].
 */
val DKodein.jit: KodeinJIT get() = KodeinJIT(this, Instance(erased(), null))

/**
 * Defines that this should be the constructor used to build.
 */
@Target(AnnotationTarget.CONSTRUCTOR)
@Retention(AnnotationRetention.RUNTIME)
annotation class JIT