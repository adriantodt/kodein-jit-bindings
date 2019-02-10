# KodeinJIT
Just-in-Time Runtime Bindings.

This library was based on Kodein-jxInject but specialized for actual runtime instantiation instead of complying with javax.inject.

**Warning**: The version v2.0 had a lot of breaking changes.
Be sure to correctly migrate to it by checking the end of this document.

### Adding Dependency
```gradle
repositories {
    jcenter()
}

dependencies {
    compile 'org.kodein.di:kodein-di-generic-jvm:6.1.0'
    compile 'pw.aru.kt:kodein-jit-bindings:X.Y.Z'
}
```

Latest Version:

![Latest Version](https://api.bintray.com/packages/adriantodt/maven/kodein-jit-bindings/images/download.svg)

### Using it:
First, import the module to the Kodein:
```kotlin 
val kodein = Kodein {
    installJit()
    /* Other bindings */
}
```

Creating new instances in Kotlin:
```kotlin
val controller = kodein.jitInstance<MyJavaController>()
```

Creating new instances in Java:
```java
MyJavaController controller = KodeinJIT.newInstance(kodein, MyJavaController.class);
```

## KodeinJIT 2.0

- KodeinJIT got a full rewrite, in order to fix circular dependencies.
- You can make KodeinJIT recursively instantiate just-in-time all the required dependencies.
- **`NEW`** Multiple constructors? Annotate the right one with the **@JIT** annotation to make sure it's the right one!
- **`NEW`** Annotate your class with the **@Singleton** annotation to make the JIT instance a singleton!
  - **Note**: A singleton-annotated class trying to instantiate another singleton-annotated class will throw a circular dependency exception, because... uh... Kodein.
