# Vela Template Engine

//TODO

Feautres:
 - Platzhalter -> "{{Variable.Name}}" -> "Variable.Wert"
 - Bedingungen -> "{IF (Boolean.Wert) {...} (ELSE {...})?}"
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
        "&IS_LESS" "&IS_EQUAL" "&IS_GREATER" "&IS_LEEQ" "&IS_GREQ"
 - Schleifen:
      {FOR Variable.Name(X) IN Integer.Value TO Integer.Value {...}}
      {WHILE Boolean.Wert {...}}
 - Variablen
     &SET{Variable.Name, Variable.Wert}
     &CLEAR{Variable.Name}
 - Dynamische Ersetzung von XXX Platzhaltern, etc. (Definition von Variablen, die geprüft werden sollen)
     &USE{Variable.Name, String.Platzhalter, String.Format}
 - Definition von Trennzeichen der Variablen
     &ADD_SPACER{String.Wert}
     &REM_SPACER{String.Wert}
 - Escapte Zeichen: "{" = "\{", "\" = "\\", "&" = "\&"
 - Operatoren/keywords nicht Case-Sensitive

REGEX Operatoren:         (?<!\\)&[a-zA-Z_]*
REGEX Platzhalter:        \{\{\S+\}\}
REGEX Escaped Chars:      \\[\\&{]
REGEX Kontrollstrukturen: 
REGEX Argumentlisten:     

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