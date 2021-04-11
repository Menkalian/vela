package de.menkalian.vela.test.unit

import de.menkalian.vela.determining.PropFileVersionDeterminer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import java.io.File

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class PropFileVersionDeterminerTest {
    private val propFile = File("versioning.properties")
    private val versionDeterminer = PropFileVersionDeterminer(File("."))

    @BeforeEach
    internal fun setUp() {
        propFile.writeText("")
    }

    @AfterEach
    internal fun tearDown() {
        propFile.delete()
    }

    @Test
    @Order(1)
    internal fun canHandleWorking() {
        assertTrue(versionDeterminer.canHandle())

        propFile.delete()
        assertFalse(versionDeterminer.canHandle())
        assertEquals(0, versionDeterminer.getBuildNo())
    }

    @Test
    @Order(2)
    internal fun getBuildNumberWorking() {
        assertEquals(0, versionDeterminer.getBuildNo())

        propFile.writeText("BUILD_NO=4")
        assertEquals(4, versionDeterminer.getBuildNo())
    }

    @Test
    @Order(3)
    internal fun increaseBuildNumber() {
        assertEquals(0, versionDeterminer.getBuildNo())
        versionDeterminer.increaseBuildNo()
        assertEquals(1, versionDeterminer.getBuildNo())

        propFile.writeText("BUILD_NO=4")
        assertEquals(4, versionDeterminer.getBuildNo())
        versionDeterminer.increaseBuildNo()
        assertEquals(5, versionDeterminer.getBuildNo())
    }
}