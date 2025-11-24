package hmbstnks;

import javax.swing.JFrame;
import app.ui.LoginPage;
import data_access.InMemorySessionDataAccessObject;

public class LoginTest {
    public static void main(String[] args) {
        InMemorySessionDataAccessObject session = new InMemorySessionDataAccessObject();

        LoginPage login = new LoginPage(new JFrame(), session);

        if (login.wasSuccessful()) {
            System.out.println("SUCCESS!");
            System.out.println("JWT = " + session.getJwtToken());
        } else {
            System.out.println("FAILED OR CANCELLED");
        }
    }
}
