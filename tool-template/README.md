# Vela Template Engine

//TODO

Feautres:
 - Platzhalter -> "{{Variable.Name}}" -> "Variable.Wert"
 - Bedingungen -> "&IF Boolean.Wert {...}"
                  "&ELSE {...}"
 - Allgemeine Operatoren auf Variablen:
        "&DEFINED{Variable.Name}" (Boolean ob definiert)
        "&REF{Variable.Name}" (Inhalt der Variable wird als eigener Name ausgewertet)
        "&NOT{Boolean.Wert}" (Invertierung eines Booleschen Wertes [Alles was kein bool ist, wird als 0/false or else true ausgewertet])
        "&AND{Boolean.Wert, Boolean.Wert}" (Konjunktion eines Booleschen Wertes [Alles was kein bool ist, wird als 0/false or else true ausgewertet])
        "&OR{Boolean.Wert, Boolean.Wert}" (Disjuntion zweier Boolescher Werte [Alles was kein bool ist, wird als 0/false or else true ausgewertet])
        "&XOR{Boolean.Wert, Boolean.Wert}" (XOR zwei Booleschen Werte [Alles was kein bool ist, wird als 0/false or else true ausgewertet])
        "&IS_BOOL{Variable.Name}" (Ob Wert als valider Boolean Wert gesehen wird)
        "&IS_NUMERIC{Variable.Name}" (Ob Wert numerisch (Int or float) ist)
        "&ADD{Variable.Name, Value}" (Addiert/Konkateniert zu besehendem Wert)
        "&CAT{Variable.Name, Wert}" (Konkateniert zu Bestehendem Wert)
 - Schleifen
 - Variablen
 - Definitionen im Code
 - Dynamische Ersetzung von XXX Platzhaltern, etc. (Definition von Variablen, die geprüft werden sollen)
 - Dynamische Defitiontion von Ersetzungsroutinen
 - Definition von Trennzeichen der Variablen
 - Escapte Zeichen: "{" = "\{", "\" = "\\", "&" = "\&"
 - Operatoren/keywords nicht Case-Sensitive
---
Gradle:

```kotlin
dependencies {
    implementation("de.menkalian.vela:tool-template:1.0.0")
}
```

Maven:

```xml

<dependency>
    <groupId>de.menkalian.vela</groupId>
    <artifactId>tool-template</artifactId>
    <version>1.0.0</version>
</dependency>
```