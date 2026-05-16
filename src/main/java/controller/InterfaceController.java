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
    @FXML private Label labelUserRole; // Unified: We will use this single label for the header role
    @FXML private BorderPane mainPane;
    @FXML private Button btnDashboard;
    @FXML private Button btnScholarships;
    @FXML private Button btnProfile;
    @FXML private Button btnDonate;
    @FXML private Button btnMyApplications;

    // Admin-only sidebar button
    @FXML private Button btnAdminPanel;

    private int currentUserID;
    private String currentUserRole;

    @FXML
    public void initialize() {
        // Leave central area with default "Select a tab to begin" layout state
        // until userData payload passes a specific role filter
    }

    public void setUserData(String name, int userID, String role) {
        this.currentUserID = userID;
        this.currentUserRole = role;

        // 1. Safely update user profile display headers
        if (name != null && !name.isEmpty()) {
            labelUserName.setText(name.toUpperCase());
        }

        if (labelUserRole != null && role != null) {
            String roleCap = role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase();
            labelUserRole.setText(roleCap);
        }

        // 2. Hide or show custom buttons dynamically based on user privilege levels
        configureSidebarForRole(role);

        // 3. Router logic: Load the correct inner operational center view
        if ("SPONSOR".equalsIgnoreCase(role)) {
            loadSponsorDashboard();
        } else if ("ADMIN".equalsIgnoreCase(role)) {
            loadAdminDashboard();
        } else {
            loadView("/student-dashboard.fxml");
        }
    }

    public void setUserData(String name) {
        if (name != null && !name.isEmpty()) {
            labelUserName.setText(name.toUpperCase());
        }
    }

    private void configureSidebarForRole(String role) {
        boolean isSponsor = "SPONSOR".equalsIgnoreCase(role);
        boolean isAdmin   = "ADMIN".equalsIgnoreCase(role);
        boolean isStudent = "STUDENT".equalsIgnoreCase(role);

        // Donate – sponsor only
        setVisible(btnDonate, isSponsor);

        // Scholarships & My Applications – student only
        setVisible(btnScholarships, isStudent);
        setVisible(btnMyApplications, isStudent);

        // Profile – student & sponsor only (admins manage users differently)
        setVisible(btnProfile, !isAdmin);

        // Admin Panel – admin only
        setVisible(btnAdminPanel, isAdmin);
    }

    private void setVisible(Button btn, boolean visible) {
        if (btn != null) {
            btn.setVisible(visible);
            btn.setManaged(visible);
        }
    }

    // ── Sidebar handlers ─────────────────────────────────────────────────────

    @FXML
    private void showHome(MouseEvent event) {
        if ("SPONSOR".equalsIgnoreCase(currentUserRole)) {
            loadSponsorDashboard();
        } else if ("ADMIN".equalsIgnoreCase(currentUserRole)) {
            loadAdminDashboard();
        } else {
            loadView("/student-dashboard.fxml");
        }
        updateButtonStyles(btnDashboard, btnScholarships, btnProfile, btnDonate, btnMyApplications, btnAdminPanel);
    }

    @FXML
    private void showScholarshipList(MouseEvent event) {
        try {
            URL loc = getClass().getResource("/scholarship-list.fxml");
            if (loc == null) { System.err.println("scholarship-list.fxml not found"); return; }
            FXMLLoader loader = new FXMLLoader(loc);
            Parent view = loader.load();

            ScholarshipListController ctrl = loader.getController();
            if (ctrl != null) ctrl.setStudentID(currentUserID);

            BorderPane.setAlignment(view, javafx.geometry.Pos.TOP_LEFT);
            mainPane.setCenter(view);
        } catch (IOException e) { e.printStackTrace(); }
        updateButtonStyles(btnScholarships, btnDashboard, btnProfile, btnDonate, btnMyApplications, btnAdminPanel);
    }

    @FXML
    private void showDonate(MouseEvent event) {
        loadSponsorDashboard();
        updateButtonStyles(btnDonate, btnDashboard, btnScholarships, btnProfile, btnMyApplications, btnAdminPanel);
    }

    @FXML
    private void showMyApplications(MouseEvent event) {
        loadStudentApplications();
        updateButtonStyles(btnMyApplications, btnDashboard, btnScholarships, btnProfile, btnDonate, btnAdminPanel);
    }

    @FXML
    private void showProfile(MouseEvent event) {
        try {
            String fxmlPath = "SPONSOR".equalsIgnoreCase(currentUserRole)
                    ? "/sponsor-profile.fxml"
                    : "/student-profile.fxml";

            URL loc = getClass().getResource(fxmlPath);
            if (loc == null) { System.err.println("Profile FXML not found: " + fxmlPath); return; }

            FXMLLoader loader = new FXMLLoader(loc);
            Parent view = loader.load();

            if ("SPONSOR".equalsIgnoreCase(currentUserRole)) {
                SponsorProfileController ctrl = loader.getController();
                if (ctrl != null) ctrl.setUserID(currentUserID);
            } else {
                StudentProfileController ctrl = loader.getController();
                if (ctrl != null) ctrl.setUserID(currentUserID);
            }

            if (view instanceof ScrollPane sp) { sp.setFitToWidth(true); sp.setFitToHeight(false); }
            BorderPane.setAlignment(view, javafx.geometry.Pos.TOP_LEFT);
            mainPane.setCenter(view);
            updateButtonStyles(btnProfile, btnDashboard, btnScholarships, btnDonate, btnMyApplications, btnAdminPanel);

        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void showAdminPanel(MouseEvent event) {
        loadAdminDashboard();
        updateButtonStyles(btnAdminPanel, btnDashboard, btnScholarships, btnProfile, btnDonate, btnMyApplications);
    }

    // ── Loaders ───────────────────────────────────────────────────────────────

    private void loadAdminDashboard() {
        try {
            URL loc = getClass().getResource("/admin-dashboard.fxml");
            if (loc == null) { System.err.println("admin-dashboard.fxml not found"); return; }
            FXMLLoader loader = new FXMLLoader(loc);
            Parent view = loader.load();

            if (view instanceof ScrollPane sp) { sp.setFitToWidth(true); sp.setFitToHeight(false); }
            BorderPane.setAlignment(view, javafx.geometry.Pos.TOP_LEFT);
            mainPane.setCenter(view);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadSponsorDashboard() {
        try {
            URL loc = getClass().getResource("/sponsor-dashboard.fxml");
            if (loc == null) { System.err.println("sponsor-dashboard.fxml not found"); return; }
            FXMLLoader loader = new FXMLLoader(loc);
            Parent view = loader.load();

            SponsorDashboardController ctrl = loader.getController();
            if (ctrl != null) ctrl.setSponsorUserID(currentUserID);

            if (view instanceof ScrollPane sp) { sp.setFitToWidth(true); sp.setFitToHeight(false); }
            BorderPane.setAlignment(view, javafx.geometry.Pos.TOP_LEFT);
            mainPane.setCenter(view);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadStudentApplications() {
        try {
            URL loc = getClass().getResource("/student-applications.fxml");
            if (loc == null) { System.err.println("student-applications.fxml not found"); return; }
            FXMLLoader loader = new FXMLLoader(loc);
            Parent view = loader.load();

            StudentApplicationsController ctrl = loader.getController();
            if (ctrl != null) ctrl.setStudentID(currentUserID);

            if (view instanceof ScrollPane sp) { sp.setFitToWidth(true); sp.setFitToHeight(false); }
            BorderPane.setAlignment(view, javafx.geometry.Pos.TOP_LEFT);
            mainPane.setCenter(view);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadView(String fxmlPath) {
        try {
            URL loc = getClass().getResource(fxmlPath);
            if (loc == null) { System.err.println("Resource not found: " + fxmlPath); return; }
            Parent view = FXMLLoader.load(loc);
            if (view instanceof ScrollPane sp) { sp.setFitToWidth(true); sp.setFitToHeight(false); }
            BorderPane.setAlignment(view, javafx.geometry.Pos.TOP_LEFT);
            mainPane.setCenter(view);
        } catch (IOException e) { e.printStackTrace(); }
    }

    // ── Style helpers ─────────────────────────────────────────────────────────

    private void updateButtonStyles(Button active, Button... inactive) {
        if (active != null)
            active.setStyle("-fx-background-color: #800000; -fx-background-radius: 10; -fx-text-fill: white; -fx-cursor: hand;");
        for (Button b : inactive)
            if (b != null)
                b.setStyle("-fx-background-color: transparent; -fx-text-fill: #495057; -fx-cursor: hand;");
    }

    @FXML
    private void handleLogout(MouseEvent event) {
        try {
            java.io.File session = new java.io.File("session.ser");
            if (session.exists()) session.delete();

            Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }
}