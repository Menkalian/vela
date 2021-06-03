package de.menkalian.vela.featuretoggle.compiler.parser.xml

import de.menkalian.vela.featuretoggle.compiler.tree.DefaultFeatureNode
import de.menkalian.vela.featuretoggle.compiler.tree.DefaultFeatureSetNode
import de.menkalian.vela.featuretoggle.compiler.tree.IFeatureTree
import org.junit.jupiter.api.Test
import java.io.InputStream

internal class XmlParserTest {

    @Test
    fun parse() {
        val xmlParser = XmlParser.createInstance(
            XmlParserTest::class.java.classLoader
                .getResourceAsStream("test_parse.xml") ?: InputStream.nullInputStream()
        )

        val parse = xmlParser.parse()
        assertTreeEquality(getExpectedParseResult(), parse)
    }

    /**
     * Hardcoded Representation of the expected ParsingResult in test_parse.xml
     */
    private fun getExpectedParseResult(): IFeatureTree {
        val rootFeatureSetNode = DefaultFeatureSetNode("", null)
        val versioningNode = rootFeatureSetNode.addFeature("Versioning", true)

        val velaSetNode = rootFeatureSetNode
            .addSet("de")
            .addSet("menkalian")
            .addSet("vela")

        val gradleSetNode = velaSetNode.addSet("gradle")

        val buildconfigGradlePlugNode = gradleSetNode
            .addFeature("BuildconfigGradlePlugin", true)
        val buildconfigImplNode = gradleSetNode
            .addFeature("BuildconfigImplementation")
            .addDependency(versioningNode)

        val databaseVersionDeterNode = (gradleSetNode
            .addFeature("DatabaseVersionDeterminer")
            .addDependency(buildconfigGradlePlugNode)
            .addDependency(buildconfigImplNode)
            .addImplementation("VERSION_1_0")
            .addDependency(buildconfigGradlePlugNode)
            .parent as DefaultFeatureNode)
            .addImplementation("VERSION_2_1")
            .parent

        (velaSetNode
            .addFeature("Keygen")
            .addImplementation("JAVA", true)
            .parent as DefaultFeatureNode)
            .addImplementation("KOTLIN")
            .addDependency(databaseVersionDeterNode)

        return IFeatureTree.create(rootFeatureSetNode, IFeatureTree.Version.VERSION_1)
    }
}