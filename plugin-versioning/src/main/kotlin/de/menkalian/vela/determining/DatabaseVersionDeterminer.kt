package de.menkalian.vela.determining

import java.sql.Connection
import java.sql.DriverManager

class DatabaseVersionDeterminer(val projName: String, successor: VersionDeterminer? = null) : VersionDeterminer(successor) {

    private val dbHost = System.getenv().getOrDefault("VELA_DB_HOST", "")
    private val dbPort = System.getenv().getOrDefault("VELA_DB_PORT", "3306")
    private val dbName = System.getenv().getOrDefault("VELA_DB_NAME", "VELA_VERSIONING")
    private val dbUser = System.getenv().getOrDefault("VELA_DB_USER", "root")
    private val dbPassword = System.getenv().getOrDefault("VELA_DB_PASS", "")

    private val connection: Connection? = if (canHandle()) openDbConnection() else null

    override fun canHandle(): Boolean = dbHost.isNotBlank()

    override fun determineBuildNo(): Int {
        val stmnt = connection!!.createStatement()
        val result = stmnt.executeQuery(
            """
                SELECT version FROM vela_version
                WHERE project = '$projName';
            """.trimIndent()
        )

        if (result.first()) {
            return result.getInt("VERSION")
        } else {
            stmnt.executeUpdate(
                """
                    INSERT INTO vela_version (project, version)
                    VALUES ('$projName', 0)
                """.trimIndent()
            )
            return 0
        }
    }

    override fun incBuildNo() {
        val version = determineBuildNo() + 1

        val updateStmnt = connection!!.createStatement()
        updateStmnt.executeUpdate(
            """
                UPDATE vela_version 
                SET version=$version
                WHERE project='$projName'
            """.trimIndent()
        )
        updateStmnt.close()
        connection.commit()
    }

    private fun openDbConnection(): Connection {
        Class.forName("org.mariadb.jdbc.Driver")
        val connection = DriverManager.getConnection("jdbc:mariadb://$dbHost:$dbPort/$dbName?user=$dbUser&password=$dbPassword")

        connection.createStatement().executeUpdate(
            """
                CREATE TABLE if NOT EXISTS vela_version (
                    project VARCHAR(255),
                    version INT,
                    PRIMARY KEY (project)
                );
            """.trimIndent()
        )
        return connection
    }
}