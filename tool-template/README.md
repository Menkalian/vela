# tool-template

This is a template engine with turing completeness. It is built primarily to generate source code, but it may be used in other applications where a text template has to be filled. The design of the syntax assumes empty lines are not important for the resulting text. If they are, the resulting code might not be very well legible.

**Currently the implementation is not suitable for multithreaded access, therefore only one template can be evaluated at a given point in time. This should be fixed as soon as possible**

---
To use this in your project include the following artifacty in your project:

<details>
<summary>Gradle:</summary>
```kotlin
dependencies {
    implementation("de.menkalian.vela:tool-template:1.0.0")
}
```
</details>
<details>
<summary>Maven:</summary>
```xml
<dependency>
    <groupId>de.menkalian.vela</groupId>
    <artifactId>tool-template</artifactId>
    <version>1.0.0</version>
</dependency>
```
</details>

## Evaluating Templates

Templates may be written in files with any name. It is, however, recommended to name the source files something like `user.eml.vtp`, to be able to distinguish between templates and evaluated files. The abbreviation `vtp` denotes `vela template`.

To retrieve an instance of the template evaluator you can do so like this:

````kotlin
import de.menkalian.vela.template.evaluator.ITemplateParser

val parser = ITemplateParser.getDefaultImplementation()
````

To evaluate a template use the following call:

````kotlin
val evaluated = parser.parse("generic.eml.vtp").evaluate()

val evaluatedWithVariables = parser.parse("user.eml.vtp").evaluate(
    mapOf(
        "User.Name" to "Max Mustermann",
        "User.Email" to "max.mustermann@example.org"
    )
)
````

## Writing templates

Templates are basically text files, which contain additional tokens which are evaluated. This is a guide to show the features provided by the vela template engine. You may also look at the [examples](src/example/resources) and their [output](src/example/resources/output) to see how these templates can be used.

### Variable inserts

To put the value of a variable at a certain position in text you can use the following:

````text
Hello {{User.Name}} and welcome to the Aperture Science Enrichment Center.
````

### Type handling

Internally all variables are saved as strings. Depending on their usage, they may occasionally also be interpreted as `boolean` or `numeric`.

When a variable is interpreted as numeric, it is parsed as a **decimal** number. If that fails, the value is treated as `0`.

For booleans the values *"0"*, *"false"* and blank strings are interpreted as `false` values. Any other value is treated as true.

### Escaping protected symbols

Certain symbols are part of the syntax and have to be escaped. This is done by prepending a '\'. E.g. to produce the text "{Curly braces are funny :-}" your template would look like this: `\{Curly braces are funny :-\}`.

These symbols have to be escaped: `{`, `}`, `\`, `&`.

### Predefined variables

The following variables are **always** defined when a script starts executing. It is possible, although NOT recommended, to change or delete these variables.

| name                      | description                                                | example/default                                                   |
|---------------------------|------------------------------------------------------------|-------------------------------------------------------------------|
| `Const.True`              | Constant value for a `true` boolean                        | "true"                                                            |
| `Const.False`             | Constant value for a `false` boolean                       | "false"                                                           |
|                           |                                                            |                                                                   |
| `Const.Zero`              | Constant value for a numeric with value `0`                | "0"                                                               |
| `Const.One`               | Constant value for a numeric with value `1`                | "1"                                                               |
| `Const.Two`               | Constant value for a numeric with value `2`                | "2"                                                               |
| `Const.Ten`               | Constant value for a numeric with value `10`               | "10"                                                              |
|                           |                                                            |                                                                   |
| `Engine.Version`          | Version of the engine used to evaluate the template        | "2.0.0"                                                           |
| `Engine.Language.Version` | Language version (supported features, etc.) of the engine  | "1.0"                                                             |
| `Engine.Name`             | Name of the engine implementation                          | "de.menkalian.vela"                                               |
| `Engine.Hint`             | Short text, which can be used as a hint in generated files | "##### Created with Vela Engine (de.menkalian.vela) V2.0.0 #####" |

### Control structures

There are currently 3 control structures for the templates: `while`-Loops, `for`-Loops and `if`-Conditions. These behave as they would in any other language.

#### WHILE

Repeats as long as the condition evaluates to the boolean value `true`.

````text
{WHILE {*CONDITION*} {
    *EXPRESSION*
}}
````

#### FOR

Iterates over a range of numeric values.

````text
{FOR {*VARIABLE*} IN {*START*} TO {*FINISH*} {
    *EXPRESSION*
}}
````

#### IF-ELSE

Executes the `if`-Branch, if the condition is true, otherwise the `else`-Branch is executed. The `else`-Branch may be ommitted (it is treated as an empty Text in that case)

````text
{IF {*CONDITION*} {
    *EXPRESSION*
} ELSE {
    *EXPRESSION*
}}
````

### Operators

There are several operators defined. Each operator is prefixed by `&`. Arguments are given afterwards, each wrapped in curly braces (`{ARG1}{ARG2}`).

The following operators are defined:

| Name          | return type | description                                                                                                                           | argument type 1   | argument type 2 | argument type 3 |
|---------------|-------------|---------------------------------------------------------------------------------------------------------------------------------------|-------------------|-----------------|-----------------|
| NOT           | `boolean`   | Inverts the value of the argument                                                                                                     | `boolean`         | -               | -               |
| AND           | `boolean`   | Returns `true` if both arguments are true (using fast evaluation)                                                                     | `boolean`         | `boolean`       | -               |
| OR            | `boolean`   | Returns `true` if any of the arguments are true (using fast evaluation)                                                               | `boolean`         | `boolean`       | -               |
| XOR           | `boolean`   | Returns `true` if exactly one argument is true                                                                                        | `boolean`         | `boolean`       | -               |
|               |             |                                                                                                                                       |                   |                 |                 |
| OFF           | `text`      | Returns a blank string and ignores everything written inside. Can be used to comment on templates.                                    | `text`            | -               | -               |
| REF           | `text`      | Shorthand for `{{{{Reference.Name}}}}` (the content of the variable is treated as a variable name itself)                             | `text` (var name) | -               | -               |
| DEFINED       | `boolean`   | Checks whether the given variable exists. Returns also true if the variable value is blank.                                           | `text` (var name) | -               | -               |
| SET           | -           | Sets the variable to the given value                                                                                                  | `text` (var name) | `text` (value)  | -               |
| CLEAR         | -           | Deletes the variable.                                                                                                                 | `text` (var name) | -               | -               |
|               |             |                                                                                                                                       |                   |                 |                 |
| INC           | `numeric`   | Increments the given variable.                                                                                                        | `text` (var name) | -               | -               |
| DEC           | `numeric`   | Decrements the given variable.                                                                                                        | `text` (var name) | -               | -               |
|               |             |                                                                                                                                       |                   |                 |                 |
| IS_BOOL       | `boolean`   | Checks whether the given variable is a valid boolean value ("1", "0", "true", "false")                                                | `text` (var name) | -               | -               |
| IS_NUMERIC    | `boolean`   | Checks whether the given variable is a valid numeric value                                                                            | `text` (var name) | -               | -               |
|               |             |                                                                                                                                       |                   |                 |                 |
| IS_LESS       | `boolean`   | Compares the values (lexiographical order for strings, natural for numeric) and returns whether arg1 is less than arg2                | `text` (value)    | `text` (value)  | -               |
| IS_LEEQ       | `boolean`   | Compares the values (lexiographical order for strings, natural for numeric) and returns whether arg1 is less than or equal to arg2    | `text` (value)    | `text` (value)  | -               |
| IS_EQUAL      | `boolean`   | Compares the values (lexiographical order for strings, natural for numeric) and returns whether arg1 is equal to arg2                 | `text` (value)    | `text` (value)  | -               |
| IS_GREQ       | `boolean`   | Compares the values (lexiographical order for strings, natural for numeric) and returns whether arg1 is greater than or equal to arg2 | `text` (value)    | `text` (value)  | -               |
| IS_GREATER    | `boolean`   | Compares the values (lexiographical order for strings, natural for numeric) and returns whether arg1 is greater than arg2             | `text` (value)    | `text` (value)  | -               |
|               |             |                                                                                                                                       |                   |                 |                 |
| USE           | -           | Adds a rule for the *DRAcoEval*. This advanced mechanism is explained in a later section.                                             | `text` (value)    | `expression`    | `text` (format) |
| ADD_SPACER    | -           | Adds a spacer for *DRAcoEval*. This advanced mechanism is explained in a later section.                                               |                   |                 |                 |
| REMOVE_SPACER | -           | Removes a spacer for *DRAcoEval*. This advanced mechanism is explained in a later section.                                            |                   |                 |                 |

### Dynamic Replacement Algorithm for convenient Evaluation (*DRAcoEval*)

<!-- TODO -->
