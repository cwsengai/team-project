package hmbstnks;

import java.util.HashMap;
import java.util.Map;

public class UserDatabase {
    private final Map<String, User> users = new HashMap<>();

    public boolean emailExists(String email) {
        return users.containsKey(email);
    }

    public void saveUser(User user) {
        users.put(user.getEmail(), user);
    }

    public User getUser(String email) {
        return users.get(email);
    }
}
