package data_access;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Client for connecting to PostgreSQL database (including Supabase).
 * Manages database connections using JDBC.
 */
public class PostgresClient {
    private final String jdbcUrl;
    private final Properties props;

    /**
     * Creates a PostgreSQL client using environment configuration.
     * Parses DATABASE_URL connection string from .env
     */
    public PostgresClient() {
        String connectionString = EnvConfig.getDatabaseUrl();
        this.props = new Properties();
        
        try {
            // Parse: postgresql://user:password@host:port/database
            URI uri = new URI(connectionString);
            
            String userInfo = uri.getUserInfo();
            String[] credentials = userInfo.split(":", 2);
            String username = credentials[0];
            String password = credentials.length > 1 ? credentials[1] : "";
            
            String host = uri.getHost();
            int port = uri.getPort();
            String database = uri.getPath().substring(1); // Remove leading /
            
            // Build JDBC URL without credentials
            this.jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
            
            // Set credentials as properties
            props.setProperty("user", username);
            props.setProperty("password", password);
            
            // Add SSL settings for Supabase
            props.setProperty("ssl", "true");
            props.setProperty("sslmode", "require");
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse DATABASE_URL: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a PostgreSQL client with custom connection URL.
     *
     * @param url JDBC connection URL (with credentials embedded)
     */
    public PostgresClient(String url) {
        this.jdbcUrl = url;
        this.props = new Properties();
    }

    /**
     * Get a database connection.
     * Caller is responsible for closing the connection.
     *
     * @return active database connection
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, props);
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
        return jdbcUrl;
    }
}
