# Kodein JIT Bindings
Just-in-Time Runtime Bindings.

### Adding Dependency
```gradle
repositories {
    jcenter()
}

dependencies {
    compile 'jibril:kodein-jit-bindings:1.0'
}
```

### Using it:
First, import the module to the Kodein:
```kotlin 
val kodein = Kodein {
    import(jitModule)
    /* Other bindings */
}
```

Creating new instances in Kotlin:
```kotlin
val controller = kodein.jit.newInstance<MyJavaController>()
```

Creating new instances in Java:
```java
MyJavaController controller = KodeinJIT.of(kodein).newInstance(MyJavaController.class);
```