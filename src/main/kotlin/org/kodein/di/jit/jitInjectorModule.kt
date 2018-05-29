package org.kodein.di.jit

import org.kodein.di.DKodein
import org.kodein.di.Kodein
import org.kodein.di.bindings.NoScope
import org.kodein.di.bindings.Singleton
import org.kodein.di.direct
import org.kodein.di.erased
import org.kodein.di.jit.internal.JITContainer

/**
 * Module that must be imported in order to use [KodeinJIT].
 */
val jitModule = Kodein.Module {
    Bind() from Singleton(NoScope(), erased(), erased()) { JITContainer() }
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