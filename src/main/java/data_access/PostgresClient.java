package data_access;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Client for connecting to PostgreSQL database (including Supabase).
 * Manages database connections using JDBC.
 */
public class PostgresClient {
    private final String url;

    /**
     * Creates a PostgreSQL client using environment configuration.
     * Uses DATABASE_URL connection string from .env
     */
    public PostgresClient() {
        this.url = EnvConfig.getDbUrl();
    }

    /**
     * Creates a PostgreSQL client with custom connection URL.
     *
     * @param url JDBC connection URL (with credentials embedded)
     */
    public PostgresClient(String url) {
        this.url = url;
    }

    /**
     * Get a database connection.
     * Caller is responsible for closing the connection.
     *
     * @return active database connection
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
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
