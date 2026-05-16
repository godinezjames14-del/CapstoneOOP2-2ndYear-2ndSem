package main.java.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.Node;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class InterfaceController {

    @FXML private Label labelUserName;
    @FXML private BorderPane mainPane;
    @FXML private Button btnDashboard;
    @FXML private Button btnScholarships;
    @FXML private Button btnProfile;
    @FXML private Label labelRole;
    @FXML private Button btnDynamicAction;

    private int currentUserID;
    private String currentUserRole;

    @FXML
    public void initialize() {
        loadView("/student-dashboard.fxml");
    }

    public void setUserData(String name, int userID, String role) {
        this.currentUserID = userID;
        this.currentUserRole = role;
        labelRole.setText(role);
        if (name != null && !name.isEmpty()) {
            labelUserName.setText(name.toUpperCase());
        }
        configureDynamicTabs();
    }

    public void setUserData(String name) {
        if (name != null && !name.isEmpty()) {
            labelUserName.setText(name.toUpperCase());
        }
    }

    @FXML
    private void showHome(MouseEvent event) {
        loadView("/student-dashboard.fxml");
        updateButtonStyles(btnDashboard, btnScholarships, btnProfile);
    }

    @FXML
    private void showScholarshipList(MouseEvent event) {
        loadView("/scholarship-list.fxml");
        updateButtonStyles(btnScholarships, btnDashboard, btnProfile);
    }

    @FXML
    private void showProfile(MouseEvent event) {
        try {
            String fxmlPath = "SPONSOR".equalsIgnoreCase(currentUserRole)
                    ? "/sponsor-profile.fxml"
                    : "/student-profile.fxml";

            URL loc = getClass().getResource(fxmlPath);
            if (loc == null) {
                System.err.println("Profile FXML not found: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(loc);
            Parent view = loader.load();

            if ("SPONSOR".equalsIgnoreCase(currentUserRole)) {
                SponsorProfileController ctrl = loader.getController();
                ctrl.setUserID(currentUserID);
            } else {
                StudentProfileController ctrl = loader.getController();
                ctrl.setUserID(currentUserID);
            }

            if (view instanceof ScrollPane sp) {
                sp.setFitToWidth(true);
                sp.setFitToHeight(false);
            }

            BorderPane.setAlignment(view, javafx.geometry.Pos.TOP_LEFT);
            mainPane.setCenter(view);
            updateButtonStyles(btnProfile, btnDashboard, btnScholarships);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateButtonStyles(Button active, Button... inactive) {
        active.setStyle("-fx-background-color: #800000; -fx-background-radius: 10; -fx-text-fill: white; -fx-cursor: hand;");
        for (Button b : inactive) {
            if (b != null) {
                b.setStyle("-fx-background-color: transparent; -fx-text-fill: #495057; -fx-cursor: hand;");
            }
        }
    }

    private void loadView(String fxmlPath) {
        try {
            URL loc = getClass().getResource(fxmlPath);
            if (loc == null) {
                System.err.println("Resource not found: " + fxmlPath);
                return;
            }
            Parent view = FXMLLoader.load(loc);

            if (view instanceof ScrollPane sp) {
                sp.setFitToWidth(true);
                sp.setFitToHeight(false);
            }

            BorderPane.setAlignment(view, javafx.geometry.Pos.TOP_LEFT);
            mainPane.setCenter(view);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void configureDynamicTabs() {
        if ("SPONSOR".equalsIgnoreCase(currentUserRole)) {
            btnDynamicAction.setText("💰 Add Funds");
        } else if ("ADMIN".equalsIgnoreCase(currentUserRole)) { // Assuming "MANAGE SYSTEM" is for Admin roles
            btnDynamicAction.setText("⚙️ Manage System");
        } else {
            btnDynamicAction.setText("📄 My Applications");
        }
    }

    @FXML
    private void handleDynamicActionClick(MouseEvent event) {
        String fxmlPath = "";

        // 1. Determine which individual view path target to resolve
        if ("SPONSOR".equalsIgnoreCase(currentUserRole)) {
            fxmlPath = "/add-funds-view.fxml";
        } else if ("ADMIN".equalsIgnoreCase(currentUserRole)) {
            fxmlPath = "/manage-system-view.fxml";
        } else {
            fxmlPath = "/my-applications-view.fxml";
        }

        // 2. Load the view dynamically into your main central workspace container pane
        try {
            URL loc = getClass().getResource(fxmlPath);
            if (loc == null) {
                System.err.println("Target view FXML not found: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(loc);
            Parent view = loader.load();

            // 3. Optional: Pass your currentUserID to the target controller if they need it
            if ("SPONSOR".equalsIgnoreCase(currentUserRole)) {
//                AddFundsController ctrl = loader.getController();
//                ctrl.setUserID(currentUserID); // Pass data directly to your Add Funds handler
            } else if ("ADMIN".equalsIgnoreCase(currentUserRole)) {
//                ManageSystemController ctrl = loader.getController();
                // ctrl.setupAdminAccess(currentUserID);
            } else {
//                MyApplicationsController ctrl = loader.getController();
//                ctrl.setStudentUserID(currentUserID);
            }

            // 4. Clean up ScrollPane width matching
            if (view instanceof ScrollPane sp) {
                sp.setFitToWidth(true);
                sp.setFitToHeight(false);
            }

            BorderPane.setAlignment(view, javafx.geometry.Pos.TOP_LEFT);
            mainPane.setCenter(view); // Update dashboard workspace frame view area

            // Update button highlight layout styling rules
            updateButtonStyles(btnDynamicAction, btnDashboard, btnScholarships, btnProfile);

        } catch (IOException e) {
            System.err.println("Error rendering dynamic route selection view pane:");
            e.printStackTrace();
        }
    }


}