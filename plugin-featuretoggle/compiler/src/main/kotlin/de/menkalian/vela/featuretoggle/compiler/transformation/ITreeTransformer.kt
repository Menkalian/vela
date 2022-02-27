package de.menkalian.vela.featuretoggle.compiler.transformation

import de.menkalian.vela.featuretoggle.compiler.tree.IFeatureTree

/**
 * Interface for objects that manipulate the ast-object to perform actions.
 * Possible actions include for example optimizing or normalizing the tree
 */
fun interface ITreeTransformer {
    /**
     * Gives the priority of the Transformers to execute.
     * (Used by the TransformerManager)
     *
     * The lower this number is, the earlier it is executed.
     * If two transformers have the same priority no order is guaranteed.
     *
     * Predefined transformers have priorities in the range of 1000..10000.
     */
    fun getPriority(): Int = Int.MAX_VALUE

    /**
     * Applies the transformation to the given tree.
     *
     * @param tree The ast-object to manipulate
     * @return `true` if the transformation was successful. `false` otherwise
     */
    fun apply(tree: IFeatureTree): Boolean

    /**
     * Interface for objects that allow the dynamic creation of `TreeTransformer` implementations
     */
    fun interface ITreeTranformerFactory {

        /**
         * Creates a new instance of a `ITreeTransformer` or provides an previously created instance
         *
         * @return the created instance
         */
        fun create(): ITreeTransformer
    }
}