package hmbstnks;

public class MainLoginTest {
    public static void main(String[] args) {
        UserDatabase db = new UserDatabase();
        AuthService authService = new AuthService(db);
        AuthController controller = new AuthController(authService);

        LoginPage page = new LoginPage(controller);
        page.setVisible(true);
    }
}
