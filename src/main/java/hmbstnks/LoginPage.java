package hmbstnks;

import javax.swing.*;
import java.awt.*;

public class LoginPage extends JFrame {

    public LoginPage(AuthController controller) {
        setTitle("Billionaire – Login / Sign Up");
        setSize(420, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Program Title
        JLabel programTitle = new JLabel("Billionaire", SwingConstants.CENTER);
        programTitle.setFont(new Font("Arial", Font.BOLD, 26));
        programTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        JLabel subtitle = new JLabel("Login or Create an Account", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField displayNameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        displayNameField.setPreferredSize(new Dimension(250, 30));
        emailField.setPreferredSize(new Dimension(250, 30));
        passwordField.setPreferredSize(new Dimension(250, 30));

        // Buttons
        JButton loginBtn = new JButton("Login");
        JButton signupBtn = new JButton("Sign Up");

        loginBtn.setPreferredSize(new Dimension(120, 40));
        signupBtn.setPreferredSize(new Dimension(120, 40));

        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        signupBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel message = new JLabel(" ", SwingConstants.CENTER);
        message.setAlignmentX(Component.CENTER_ALIGNMENT);
        message.setFont(new Font("Arial", Font.PLAIN, 14));

        // ---- UI Layout ----
        mainPanel.add(programTitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(subtitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Display Name (sign up only)
        mainPanel.add(new JLabel("Display Name (required for sign up):"));
        mainPanel.add(displayNameField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Email
        mainPanel.add(new JLabel("Email:"));
        mainPanel.add(emailField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Password
        mainPanel.add(new JLabel("Password:"));
        mainPanel.add(passwordField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        mainPanel.add(loginBtn);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(signupBtn);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(message);

        add(mainPanel);

        // -------- Button Logic --------

        // LOGIN → Display name must NOT be used
        loginBtn.addActionListener(e -> {
            if (!displayNameField.getText().trim().isEmpty()) {
                message.setText("Do NOT enter a display name when logging in.");
                return;
            }

            String result = controller.handleLogin(
                    emailField.getText(),
                    new String(passwordField.getPassword())
            );
            message.setText(result);
        });

        // SIGN UP → Display name MUST be provided
        signupBtn.addActionListener(e -> {
            if (displayNameField.getText().trim().isEmpty()) {
                message.setText("Please choose a display name to sign up.");
                return;
            }

            if (emailField.getText().trim().isEmpty()) {
                message.setText("Email cannot be empty.");
                return;
            }

            if (passwordField.getPassword().length == 0) {
                message.setText("Password cannot be empty.");
                return;
            }

            String result = controller.handleSignup(
                    displayNameField.getText(),
                    emailField.getText(),
                    new String(passwordField.getPassword())
            );
            message.setText(result);
        });
    }
}
