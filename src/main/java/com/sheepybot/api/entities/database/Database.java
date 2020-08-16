package com.sheepybot.api.entities.database;

import com.sheepybot.BotInfo;
import com.sheepybot.api.entities.database.auth.DatabaseInfo;
import com.sheepybot.api.entities.database.object.DBCursor;
import com.sheepybot.api.entities.database.object.DBObject;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class Database {

    private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);

    private final HikariDataSource dataSource;

    /**
     * Construct a new {@link Database} instance
     *
     * @param info The {@link DatabaseInfo} to use
     */
    public Database(@NotNull(value = "credentials cannot be null") final DatabaseInfo info) {
        this.dataSource = new HikariDataSource();
        this.dataSource.setJdbcUrl(String.format("jdbc:%s://%s:%s/%s", info.getDatabaseType(), info.getHost(), info.getPort(), info.getDatabase()));
        this.dataSource.setUsername(info.getUsername());
        this.dataSource.setPassword(info.getPassword());
        this.dataSource.setMaximumPoolSize(info.getPoolSize());
        this.dataSource.setLeakDetectionThreshold(5_000); //5 seconds
        this.dataSource.setConnectionTimeout(30_000); //30 seconds
        this.dataSource.setDataSourceClassName(getDataSourceClassNameFromDatabaseType(info.getDatabaseType()));
        this.dataSource.setPoolName(String.format("%s-Connection-Pool", BotInfo.BOT_NAME));
    }

    /**
     * Figure out the proper driver class name for Hikari CP to use dependant on the
     * type of database specified within the bot configuration.
     *
     * <p>The currently accepted database types are as follows:</p>
     * <ul>
     *     <li>postgresql</li>
     *     <li>mariadb</li>
     *     <li>mysql</li>
     * </ul>
     *
     * @param databaseType The database type.
     * @return The database type
     * @throws IllegalArgumentException If the input argument is not one of the accepted types.
     */
    private String getDataSourceClassNameFromDatabaseType(final String databaseType) {
        switch (databaseType.toLowerCase()) {
            case "postgresql":
                return "org.postgresql.ds.PGSimpleDataSource";
            case "mariadb":
                return "org.mariadb.jdbc.Driver";
            case "mysql":
                return "com.mysql.jdbc.Driver";
        }
        throw new IllegalArgumentException(String.format("Invalid database type %s (must be one of postgresql, mariadb or mysql)", databaseType));
    }

    /**
     * Find a singular database entry, It's advised to put {@code LIMIT 1} on the query because
     * should no limit be put on the query itself then only the first item will be returned.
     *
     * @param haystack The query to execute
     * @param needles  The values
     * @return A {@link DBObject} containing the rows which were returned by the database, or {@code null} if an error occurred.
     */
    public DBObject findOne(final String haystack,
                            final Object... needles) {
        try (final Connection connection = this.getConnection();
             final PreparedStatement statement = connection.prepareStatement(haystack)) {

            for (int i = 0; i < needles.length; i++) {
                statement.setObject((i + 1), needles[i]);
            }

            final DBObject object = new DBObject();
            try (final ResultSet set = statement.executeQuery()) {
                if (set.next()) {
                    for (int i = 0; i < set.getMetaData().getColumnCount() + 1; i++) {
                        object.add(set.getMetaData().getColumnName(i), set.getObject(i));
                    }
                }
            }

            return object;
        } catch (final SQLException ex) {
            LOGGER.error(String.format("An SQLException was thrown whilst attempting to execute query '%s' with values %s", haystack, (needles.length == 0 ? "no values" : Arrays.toString(needles))), ex);
        }
        return null;
    }

    /**
     * Find all rows that match the input query
     *
     * @param haystack The query to execute
     * @param needles  The values
     *
     * @return A {@link DBCursor}, or {@code null} if an error occurred.
     */
    public DBCursor find(@NotNull(value = "query cannot be null") final String haystack,
                         final Object... needles) {
        try (final Connection connection = this.getConnection();
             final PreparedStatement statement = connection.prepareStatement(haystack)) {

            for (int i = 0; i < needles.length; i++) {
                statement.setObject((i + 1), needles[i]);
            }

            final DBCursor cursor = new DBCursor();
            try (final ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    final DBObject object = new DBObject();
                    for (int i = 0; i < set.getMetaData().getColumnCount() + 1; i++) {
                        object.add(set.getMetaData().getColumnName(i), set.getObject(i));
                    }
                    cursor.add(object);
                }
            }

            return cursor;
        } catch (final SQLException ex) {
            LOGGER.error(String.format("An SQLException was thrown whilst attempting to execute query '%s' with values %s", haystack, (needles.length == 0 ? "no values" : Arrays.toString(needles))), ex);
        }
        return null;
    }

    /**
     * @param haystack The query to execute
     * @param needles  The values
     *
     * @return {@code true} if this was successful, {@code false} if there were no rows affected or an error occurred
     */
    public boolean execute(@NotNull(value = "haystack cannot be null") final String haystack,
                           final Object... needles) {
        try (final Connection connection = this.getConnection();
             final PreparedStatement statement = connection.prepareStatement(haystack)) {

            for (int i = 0; i < needles.length; i++) {
                statement.setObject((i + 1), needles[i]);
            }

            return statement.executeUpdate() > 0;
        } catch (final SQLException ex) {
            LOGGER.error(String.format("An SQLException was thrown whilst attempting to execute query '%s' with values %s", haystack, (needles.length == 0 ? "no values" : Arrays.toString(needles))), ex);
        }
        return false;
    }

    /**
     * Shutdown this {@link Database}, this will close the internal connection pool
     * preventing any more queries from being executed.
     *
     * <p>This should only ever be called by the API directly, any calls made by a module may disrupt the process
     * of other modules.</p>
     */
    public void shutdown() {
        LOGGER.info("Shutting down database connection pool...");
        if (!this.dataSource.isClosed()) {
            this.dataSource.close();
        }
    }

    /**
     * @return A {@link Connection}
     * @throws SQLException If we couldn't retrieve a connection from the pool.
     */
    private Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

}
