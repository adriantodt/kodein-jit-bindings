# KodeinJIT
Just-in-Time Runtime Bindings.

**Warning**: The new v2.0 had a lot of breaking changes.
Be sure to correctly migrate to it by checking the end of this document.

### Adding Dependency
```gradle
repositories {
    jcenter()
}

dependencies {
    compile 'org.kodein.di:kodein-di-generic-jvm:5.2.0'
    compile 'jibril:kodein-jit-bindings:2.0'
}
```

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
- **`NEW`** **@JIT** 