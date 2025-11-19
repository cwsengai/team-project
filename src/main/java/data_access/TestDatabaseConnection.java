package data_access;

/**
 * Simple test to verify database connection works.
 */
public class TestDatabaseConnection {
    public static void main(String[] args) {
        System.out.println("Testing database connection...");
        
        String dbUrl = EnvConfig.getDbUrl();
        System.out.println("DB URL: " + dbUrl.replaceAll(":[^:@]+@", ":****@"));
        
        try (var conn = java.sql.DriverManager.getConnection(dbUrl)) {
            System.out.println("✅ Connected successfully!");
            
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery("SELECT version()");
            if (rs.next()) {
                System.out.println("PostgreSQL version: " + rs.getString(1));
            }
            rs.close();
            stmt.close();
            
        } catch (Exception e) {
            System.err.println("❌ Connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
