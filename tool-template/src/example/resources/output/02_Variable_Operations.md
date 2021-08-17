# Basis Variable Operations

Vela templates support the use of variables.
There are some defined constants, e.g. "Engine.Name"={Vela}.

You may also set your own variables like this: 
Now you can just insert the value everywhere, just like this: This is your own variable
If you try to access a non-existent variable, it will just be blank: 

There are also operators for checking if a variable exists.
Variable exists: true
Delete Variable... 
Variable exists: false

If your variable has a numeric value, you can increase or decrease it. 
5
`INCREMENT` 
6
`DECREMENT` 
5

You can also use the ref operator to access the value of the variable that is saved in another variable (similar to pointers):

Value: Numeric.Value
Referenced: 5

It might also be handy, that you can check if a variable contains a numeric or a boolean value.
true
true
true
false
false

The following examples will show you how to work with these variables.


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


