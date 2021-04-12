package de.menkalian.vela.test.unit

import ch.vorburger.mariadb4j.DB
import ch.vorburger.mariadb4j.DBConfigurationBuilder
import com.github.stefanbirkner.systemlambda.SystemLambda
import de.menkalian.vela.determining.DatabaseVersionDeterminer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.sql.DriverManager

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DatabaseVersionDeterminerTest {
    lateinit var dbInstance: DB
    lateinit var versionDeterminer: DatabaseVersionDeterminer

    @BeforeAll
    internal fun createDb() {
        val config = DBConfigurationBuilder.newBuilder()
            .addArg("--user=root")
            .build()

        dbInstance = DB.newEmbeddedDB(config)
        dbInstance.start()
        dbInstance.createDB("vela_versioning")
    }

    @BeforeEach
    internal fun setUp() {
        dbInstance.run("DROP DATABASE vela_versioning; CREATE DATABASE vela_versioning;")

        SystemLambda
            .withEnvironmentVariable("VELA_DB_HOST", "localhost")
            .and("VELA_DB_USER", "root")
            .and("VELA_DB_PORT", dbInstance.configuration.port.toString())
            .execute {
                versionDeterminer = DatabaseVersionDeterminer("de.menkalian.vela.test:versioning-test")
            }
    }

    @Test
    internal fun tableInitializingOnConnection() {
        assertEquals(1, countResults("SHOW TABLES;"))
    }

    @Test
    internal fun canHandleWorking() {
        assertTrue(versionDeterminer.canHandle())

        SystemLambda
            .withEnvironmentVariable("VELA_DB_HOST", "")
            .execute {
                versionDeterminer = DatabaseVersionDeterminer("de.menkalian.vela.test:versioning-test")
            }

        assertFalse(versionDeterminer.canHandle())
        assertEquals(0, versionDeterminer.getBuildNo())
    }

    @Test
    internal fun getBuildNumber() {
        assertEquals(0, versionDeterminer.getBuildNo())

        executeSql("UPDATE vela_version SET version=4;")
        assertEquals(4, versionDeterminer.getBuildNo())
    }

    @Test
    internal fun increaseBuildNumber() {
        assertEquals(0, versionDeterminer.getBuildNo())
        versionDeterminer.increaseBuildNo()

        assertEquals(1, versionDeterminer.getBuildNo())

        executeSql("UPDATE vela_version SET version=4;")
        assertEquals(4, versionDeterminer.getBuildNo())

        versionDeterminer.increaseBuildNo()
        assertEquals(5, versionDeterminer.getBuildNo())
    }

    @AfterAll
    internal fun tearDown() {
        dbInstance.stop()
    }

    private fun executeSql(sql: String) {
        Class.forName("org.mariadb.jdbc.Driver")
        val connection = DriverManager.getConnection("jdbc:mariadb://localhost:${dbInstance.configuration.port}/vela_versioning?user=auser&password=sa")

        val updateStatement = connection.createStatement()
        updateStatement.executeUpdate(sql)
        updateStatement.close()

        connection.commit()
        connection.close()
    }

    private fun countResults(sql: String): Int {
        Class.forName("org.mariadb.jdbc.Driver")
        val connection = DriverManager.getConnection("jdbc:mariadb://localhost:${dbInstance.configuration.port}/vela_versioning?user=auser&password=sa")

        val result = connection.createStatement().executeQuery(sql)
        result.beforeFirst()

        var count = 0
        while (result.next())
            count++
        return count
    }

}