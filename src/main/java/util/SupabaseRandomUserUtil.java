package util;

import api.SupabaseAuthClient;
import data_access.EnvConfig;
import use_case.session.SessionDataAccessInterface;

public class SupabaseRandomUserUtil {
    /**
     * Creates a random Supabase user, logs in, and sets the JWT in the session DAO. Returns the JWT or null on failure.
     */
    public static String createAndLoginRandomUser(SessionDataAccessInterface sessionDAO) {
        try {
            String supabaseUrl = EnvConfig.getSupabaseUrl();
            String supabaseApiKey = EnvConfig.getSupabaseAnonKey();
            if (supabaseUrl == null || supabaseUrl.isEmpty() || supabaseApiKey == null || supabaseApiKey.isEmpty()) {
                System.err.println("ERROR: SUPABASE_URL or SUPABASE_ANON_KEY not set in EnvConfig.");
                return null;
            }
            SupabaseAuthClient authClient = new SupabaseAuthClient(supabaseUrl, supabaseApiKey);
            String jwt = authClient.createRandomUserAndGetJwt();
            if (jwt != null) {
                sessionDAO.setJwtToken(jwt);
                System.out.println("[SupabaseRandomUserUtil] Supabase login: JWT set = " + jwt);
            } else {
                System.err.println("[SupabaseRandomUserUtil] Supabase login failed.");
            }
            return jwt;
        } catch (Exception ex) {
            System.err.println("[SupabaseRandomUserUtil] Supabase login exception: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }
}
