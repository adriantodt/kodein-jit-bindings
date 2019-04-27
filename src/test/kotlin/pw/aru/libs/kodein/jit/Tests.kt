package pw.aru.libs.kodein.jit

import io.kotlintest.matchers.instanceOf
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import org.kodein.di.Kodein
import org.kodein.di.direct
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance

class A

class B0(dep: B1)
class B1

@Singleton
class C

class D0(dep: D1)
class D1(dep: D2)
class D2(dep: D3)
class D3(dep: D4)
class D4

class E0(dep: D1)
@Singleton
class E1(dep: D2)

@Singleton
class E2(dep: D3)

@Singleton
class E3(dep: D4)

@Singleton
class E4

class F0(val dep: F)
@Singleton
class F

class GLeft(dep: GRight)
class GRight(dep: GLeft)

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

        kodein.jitInstance<B0>() should instanceOf(B0::class)
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

        kodein.direct.instance<B0>() should instanceOf(B0::class)
    }

    "Singletons" {
        val kodein = Kodein {
            installJit()
        }

        kodein.jitInstance<C>() shouldBe kodein.jitInstance()
    }

    "Singletons across modules" {
        val base = Kodein {
            installJit()
        }

        val kodein = Kodein {
            extend(base)
        }

        base.jitInstance<C>() shouldBe kodein.jitInstance()
    }

    "Recursivity" {
        val kodein = Kodein {
            installJit()
        }

        kodein.jitInstance<D0>()

        kodein.jitInstance<E0>()

        val f1 = kodein.jitInstance<F0>()
        val f2 = kodein.jitInstance<F0>()
        f1 shouldNotBe f2
        f1.dep shouldBe f2.dep
    }

    "Recursivity Errors" {
        val kodein = Kodein {
            installJit()
        }

        shouldThrow<Kodein.DependencyLoopException> {
            kodein.jitInstance<GLeft>()
        }
    }
})