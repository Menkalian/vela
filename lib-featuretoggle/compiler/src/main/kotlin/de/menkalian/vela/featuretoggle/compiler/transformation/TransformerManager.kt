package de.menkalian.vela.featuretoggle.compiler.transformation

import de.menkalian.vela.featuretoggle.compiler.tree.IFeatureTree

object TransformerManager : ITreeTransformer {
    private val registeredTransformers: MutableList<ITreeTransformer> = mutableListOf()

    init {
        // Register the default transformers
        register(DefaultImplementationTransformer)
        register(DependencyPropagationTransformer)
        register(VisualizationTransformer.Before)
        register(VisualizationTransformer.After)
    }

    override fun getPriority() = Int.MIN_VALUE

    fun register(factory: ITreeTransformer.ITreeTranformerFactory) {
        registeredTransformers.add(factory.create())
    }

    fun register(transformer: ITreeTransformer) {
        registeredTransformers.add(transformer)
    }

    fun deregister(transformer: ITreeTransformer) {
        registeredTransformers.remove(transformer)
    }

    fun clear() {
        registeredTransformers.clear()
    }

    override fun apply(tree: IFeatureTree): Boolean {
        registeredTransformers.sortBy { it.getPriority() }
        return registeredTransformers.all {
            println("Executing Transformer ${it::class.simpleName}")
            it.apply(tree)
        }
    }
}