package hmbstnks;

public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public String handleLogin(String email, String password) {
        return authService.login(email, password);
    }

    public String handleSignup(String displayName, String email, String password) {
        return authService.signup(displayName, email, password);
    }
}
