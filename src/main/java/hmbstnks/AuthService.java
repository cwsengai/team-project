package hmbstnks;

public class AuthService {

    private final UserDatabase userDb;

    public AuthService(UserDatabase userDb) {
        this.userDb = userDb;
    }

    public String signup(String displayName, String email, String password) {
        if (userDb.emailExists(email)) {
            return "Error: Email already registered.";
        }

        String hash = String.valueOf(password.hashCode());
        User user = new User(displayName, email, hash);
        userDb.saveUser(user);

        return "Sign up successful!";
    }

    public String login(String email, String password) {
        User user = userDb.getUser(email);

        if (user == null) {
            return "Error: Account does not exist.";
        }

        String hash = String.valueOf(password.hashCode());

        if (!hash.equals(user.getPasswordHash())) {
            return "Error: Incorrect password.";
        }

        return "Login successful! Welcome " + user.getDisplayName() + "!";
    }
}
