package de.menkalian.vela.test.unit

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import de.menkalian.vela.plain.transformToYaml
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class UtilTest {

    @Test
    fun doesNotTransformValidYAML() {
        val yaml = """
            test:
              value:
                vela: kalix
                aquila: menkalian
                auriga:
              anotherOne:
        """.trimIndent()
        assertEquals(yaml.trim(), transformToYaml(yaml).trim(), "Should not transform valid yaml")
    }

    @Test
    fun doesProduceValidYaml() {
        val input = "vela\n" +
                "  tests\n" +
                "    unit\n" +
                "    integration\n" +
                "    functional\n" +
                "  prod\n" +
                "    plugins\n" +
                "      versioning\n" +
                "      keygen\n"
        assertDoesNotThrow {
            YAMLMapper().readTree(transformToYaml(input))
        }
    }

    @Test
    fun doesNotAlterExistingYamlObject() {
        val mapper = YAMLMapper()
        val initalNode = mapper.createObjectNode()
        initalNode.set<ObjectNode>(
            "vela", mapper.createObjectNode()
                .put("tests", "unit")
        )
        val input = mapper.writeValueAsString(initalNode).trim()
        assertEquals(initalNode, mapper.readTree(transformToYaml(input)), "Should not alter YAML")
    }
}