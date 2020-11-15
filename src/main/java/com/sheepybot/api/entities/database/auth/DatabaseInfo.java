package com.sheepybot.api.entities.database.auth;

import com.moandjiezana.toml.Toml;
import com.sheepybot.util.Objects;
import org.jetbrains.annotations.NotNull;

public class DatabaseInfo {

    private final String username;
    private final String password;
    private final String host;
    private final String database;
    private final int port;
    private final int poolSize;
    private final String databaseType;

    /**
     * Construct a new {@link DatabaseInfo} instance from a {@link Toml} instance
     *
     * @param table The {@link Toml} table
     */
    public DatabaseInfo(@NotNull("table cannot be null") final Toml table) {
        this(table.getString("username"), table.getString("password"), table.getString("host"),
                table.getString("database"), Math.toIntExact(table.getLong("port")), Math.toIntExact(table.getLong("poolSize")),
                table.getString("database_type", "postgresql"));
    }

    /**
     * Construct a new {@link DatabaseInfo}
     *
     * @param username     The username of the database server
     * @param password     The password of the database server
     * @param host         The address of the database server
     * @param database     The database to use on the server
     * @param port         The port of the database server
     * @param poolSize     The size of the connection pool
     * @param databaseType The database type to use, must be one of
     *                     <ul>
     *                         <li>mysql</li>
     *                         <li>mariadb</li>
     *                         <li>postgresql</li>
     *                     </ul>
     */
    public DatabaseInfo(@NotNull("username cannot be null") final String username,
                        @NotNull("password cannot be null") final String password,
                        @NotNull("host cannot be null") final String host,
                        @NotNull("database cannot be null") final String database,
                        final int port,
                        final int poolSize,
                        @NotNull("database type cannot be null") final String databaseType) {
        Objects.checkArgument(port > 0, "port must be greater than 0");
        Objects.checkArgument(poolSize > 0, "pool size must be greater than 0");
        this.username = username;
        this.password = password;
        this.host = host;
        this.database = database;
        this.port = port;
        this.poolSize = poolSize;
        this.databaseType = databaseType;
    }

    /**
     * @return The username of the database server
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * @return The password of the database server
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * @return The address of the database server
     */
    public String getHost() {
        return this.host;
    }

    /**
     * @return The database to use on the server
     */
    public String getDatabase() {
        return this.database;
    }

    /**
     * @return The port of the database server
     */
    public int getPort() {
        return this.port;
    }

    /**
     * @return The size of the connection pool
     */
    public int getPoolSize() {
        return this.poolSize;
    }

    /**
     * @return The database type
     */
    public String getDatabaseType() {
        return this.databaseType;
    }

}
