package main.java.controller;

import JDBC.mySQLConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.sql.*;

public class RegisterController {

    @FXML private TextField txtFirstName, txtLastName, txtLRN, txtEmail;
    @FXML private PasswordField txtPassword, txtConfirmPassword;
    @FXML private ComboBox<String> comboRole;
    @FXML private VBox studentFields;
    @FXML private Label lblFileName;

    private File selectedFile;

    @FXML
    public void initialize() {
        // Setup Role selection
        comboRole.getItems().addAll("STUDENT", "SPONSOR", "ADMIN");
        comboRole.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleRoleSelection() {
        // Toggle visibility: only Student needs LRN and Transcript
        boolean isStudent = "STUDENT".equals(comboRole.getValue());
        studentFields.setVisible(isStudent);
        studentFields.setManaged(isStudent);
    }

    @FXML
    private void handleBrowse(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
        if (selectedFile != null) {
            lblFileName.setText(selectedFile.getName());
        }
    }

    @FXML
    private void handleCompleteRegistration(MouseEvent event) {
        String role = comboRole.getValue();

        // Validation
        if (txtFirstName.getText().isEmpty() || txtEmail.getText().isEmpty() || txtPassword.getText().isEmpty()) {
            System.err.println("Required fields are empty.");
            return;
        }

        if (!txtPassword.getText().equals(txtConfirmPassword.getText())) {
            System.err.println("Passwords do not match.");
            return;
        }

        try (Connection conn = mySQLConnection.getConnection()) {
            String userSql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
            PreparedStatement userPstmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            userPstmt.setString(1, txtFirstName.getText() + " " + txtLastName.getText());
            userPstmt.setString(2, txtEmail.getText());
            userPstmt.setString(3, txtPassword.getText());
            userPstmt.setString(4, role);
            userPstmt.executeUpdate();

            System.out.println(role + " registered successfully!");
            MainMenuScreen(event); // Redirect after success

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // FIXED: Added MouseEvent parameter to match FXML Namespace
    @FXML
    private void MainMenuScreen(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/mainmenu-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            System.err.println("Could not load mainmenu-view.fxml");
            e.printStackTrace();
        }
    }
}