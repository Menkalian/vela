package de.menkalian.vela.test.integration

import ch.vorburger.mariadb4j.DB
import ch.vorburger.mariadb4j.DBConfigurationBuilder
import com.github.stefanbirkner.systemlambda.SystemLambda
import de.menkalian.vela.determining.DatabaseVersionDeterminer
import de.menkalian.vela.determining.PropFileVersionDeterminer
import de.menkalian.vela.determining.VersionDeterminer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VersionDeterminerTest {
    private lateinit var dbInstance: DB
    private lateinit var determiner: VersionDeterminer

    @BeforeAll
    internal fun setAllUp() {
        val config = DBConfigurationBuilder.newBuilder()
            .setPort(3306)
            .build()

        dbInstance = DB.newEmbeddedDB(config)
        dbInstance.start()
        dbInstance.createDB("VELA_VERSIONING")
    }

    @Test
    internal fun testUsingDbDeterminer() {
        setDbBuildNo(42)

        // Without versioning.properies and with Env-Variable the DB is used
        assertFalse(File("versioning.properties").exists())
        SystemLambda
            .withEnvironmentVariable("VELA_DB_HOST", "localhost")
            .execute {
                determiner = newVersionDeterminer()

                assertEquals(42, determiner.getBuildNo())
                determiner.increaseBuildNo()
                assertEquals(43, determiner.getBuildNo())
            }

        // With both file and env-variable the db has priority
        setDbBuildNo(10)
        setPropFileBuildNo(71)
        SystemLambda
            .withEnvironmentVariable("VELA_DB_HOST", "localhost")
            .execute {
                determiner = newVersionDeterminer()

                assertEquals(10, determiner.getBuildNo())
                determiner.increaseBuildNo()
                assertEquals(11, determiner.getBuildNo())
            }
    }

    @Test
    internal fun testUsingPropFileDeterminer() {
        setPropFileBuildNo(18)
        setDbBuildNo(14)

        // Without Env and with versioning.properties the properties file is used
        determiner = newVersionDeterminer()

        assertEquals(18, determiner.getBuildNo())
        determiner.increaseBuildNo()
        assertEquals(19, determiner.getBuildNo())
    }

    @Test
    internal fun testUsingDefault() {
        // Neither versioning.properties nor VELA_DB_HOST exist
        determiner = newVersionDeterminer()

        assertEquals(0, determiner.getBuildNo())
        determiner.increaseBuildNo()
        assertEquals(0, determiner.getBuildNo())
    }


    @AfterEach
    internal fun tearDown() {
        File("versioning.properties").delete()
    }

    @AfterAll
    internal fun tearAllDown() {
        dbInstance.stop()
    }

    private fun newVersionDeterminer() =
        DatabaseVersionDeterminer("de.menkalian.vela.test:integration-test", PropFileVersionDeterminer(File(".")))

    private fun setDbBuildNo(buildNo: Int) {
        dbInstance.run(
            """
                USE vela_versioning;
                CREATE TABLE IF NOT EXISTS vela_version (
                    project VARCHAR(255),
                    version INT,
                    PRIMARY KEY (project)
                );
                DELETE FROM vela_version WHERE project='de.menkalian.vela.test:integration-test';
                INSERT INTO vela_version (project, version)
                    VALUES ('de.menkalian.vela.test:integration-test', $buildNo)
            """.trimIndent()
        )
    }

    private fun setPropFileBuildNo(buildNo: Int) {
        File("versioning.properties").writeText("BUILD_NO=$buildNo")
    }
}