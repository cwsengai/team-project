package data_access;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class to create database connections from DATABASE_URL.
 * Properly parses postgresql:// URLs and converts to JDBC format.
 */
public class DatabaseConnection {
    
    /**
     * Create a database connection from the DATABASE_URL environment variable.
     * Handles both formats:
     * - postgresql://user:password@host:port/database
     * - jdbc:postgresql://host:port/database (with separate user/password)
     */
    public static Connection getConnection() throws SQLException {
        String dbUrl = EnvConfig.getDatabaseUrl();
        
        if (dbUrl == null || dbUrl.isEmpty()) {
            throw new SQLException("DATABASE_URL not configured in environment");
        }
        
        // Parse the URL
        if (dbUrl.startsWith("postgresql://")) {
            return connectFromPostgresUrl(dbUrl);
        } else if (dbUrl.startsWith("jdbc:postgresql://")) {
            // Already in JDBC format, use as-is
            return DriverManager.getConnection(dbUrl);
        } else {
            throw new SQLException("Unsupported DATABASE_URL format: " + dbUrl);
        }
    }
    
    /**
     * Parse and connect using postgresql:// URL format.
     * Format: postgresql://username:password@hostname:port/database
     */
    private static Connection connectFromPostgresUrl(String postgresUrl) throws SQLException {
        // Pattern to parse: postgresql://user:password@host:port/database
        Pattern pattern = Pattern.compile("postgresql://([^:]+):([^@]+)@([^:]+):(\\d+)/(.+)");
        Matcher matcher = pattern.matcher(postgresUrl);
        
        if (!matcher.matches()) {
            throw new SQLException("Invalid PostgreSQL URL format: " + postgresUrl);
        }
        
        String user = matcher.group(1);
        String password = matcher.group(2);
        String host = matcher.group(3);
        String port = matcher.group(4);
        String database = matcher.group(5);
        
        // Build JDBC URL without credentials
        String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s?sslmode=require", host, port, database);
        
        // Pass credentials via Properties
        Properties props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", password);
        props.setProperty("ssl", "true");
        props.setProperty("sslmode", "require");
        
        return DriverManager.getConnection(jdbcUrl, props);
    }
    
    /**
     * Get a masked version of the database URL for logging.
     */
    public static String getMaskedUrl() {
        String dbUrl = EnvConfig.getDatabaseUrl();
        if (dbUrl == null) return "not configured";
        
        // Mask password in URL
        return dbUrl.replaceAll(":[^:@]+@", ":****@");
    }
}
