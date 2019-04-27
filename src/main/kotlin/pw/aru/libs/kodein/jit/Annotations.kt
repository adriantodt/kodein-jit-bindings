package pw.aru.libs.kodein.jit

/**
 * Defines that this should be the constructor used to build.
 */
@Target(AnnotationTarget.CONSTRUCTOR)
@Retention(AnnotationRetention.RUNTIME)
annotation class JIT

/**
 * Defines that this class instance should be unique per container.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Singleton