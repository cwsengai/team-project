package data_access;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Client for connecting to PostgreSQL database.
 * Manages database connections using JDBC.
 * 
 * For local development with pgAdmin.
 */
public class PostgresClient {
    private final String url;
    private final String user;
    private final String password;

    /**
     * Creates a PostgreSQL client using environment configuration.
     */
    public PostgresClient() {
        this.url = EnvConfig.getDbUrl();
        this.user = EnvConfig.getDbUser();
        this.password = EnvConfig.getDbPassword();
    }

    /**
     * Creates a PostgreSQL client with custom connection details.
     *
     * @param url JDBC connection URL
     * @param user database username
     * @param password database password
     */
    public PostgresClient(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /**
     * Get a database connection.
     * Caller is responsible for closing the connection.
     *
     * @return active database connection
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Test the database connection.
     *
     * @return true if connection successful, false otherwise
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get the JDBC URL being used.
     *
     * @return JDBC connection URL
     */
    public String getUrl() {
        return url;
    }
}
