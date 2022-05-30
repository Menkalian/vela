# tool-epc

This library provides a Tooling-Class called `Epc` (Editable Project Compactor). It has two methods `compress` and `decompress`.

`compress` allows you to provide a directory and have it's information written to a single-file. However, the files are **not** reduced in size. If anything, the size will increase. Thus, the target of this tool is **not** to save some storage. It differentiates between *text* and *binary* files. Some basic extensions are already assigned, but you can modify the extensions and the default mode at initialization

It's main goal is to provide **easy editable**, **reusable** and **transportable** test-projects for gradle-plugins (I know `tar`-files are kinda similar, but I believe this format is easier to edit).
