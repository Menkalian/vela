package de.menkalian.vela.featuretoggle.compiler.parser

import de.menkalian.vela.featuretoggle.compiler.tree.INode

class NoParserImplementationException(extension: String) : Exception("No valid parser is implemented for $extension-files")

class SyntaxInvalidException(msg: String, reason: Throwable? = null) : Exception(msg, reason)

class DependencyResolutionFailedException(unsatisfied: INode.IDependencyNode, depending: INode.IDependingNode)
    : Exception("Dependency resolution failed: Could not find Dependency '${unsatisfied.name}'. Required for node '${depending.absoluteName}'")