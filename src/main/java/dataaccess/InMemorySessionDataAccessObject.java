package dataaccess;

import java.util.Base64;
import java.util.UUID;

import org.json.JSONObject;

import use_case.session.SessionDataAccessInterface;

public class InMemorySessionDataAccessObject implements SessionDataAccessInterface {
    private String jwtToken;

    @Override
    public String getJwtToken() {
        return jwtToken;
    }

    @Override
    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    @Override
    public UUID getCurrentUserId() {
        if (jwtToken == null || jwtToken.isEmpty()) {
            throw new IllegalStateException("No JWT token in session");
        }
        // JWT format: header.payload.signature
        String[] parts = jwtToken.split("\\.");
        if (parts.length < 2) throw new IllegalArgumentException("Invalid JWT token");
        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
        JSONObject payload = new JSONObject(payloadJson);
        String userId = payload.optString("sub", null);
        if (userId == null) throw new IllegalArgumentException("JWT does not contain 'sub' (user_id)");
        return UUID.fromString(userId);
    }
}
