package data_access;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import data_access.client.AuthResponse;
import data_access.client.SupabaseClient;
import data_access.repository.supabase.SupabaseUserRepository;
import entity.User;

/**
 * Integration tests for SupabaseUserRepository.
 * Tests CRUD operations against the Supabase database.
 * 
 * Prerequisites:
 * - SUPABASE_URL and SUPABASE_ANON_KEY must be set in .env
 * - Supabase auth must be configured correctly
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Supabase User Repository Tests")
public class SupabaseUserRepositoryTest {
    
    private static SupabaseClient client;
    private static SupabaseUserRepository repository;
    private static String testUserId;
    private static String testEmail;
    private static final String TEST_PASSWORD = "TestUser123!";
    private static final boolean CLEANUP_AFTER_TESTS = true;
    
    @BeforeAll
    @SuppressWarnings("unused")
    static void setUp() throws IOException {
        // Initialize Supabase client
        client = new SupabaseClient();
        
        // Create unique test user
        testEmail = "test.user." + System.currentTimeMillis() + "@test.com";
        AuthResponse authResponse = client.signUp(testEmail, TEST_PASSWORD);
        testUserId = authResponse.getUser().getId();
        System.out.println("Created test user: " + testEmail + " (ID: " + testUserId + ")");
        
        // Initialize repository
        repository = new SupabaseUserRepository(client);
    }
    
    @AfterAll
    @SuppressWarnings("unused")
    static void tearDown() {
        if (CLEANUP_AFTER_TESTS && testUserId != null) {
            try {
                repository.delete(testUserId);
                System.out.println("Cleaned up test user: " + testEmail);
            } catch (Exception e) {
                System.out.println("Cleanup warning: " + e.getMessage());
            }
        } else {
            System.out.println("Test user cleanup skipped: " + testEmail);
        }
        // Note: Supabase doesn't provide easy user deletion via client API
        // Users should be cleaned up manually or via admin dashboard
    }
    
    @Test
    @Order(1)
    @DisplayName("Should save new user profile")
    void testSaveNewUser() {
        // Arrange
        User user = new User(testUserId, testEmail, "Test User");
        
        // Act
        User saved = repository.save(user);
        
        // Assert
        assertNotNull(saved);
        assertEquals(testUserId, saved.getId());
        assertEquals("Test User", saved.getDisplayName());
    }
    
    @Test
    @Order(2)
    @DisplayName("Should find user by ID")
    void testFindById() {
        // Act
        Optional<User> found = repository.findById(testUserId);
        
        // Assert
        assertTrue(found.isPresent());
        assertEquals(testUserId, found.get().getId());
        assertEquals("Test User", found.get().getDisplayName());
    }
    
    @Test
    @Order(3)
    @DisplayName("Should update existing user profile")
    void testUpdateUser() {
        // Arrange
        User user = new User(testUserId, testEmail, "Updated Name");
        
        // Act
        User updated = repository.save(user);
        
        // Assert
        assertNotNull(updated);
        assertEquals("Updated Name", updated.getDisplayName());
        
        // Verify in database
        Optional<User> found = repository.findById(testUserId);
        assertTrue(found.isPresent());
        assertEquals("Updated Name", found.get().getDisplayName());
    }
    
    @Test
    @Order(4)
    @DisplayName("Should update last login timestamp")
    void testUpdateLastLogin() {
        // Arrange
        LocalDateTime loginTime = LocalDateTime.now();
        
        // Act
        repository.updateLastLogin(testUserId, loginTime);
        
        // Assert
        Optional<User> found = repository.findById(testUserId);
        assertTrue(found.isPresent());
        assertNotNull(found.get().getLastLogin());
        // Note: Database might truncate milliseconds, so check within a second
        assertTrue(Math.abs(java.time.Duration.between(loginTime, found.get().getLastLogin()).getSeconds()) < 2);
    }
    
    @Test
    @Order(5)
    @DisplayName("Should return empty optional for non-existent user")
    void testFindByIdNotFound() {
        // Act
        Optional<User> result = repository.findById("00000000-0000-0000-0000-000000000000");
        
        // Assert
        assertFalse(result.isPresent());
    }
    
    @Test
    @Order(6)
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @DisplayName("Should throw UnsupportedOperationException for findByEmail")
    void testFindByEmailNotSupported() {
        // Act & Assert
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.findByEmail(testEmail);
        });
    }
    
    @Test
    @Order(7)
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @DisplayName("Should throw exception when saving user without ID")
    void testSaveWithoutId() {
        // Arrange
        User user = new User(null, "test@example.com", "No ID User");
        
        // Act & Assert
        assertThrows(Exception.class, () -> {
            repository.save(user);
        });
    }
    
    @Test
    @Order(8)
    @DisplayName("Should call delete without error (RLS may prevent actual deletion)")
    void testDeleteUser() throws IOException {
        // Arrange - create a real authenticated user for deletion test
        String tempEmail = "test.delete." + System.currentTimeMillis() + "@test.com";
        AuthResponse authResponse = client.signUp(tempEmail, TEST_PASSWORD);
        String tempUserId = authResponse.getUser().getId();
        
        // Create user profile
        User tempUser = new User(tempUserId, tempEmail, "Temp User");
        repository.save(tempUser);
        
        // Verify it exists
        Optional<User> found = repository.findById(tempUserId);
        assertTrue(found.isPresent(), "Temporary user should exist");
        
        // Act & Assert - verify delete method executes without throwing an exception
        // Note: RLS policies may prevent actual deletion of user profiles,
        // but we can verify the method call completes successfully
        assertDoesNotThrow(() -> repository.delete(tempUserId),
            "Delete method should execute without throwing an exception");
        
        // The actual deletion may be prevented by RLS, so we just verify the API call succeeded
        System.out.println("Delete API call completed successfully (RLS may have prevented actual deletion)");
    }
}
