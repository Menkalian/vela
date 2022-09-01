# lib-log

Simple logger library for the JVM built with Kotlin. For most people [https://github.com/qos-ch/slf4j](SLF4J) (and it's implementation) will probably be the right choice.

Some things this library provides are:

- Easy setup of well-defined loggers (instead of having one Log-Label per class)
- Built-in easy support for multiple log outputs
- Programmatic control of the configuration
- Usage of custom tags for formatting. These can be implemented by you
- Built-in support for gathering logfiles, that compile filtered information for other logfiles in one place
- multithreading support

## Custom formatter tags provided by the library

| Placeholder | meaning                                        | example input          | example output                      |
|-------------|------------------------------------------------|------------------------|-------------------------------------|
| %q %lq      | Insert the quoted string value of the argument | "Vela"                 | "\"Vela\""                          |
| %lx         | Insert the stacktrace of a Throwable           | new RuntimeException() | "java.lang.RuntimeException at ..." |

## Example usage

````kotlin
fun main() {
    LoggerFactory.getInstance().configure {
        addColoredStdoutLogWriter()
    }
    val log = LoggerFactory.getInstance().getLogger("DEFAULT")
    log.info("Hello Logger!")
}
````