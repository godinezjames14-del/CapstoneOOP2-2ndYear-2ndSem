package main.java.controller;

import JDBC.mySQLConnection;
import Serialization.FileHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import main.java.model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.*;

public class MainMenuController {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;

    @FXML
    private void handleLogin(MouseEvent event) {
        User user = authenticate(txtEmail.getText(), txtPassword.getText());

        if (user != null) {
            FileHandler.serialize(user, "session.ser");
            navigateToDashboard(event, user);
        } else {
            System.err.println("Login Failed: Incorrect email or password.");
        }
    }

    private User authenticate(String email, String password) {
        String query = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = mySQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(rs.getInt("userID"), rs.getString("name"), rs.getString("role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void navigateToDashboard(MouseEvent event, User user) {
        try {
            URL fxmlLocation = getClass().getResource("/dashboard-view.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            DashboardController dashboard = loader.getController();
            if (dashboard != null) {
                dashboard.setUserData(user.getName(), user.getUserID(), user.getRole());
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
            System.out.println("Login Successful. Welcome " + user.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void RegisterScreen(MouseEvent event) {
        try {
            Parent registerRoot = FXMLLoader.load(getClass().getResource("/register-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(registerRoot));
            stage.show();
        } catch (IOException e) {
            System.err.println("Error: Could not load register-view.fxml");
            e.printStackTrace();
        }
    }
}