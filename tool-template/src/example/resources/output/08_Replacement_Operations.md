# Automatic replacement of variables

As seen in the previous section, if a variable contains the character `XXX` separated by `.` from the rest of the variable, it is automatically replaced by the current value of `X` formatted as three digit number.

You can modify the available separators by using `ADD_SPACER` or `REMOVE_SPACER`.
You can also define your own replacement rules by using the `USE`-operator.

It takes three arguments: The expression to be replaced (e.g. "XXX"), the action to perform for replacement (e.g. "" to access the value of X) and the format to use for inserting the replacement (according to format-strings used by `String.format` (Java) or `printf` (C[++]).

Here is an example: 
`1`,
`2`


---
#### Overview over all examples:
 - [First example](01_Simple_Template.md)
 - [Basic usage of variables](02_Variable_Operations.md)
 - [Inclusion of files](03_include_files.md)
 - [Logic operations with variables](04_Logic_Operators.md)
 - [Comparison between variables and values](05_Comparisons.md)
 - [Conditional execution](06_Conditions.md)
 - [Repetition and loops](07_Loops.md)
 - [Automatic replacement in variable names](08_Replacement_Operations.md)
 - [Simulating a turing machine with templates (not intended usage, but at least it's possible)](10_Turing_Simulation.md)


