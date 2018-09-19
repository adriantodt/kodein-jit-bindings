package pw.aru.kodein.jit

import io.kotlintest.matchers.instanceOf
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import org.kodein.di.Kodein
import org.kodein.di.direct
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance

class A

class B(val a: A)

@Singleton
class C

class Tests : StringSpec({
    "Create Kodein" {
        Kodein {
            installJit()
        }
    }

    "Simple JIT" {
        val kodein = Kodein {
            installJit()
        }

        kodein.jitInstance<A>() should instanceOf(A::class)
    }

    "JIT with deps" {
        val kodein = Kodein {
            installJit()
            bind<A>() with instance(A())
        }

        kodein.jitInstance<B>() should instanceOf(B::class)
    }

    "Discrete JIT" {
        val kodein = Kodein {
            installJit()
        }

        kodein.direct.instance<A>() should instanceOf(A::class)
    }

    "Discrete JIT with deps" {
        val kodein = Kodein {
            installJit()
            bind<A>() with instance(A())
        }

        kodein.direct.instance<B>() should instanceOf(B::class)
    }

    "Singletons" {
        val kodein = Kodein {
            installJit()
        }

        kodein.jitInstance<C>() shouldBe kodein.jitInstance()
    }

    "recursive JIT error" {
        val kodein = Kodein {
            installJit()
        }

        shouldThrow<Kodein.DependencyLoopException> {
            kodein.jitInstance<B>()
        }
    }

})