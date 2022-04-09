# lib-transfervalue

This library provides a type called `TransferableValue`. This class can be used to place values of different native types in a common datastructure (like a `List<TransferableValue>` or a `Map<String, TransferableValue>`).

## Usage

To create a TransferableValue of a given type, you should use `TransferableValue.from`. It is **NOT** recommended using the constructor directly. To get the native value back, you may use the conversion methods like `toBoolean` or `toInt`.

## Supported Datatypes

Currently the following datatypes and encodings are used (the encoding for a given type will stay compatible, until a new major release is published):

| Name    | native type (Kotlin) | native type (Java) | encoding as string              |
|---------|----------------------|--------------------|---------------------------------|
| BOOLEAN | `Boolean`            | `boolean`          | litteral value ("true"/"false") |
| STRING  | `String`             | `String`           | plain value                     |
| BINARY  | `ByteArray`          | `byte[]`           | Hexadecimal String value        |
| INTEGER | `Int`                | `int`              | decimal string value            |
| LONG    | `Long`               | `long`             | decimal string value            |
| DOUBLE  | `Double`             | `double`           | decimal string value            |

## Conversions

When using an conversion method which does not match the type, the following conversions are attempted to return a value either way:

| from/to | BOOLEAN                | STRING     | BINARY                                                        | INTEGER                                             | LONG                                                | DOUBLE                                                   |
|---------|------------------------|------------|---------------------------------------------------------------|-----------------------------------------------------|-----------------------------------------------------|----------------------------------------------------------|
| BOOLEAN | -                      | as encoded | single element array with value 1 for true, value 0 otherwise | true = 1, false = 0                                 | true = 1, false = 0                                 | true = 1.0, false = 0.0                                  |
| STRING  | value == "true"        | -          | text encoded as utf-8 bytes                                   | integer parsed                                      | long parsed                                         | double parsed                                            |
| BINARY  | any value != 0         | as encoded | -                                                             | value of the first 4 bytes of the array (MSB first) | value of the first 8 bytes of the array (MSB first) | interpret bytes as specified by IEEE 754 "double format" |
| INTEGER | value != 0             | as encoded | single bytes of the value (MSB first)                         | -                                                   | value casted                                        | value casted                                             |
| LONG    | value != 0             | as encoded | single bytes of the value (MSB first)                         | value casted (may drop data)                        | -                                                   | value casted                                             |
| DOUBLE  | value != 0 and not NaN | as encoded | bytes as specified by IEEE 754 (MSB first)                    | value casted (rounded)                              | value casted (rounded)                              | -                                                        |