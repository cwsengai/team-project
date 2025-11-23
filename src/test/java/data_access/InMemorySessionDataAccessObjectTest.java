package data_access;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class InMemorySessionDataAccessObjectTest {
    // Example JWT with sub = 123e4567-e89b-12d3-a456-426614174000
    // Header: {"alg":"HS256","typ":"JWT"}
    // Payload: {"sub":"123e4567-e89b-12d3-a456-426614174000"}
    // Signature: dummy
    private static final String EXAMPLE_JWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjNlNDU2Ny1lODliLTEyZDMtYTQ1Ni00MjY2MTQxNzQwMDAifQ.dummy";
    private static final UUID EXAMPLE_UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    @Test
    void testSetAndGetJwtToken() {
        InMemorySessionDataAccessObject sessionDAO = new InMemorySessionDataAccessObject();
        assertNull(sessionDAO.getJwtToken());
        sessionDAO.setJwtToken(EXAMPLE_JWT);
        assertEquals(EXAMPLE_JWT, sessionDAO.getJwtToken());
    }

    @Test
    void testGetCurrentUserId() {
        InMemorySessionDataAccessObject sessionDAO = new InMemorySessionDataAccessObject();
        sessionDAO.setJwtToken(EXAMPLE_JWT);
        UUID userId = sessionDAO.getCurrentUserId();
        assertEquals(EXAMPLE_UUID, userId);
    }

    @Test
    void testGetCurrentUserIdThrowsWhenNoJwt() {
        InMemorySessionDataAccessObject sessionDAO = new InMemorySessionDataAccessObject();
        
        assertThrows(IllegalStateException.class, sessionDAO::getCurrentUserId);
    }
}
