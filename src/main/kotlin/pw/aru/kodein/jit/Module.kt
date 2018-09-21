package pw.aru.kodein.jit

import org.kodein.di.Kodein
import org.kodein.di.bindings.ExternalSource
import org.kodein.di.bindings.NoScope
import org.kodein.di.bindings.Singleton
import org.kodein.di.bindings.externalFactory
import org.kodein.di.erased
import org.kodein.di.jvmType
import pw.aru.kodein.jit.internal.LocalJIT

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
val jitModule = Kodein.Module("KodeinJIT Module") {
    Bind() from Singleton(NoScope(), erased(), erased()) { LocalJIT() }
}

/**
 * Integration with Kodein to create dependencies at runtime.
 */
val jitIntegration = ExternalSource { key ->
    externalFactory { jitInstance(key.type.jvmType.rawType()) }
}
