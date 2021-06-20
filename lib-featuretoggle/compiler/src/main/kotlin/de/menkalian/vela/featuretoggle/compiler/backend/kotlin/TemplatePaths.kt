@file:Suppress("unused")

package de.menkalian.vela.featuretoggle.compiler.backend.kotlin

internal const val ALL = "all"
internal const val BASE = "base"
internal const val FEATURE = "feature"
internal const val FEAT_FIELD = "feature/field"
internal const val FEAT_OPT = "feature/optional"

// ALL path files
internal const val Features = "Features.kt"
internal const val FeatureConfig = "FeatureConfig.kt"

// BASE path files
internal const val FeatureUtil = "FeatureUtil.kt"
internal const val IBuilder = "IBuilder.kt"
internal const val IDependable = "IDependable.kt"
internal const val IEnableable = "IEnableable.kt"
internal const val IFeature = "IFeature.kt"
internal const val IFeatureRoot = "IFeatureRoot.kt"

// FEATURE path files
internal const val FeatureBuilderClass = "FeatureBuilderClass.kt"
internal const val FeatureClass = "FeatureClass.kt"

// FEAT_FIELD path templates
internal const val ImplementationBuilderFieldTemplate = "ImplementationBuilderField.kt"
internal const val ImplementationInstanceFieldTemplate = "ImplementationInstanceField.kt"
internal const val ParentInstanceFieldTemplate = "ParentInstanceField.kt"
internal const val ParentBuilderFieldTemplate = "ParentBuilderField.kt"
internal const val ParentNullFieldTemplate = "ParentNullField.kt"

// FEAT_OPT path templates
internal const val DisableImplementationsTemplate = "DisableImplementations.kt"
internal const val EnableImplementationsTemplate = "EnableImplementations.kt"
internal const val ImplementationBlockTemplate = "ImplementationBlock.kt"
internal const val ImplementationBuilderBlockTemplate = "ImplementationBuilderBlock.kt"
internal const val ImplementationWhenCaseTemplate = "ImplementationWhenCase.kt"

// additional templates (for ALL and FEAT_FIELD)
internal const val BuilderFieldTemplate = "BuilderField.kt"
internal const val InstanceFieldTemplate = "InstanceField.kt"
