package main.java.controller;

import JDBC.mySQLConnection;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import main.java.model.Scholarship;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SponsorProfileController {

    @FXML private Label lblOrgName;
    @FXML private Label lblUserID;
    @FXML private Label lblActiveScholarships;
    @FXML private Label lblAvatarInitials;
    @FXML private Label lblStatusMessage;

    @FXML private TextField txtOrgName;
    @FXML private TextField txtEmail;
    @FXML private TextField txtLocation;
    @FXML private TextArea txtDescription;
    @FXML private PasswordField txtPassword;

    @FXML private TableView<Scholarship> tableScholarships;
    @FXML private TableColumn<Scholarship, String> colScholarshipName;
    @FXML private TableColumn<Scholarship, Double> colMinGrade;
    @FXML private TableColumn<Scholarship, String> colStatus;

    private int currentUserID;
    private String currentOrgName;
    private ObservableList<Scholarship> scholarshipList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colScholarshipName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        colMinGrade.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getGradeRequired()).asObject());
        colStatus.setCellValueFactory(data -> new SimpleStringProperty("Active"));
        tableScholarships.setItems(scholarshipList);
    }

    public void setUserID(int userID) {
        this.currentUserID = userID;
        loadProfileFromDB();
        loadScholarshipsFromDB();
    }

    private void loadProfileFromDB() {
        String query = "SELECT userID, name, email FROM users WHERE userID = ?";
        try (Connection conn = mySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, currentUserID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                currentOrgName = rs.getString("name");
                String email = rs.getString("email");
                int id = rs.getInt("userID");

                lblOrgName.setText(currentOrgName);
                lblUserID.setText("#" + id);
                txtOrgName.setText(currentOrgName);
                txtEmail.setText(email);

                String[] parts = currentOrgName.trim().split(" ");
                String initials = parts.length >= 2
                        ? "" + parts[0].charAt(0) + parts[1].charAt(0)
                        : "" + parts[0].charAt(0);
                lblAvatarInitials.setText(initials.toUpperCase());
            }

        } catch (Exception e) {
            System.err.println("Error loading sponsor profile:");
            e.printStackTrace();
        }
    }

    private void loadScholarshipsFromDB() {
        scholarshipList.clear();
        String query = "SELECT scholarshipID, name, sponsor, gradeRequired, location " +
                "FROM scholarships WHERE sponsor = ? AND status = 'Active'";
        try (Connection conn = mySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, currentOrgName != null ? currentOrgName : "");
            ResultSet rs = ps.executeQuery();

            int count = 0;
            while (rs.next()) {
                scholarshipList.add(new Scholarship(
                        rs.getInt("scholarshipID"),
                        rs.getString("name"),
                        rs.getString("sponsor"),
                        rs.getDouble("gradeRequired"),
                        rs.getString("location")
                ));
                count++;
            }
            lblActiveScholarships.setText(String.valueOf(count));

        } catch (Exception e) {
            System.err.println("Error loading scholarships for sponsor:");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSaveChanges(MouseEvent event) {
        String newName = txtOrgName.getText().trim();
        String newEmail = txtEmail.getText().trim();
        String newPassword = txtPassword.getText().trim();

        if (newName.isEmpty() || newEmail.isEmpty()) {
            showStatus("Organization name and email cannot be empty.", false);
            return;
        }

        try (Connection conn = mySQLConnection.getConnection()) {
            String updateQuery = newPassword.isEmpty()
                    ? "UPDATE users SET name = ?, email = ? WHERE userID = ?"
                    : "UPDATE users SET name = ?, email = ?, password = ? WHERE userID = ?";

            PreparedStatement ps = conn.prepareStatement(updateQuery);
            ps.setString(1, newName);
            ps.setString(2, newEmail);
            if (!newPassword.isEmpty()) {
                ps.setString(3, newPassword);
                ps.setInt(4, currentUserID);
            } else {
                ps.setInt(3, currentUserID);
            }

            int rows = ps.executeUpdate();
            if (rows > 0) {
                currentOrgName = newName;
                lblOrgName.setText(newName);
                txtPassword.clear();

                String[] parts = newName.trim().split(" ");
                String initials = parts.length >= 2
                        ? "" + parts[0].charAt(0) + parts[1].charAt(0)
                        : "" + parts[0].charAt(0);
                lblAvatarInitials.setText(initials.toUpperCase());

                showStatus("Profile updated successfully!", true);
                loadScholarshipsFromDB();
            }

        } catch (Exception e) {
            showStatus("Error saving profile. Please try again.", false);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel(MouseEvent event) {
        loadProfileFromDB();
        txtPassword.clear();
        txtDescription.clear();
        lblStatusMessage.setVisible(false);
    }

    @FXML
    private void handleAddScholarship(MouseEvent event) {
        showStatus("Add Scholarship feature coming soon.", true);
    }

    private void showStatus(String message, boolean success) {
        lblStatusMessage.setText(message);
        lblStatusMessage.setTextFill(Color.web(success ? "#27ae60" : "#e74c3c"));
        lblStatusMessage.setVisible(true);
    }
}