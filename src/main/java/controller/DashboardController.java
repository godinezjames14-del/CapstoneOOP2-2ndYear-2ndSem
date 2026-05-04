package main.java.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.Node;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class DashboardController {

    @FXML private Label labelUserName;
    @FXML private BorderPane mainPane;
    @FXML private Button btnDashboard;
    @FXML private Button btnScholarships;

    @FXML
    public void initialize() {
        // Load the dashboard stats view into the center by default
        loadView("/home-stats-view.fxml");
    }

    public void setUserData(String name) {
        if (name != null && !name.isEmpty()) {
            labelUserName.setText(name.toUpperCase());
        }
    }

    @FXML
    private void showHome(MouseEvent event) {
        loadView("/home-stats-view.fxml");
        updateButtonStyles(btnDashboard, btnScholarships);
    }

    @FXML
    private void showScholarshipList(MouseEvent event) {
        loadView("/scholarship-list-view.fxml");
        updateButtonStyles(btnScholarships, btnDashboard);
    }

    private void updateButtonStyles(Button active, Button inactive) {
        active.setStyle("-fx-background-color: #800000; -fx-background-radius: 10; -fx-text-fill: white; -fx-cursor: hand;");
        inactive.setStyle("-fx-background-color: transparent; -fx-text-fill: #495057; -fx-cursor: hand;");
    }

    private void loadView(String fxmlPath) {
        try {
            URL loc = getClass().getResource(fxmlPath);
            if (loc == null) {
                System.err.println("Resource not found: " + fxmlPath);
                return;
            }
            // Standard loading without passing 'this' as controller
            Parent view = FXMLLoader.load(loc);
            mainPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/mainmenu-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}