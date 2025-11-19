package data_access;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import data_access.client.AuthResponse;
import data_access.client.SupabaseClient;

/**
 * Integration tests for SupabaseClient.
 * Tests authentication and basic client operations.
 * 
 * Prerequisites:
 * - SUPABASE_URL and SUPABASE_ANON_KEY must be set in .env
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Supabase Client Tests")
public class SupabaseClientTest {
    
    private static SupabaseClient client;
    private static String testEmail;
    private static final String TEST_PASSWORD = "TestClient123!";
    
    @BeforeAll
    @SuppressWarnings("unused")
    static void setUp() {
        client = new SupabaseClient();
        testEmail = "test.client." + System.currentTimeMillis() + "@test.com";
    }
    
    @Test
    @Order(1)
    @DisplayName("Should create client with default configuration")
    void testCreateClient() {
        // Arrange & Act
        SupabaseClient defaultClient = new SupabaseClient();
        
        // Assert
        assertNotNull(defaultClient);
    }
    
    @Test
    @Order(2)
    @DisplayName("Should create client with service role")
    void testCreateClientWithServiceRole() {
        // Arrange & Act
        SupabaseClient serviceClient = new SupabaseClient(true);
        
        // Assert
        assertNotNull(serviceClient);
    }
    
    @Test
    @Order(3)
    @DisplayName("Should sign up new user")
    void testSignUp() throws IOException {
        // Act
        AuthResponse response = client.signUp(testEmail, TEST_PASSWORD);
        
        // Assert
        assertNotNull(response);
        assertNotNull(response.getUser());
        assertNotNull(response.getUser().getId());
        assertNotNull(response.getAccessToken());
        System.out.println("Signed up test user: " + testEmail + " (ID: " + response.getUser().getId() + ")");
    }
    
    @Test
    @Order(4)
    @DisplayName("Should sign in existing user")
    void testSignIn() throws IOException {
        // Act
        AuthResponse response = client.signIn(testEmail, TEST_PASSWORD);
        
        // Assert
        assertNotNull(response);
        assertNotNull(response.getUser());
        assertNotNull(response.getAccessToken());
        assertEquals(testEmail, response.getUser().getEmail());
    }
    
    @Test
    @Order(5)
    @DisplayName("Should fail sign in with wrong password")
    void testSignInWrongPassword() {
        // Act & Assert
        assertThrows(IOException.class, () -> {
            client.signIn(testEmail, "WrongPassword123!");
        });
    }
    
    @Test
    @Order(6)
    @DisplayName("Should fail sign in with non-existent user")
    void testSignInNonExistent() {
        // Act & Assert
        assertThrows(IOException.class, () -> {
            client.signIn("nonexistent@example.com", TEST_PASSWORD);
        });
    }
    
    @Test
    @Order(7)
    @DisplayName("Should fail sign up with duplicate email")
    void testSignUpDuplicate() {
        // Act & Assert
        assertThrows(IOException.class, () -> {
            client.signUp(testEmail, TEST_PASSWORD);
        });
    }
    
    @Test
    @Order(8)
    @DisplayName("Should fail sign up with weak password")
    void testSignUpWeakPassword() {
        // Arrange
        String weakEmail = "weak." + System.currentTimeMillis() + "@test.com";
        
        // Act & Assert
        assertThrows(IOException.class, () -> {
            client.signUp(weakEmail, "123"); // Too short
        });
    }
    
    @Test
    @Order(9)
    @DisplayName("Should handle authenticated requests after sign in")
    void testAuthenticatedRequest() throws IOException {
        // Arrange - sign in first
        AuthResponse authResponse = client.signIn(testEmail, TEST_PASSWORD);
        assertNotNull(authResponse.getAccessToken());
        
        // Act - try a query that requires authentication
        // This will test that the access token is properly set
        try {
            client.queryWithFilter("user_profiles", "id=eq." + authResponse.getUser().getId(), Object[].class);
            // If it doesn't throw, authentication is working
        } catch (IOException e) {
            // It might fail due to missing data, but not due to auth issues
            assertFalse(e.getMessage().contains("JWT") || e.getMessage().contains("authentication"));
        }
    }
    
    @Test
    @Order(10)
    @DisplayName("Should handle JSON serialization and deserialization")
    void testJsonHandling() throws IOException {
        // This is implicitly tested by all the auth operations above
        // which serialize/deserialize JSON data
        
        // Sign in to get a valid response
        AuthResponse response = client.signIn(testEmail, TEST_PASSWORD);
        
        // Assert that complex objects are properly deserialized
        assertNotNull(response.getUser());
        assertNotNull(response.getUser().getId());
        assertNotNull(response.getUser().getEmail());
        assertNotNull(response.getAccessToken());
    }
}
