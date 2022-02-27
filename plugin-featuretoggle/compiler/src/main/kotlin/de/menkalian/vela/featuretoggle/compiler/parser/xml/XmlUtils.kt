package de.menkalian.vela.featuretoggle.compiler.parser.xml

import org.w3c.dom.Node
import org.w3c.dom.NodeList

// Constants
const val XML_NAMESPACE_URI = "https://schemas.menkalian.de/vela/feature"

const val XML_ROOT_ELEMENT_NAME = "features"
const val XML_FEATURE_SET_ELEMENT_NAME = "set"
const val XML_FEATURE_ELEMENT_NAME = "feature"
const val XML_IMPLEMENTATION_ELEMENT_NAME = "impl"
const val XML_DEPENDENCY_ELEMENT_NAME = "dependency"

const val XML_NAME_ATTR_NAME = "name"
const val XML_IS_DEFAULT_ATTR_NAME = "default"

// Extension Functions

/**
 * forEach Extension of a NodeList, since it is not implemented as iterable
 */
fun NodeList.forEach(action: (Node) -> Unit) {
    for(i in 0 until length) {
        action(item(i))
    }
}

/**
 * map Extension of a NodeList, since it is not implemented as iterable
 */
fun<R> NodeList.map(mapper: (Node) -> R): List<R> {
    val toReturn = mutableListOf<R>()
    forEach {
        toReturn.add(mapper(it))
    }
    return toReturn
}
