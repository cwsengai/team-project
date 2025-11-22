package data_access;

import entity.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Supabase implementation of UserRepository.
 * Uses REST API to interact with the user_profiles table.
 * 
 * Note: Email and password are managed by Supabase Auth (auth.users table).
 * This repository only manages the user profile data (public.user_profiles table).
 */
public class SupabaseUserRepository implements UserRepository {
    private final SupabaseClient client;

    /**
     * Creates a new Supabase user repository.
     *
     * @param client the authenticated Supabase client
     */
    public SupabaseUserRepository(SupabaseClient client) {
        this.client = client;
    }

    @Override
    public Optional<User> findById(String id) {
        try {
            // Query: GET /rest/v1/user_profiles?id=eq.{id}
            User[] users = client.queryWithFilter(
                "user_profiles",
                "id=eq." + id,
                User[].class
            );

            if (users != null && users.length > 0) {
                return Optional.of(users[0]);
            }
            return Optional.empty();

        } catch (IOException e) {
            throw new RuntimeException("Error fetching user by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try {
            // Note: Email is stored in auth.users, not user_profiles
            // This query may need adjustment based on your schema
            // For now, we'll search by display_name as a placeholder
            // In production, you would query auth.users via Supabase Admin API
            
            // This is a limitation: we can't directly query auth.users from client
            // You would need to implement this via a Supabase Edge Function
            // or use the Admin API with service role key (server-side only)
            
            throw new UnsupportedOperationException(
                "findByEmail requires Supabase Admin API or Edge Function. " +
                "Use authentication flow (signIn/signUp) instead."
            );

        } catch (Exception e) {
            throw new RuntimeException("Error fetching user by email: " + e.getMessage(), e);
        }
    }

    @Override
    public User save(User user) {
        try {
            // If user has no ID, it's a new insert
            // Otherwise, it's an update
            if (user.getId() == null || user.getId().isEmpty()) {
                throw new IllegalArgumentException(
                    "User ID is required. Create users via Supabase Auth signUp() first."
                );
            }

            // Check if user profile exists
            Optional<User> existing = findById(user.getId());
            
            if (existing.isPresent()) {
                return update(user);
            } else {
                return insert(user);
            }

        } catch (IOException e) {
            throw new RuntimeException("Error saving user: " + e.getMessage(), e);
        }
    }

    private User insert(User user) throws IOException {
        // Create profile data (without password)
        Map<String, Object> profileData = new HashMap<>();
        profileData.put("id", user.getId());
        profileData.put("display_name", user.getDisplayName());
        profileData.put("created_at", user.getCreatedAt().toString());
        if (user.getLastLogin() != null) {
            profileData.put("last_login", user.getLastLogin().toString());
        }

        User[] result = client.insert(
            "user_profiles",
            profileData,
            User[].class
        );

        if (result != null && result.length > 0) {
            return result[0];
        }
        throw new RuntimeException("Insert failed: no data returned");
    }

    private User update(User user) throws IOException {
        // Update profile data
        Map<String, Object> profileData = new HashMap<>();
        profileData.put("display_name", user.getDisplayName());
        if (user.getLastLogin() != null) {
            profileData.put("last_login", user.getLastLogin().toString());
        }

        User[] result = client.update(
            "user_profiles",
            "id=eq." + user.getId(),
            profileData,
            User[].class
        );

        if (result != null && result.length > 0) {
            return result[0];
        }
        throw new RuntimeException("Update failed: user profile not found");
    }

    @Override
    public void updateLastLogin(String userId, LocalDateTime timestamp) {
        try {
            // Create a partial update object
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("last_login", timestamp.toString());

            client.update(
                "user_profiles",
                "id=eq." + userId,
                updateData,
                User[].class
            );

        } catch (IOException e) {
            throw new RuntimeException("Error updating last login: " + e.getMessage(), e);
        }
    }
}
