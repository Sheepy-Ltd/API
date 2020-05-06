package com.sheepybot.api.entities.database;

import com.sheepybot.api.entities.database.object.DBCursor;
import com.sheepybot.api.entities.database.object.DBObject;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sheepybot.BotInfo;
import com.sheepybot.api.entities.database.auth.DatabaseInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

//this will likely end up expanding in the future, but for now its just a simple fetch retrieve
//for raw sql statements, expect this in the future to give you the option between writing raw sql
//or just giving a list of things to insert and the class itself handles it
public class Database {

    private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);

    private final HikariDataSource dataSource;

    /**
     * Construct a new {@link Database} instance
     *
     * @param credentials The {@link DatabaseInfo} to use
     */
    public Database(@NotNull(value = "credentials cannot be null") final DatabaseInfo credentials) {
        this.dataSource = new HikariDataSource();
        this.dataSource.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s", credentials.getHost(), credentials.getPort(), credentials.getDatabase()));
        this.dataSource.setUsername(credentials.getUsername());
        this.dataSource.setPassword(credentials.getPassword());
        this.dataSource.setMaximumPoolSize(credentials.getPoolSize());
        this.dataSource.setLeakDetectionThreshold(5_000); //5 seconds
        this.dataSource.setConnectionTimeout(30_000); //30 seconds
        this.dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        this.dataSource.setPoolName(String.format("%s-Node-%d-Connection-Pool", BotInfo.BOT_NAME, 0));
        this.dataSource.addDataSourceProperty("useUnicode", "true");
        this.dataSource.addDataSourceProperty("cachePrepStmts", true);
        this.dataSource.addDataSourceProperty("prepStmtCacheSize", 250);
        this.dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        this.dataSource.addDataSourceProperty("useServerPrepStmts", true);
        this.dataSource.setConnectionInitSql("SET NAMES 'utf8mb4'");
    }

    /**
     * Find a singular database entry, It's advised to put {@code LIMIT 1} on the query because
     * should no limit be put on the query itself then only the first item will be returned.
     *
     * @param haystack The query to execute
     * @param needles  The values
     *
     * @return A {@link com.sheepybot.api.entities.database.object.DBObject}, or {@code null} if an error occurred.
     */
    public DBObject findOne(final String haystack,
                            final Object... needles) {
        try (final Connection connection = this.getConnection();
             final PreparedStatement statement = connection.prepareStatement(haystack)) {

            for (int i = 0; i < needles.length; i++) {
                statement.setObject((i+1), needles[i]);
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
            LOGGER.error(String.format("An SQLException was thrown whilst attempting to execute query '%s' with %s", haystack, (needles.length == 0 ? "no values" : Arrays.toString(needles))), ex);
        }
        return null;
    }

    /**
     * Find all rows that match the input query
     *
     * @param haystack The query to execute
     * @param needles  The values
     *
     * @return A {@link com.sheepybot.api.entities.database.object.DBCursor}, or {@code null} if an error occurred.
     */
    public DBCursor find(@NotNull(value = "query cannot be null") final String haystack,
                                                                  final Object... needles) {
        try (final Connection connection = this.getConnection();
             final PreparedStatement statement = connection.prepareStatement(haystack)) {

            for (int i = 0; i < needles.length; i++) {
                statement.setObject((i+1), needles[i]);
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
            LOGGER.error(String.format("An SQLException was thrown whilst attempting to execute query '%s' with %s", haystack, (needles.length == 0 ? "no values" : Arrays.toString(needles))), ex);
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
                statement.setObject((i+1), needles[i]);
            }

            return statement.executeUpdate() > 0;
        } catch (final SQLException ex) {
            LOGGER.error(String.format("An SQLException was thrown whilst attempting to execute query '%s' with %s", haystack, (needles.length == 0 ? "no values" : Arrays.toString(needles))), ex);
        }
        return false;
    }

    /**
     * Shutdown this {@link Database}, this will close the internal connection pool
     * preventing any more queries from being executed.
     */
    public void shutdown() {
        LOGGER.info("Shutting down HikariCP...");
        if (!this.dataSource.isClosed()) {
            this.dataSource.close();
        }
    }

    private Connection getConnection() {
        try {
            return this.dataSource.getConnection();
        } catch (final SQLException ex) {
            LOGGER.info("Failed to retrieve a connection", ex);
        }
        return null;
    }

}
