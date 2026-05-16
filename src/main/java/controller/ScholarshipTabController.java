package main.java.controller;

import JDBC.mySQLConnection;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.sql.*;

public class ScholarshipTabController {
    @FXML private Button btnBack;
    @FXML private Button btnApply;
    @FXML private Label lblName;
    @FXML private Label lblSponsor;
    @FXML private Label lblAcademicReq;
    @FXML private Label lblLocation;
    @FXML private Label lblAward;
    @FXML private Label lblDeadline;
    @FXML private Label lblDescription;
    @FXML private Label lblProcess;
    @FXML private Label lblDocuments;
    @FXML private Label lblOtherReq;
    @FXML private Label lblStatus;

    private int currentScholarshipId;
    private int currentStudentID;   // set via DashboardController → ScholarshipListController chain
    private BorderPane shellPane;

    @FXML
    public void initialize() {
        btnBack.setOnAction(event -> handleBackAction());
        btnApply.setOnAction(event -> handleApply());
    }

    // Called from ScholarshipListController after loading this view
    public void setStudentID(int studentID) {
        this.currentStudentID = studentID;
        refreshApplyButton();
    }

    public void loadScholarshipData(BorderPane mainPane, Parent root, int scholarshipId) {
        this.shellPane = mainPane;
        this.currentScholarshipId = scholarshipId;

        new Thread(() -> {
            String query = "SELECT s.name, s.sponsor, s.gradeRequired, s.location, s.description, " +
                    "r.process_steps, r.award_details, r.deadline_date, r.documents_needed " +
                    "FROM scholarships s " +
                    "INNER JOIN scholarship_requirements r ON s.scholarshipID = r.scholarshipID " +
                    "WHERE s.scholarshipID = ?";

            try (Connection conn = mySQLConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(query)) {

                ps.setInt(1, scholarshipId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String name     = rs.getString("name");
                        String sponsor  = rs.getString("sponsor");
                        double grade    = rs.getDouble("gradeRequired");
                        String location = rs.getString("location");
                        String desc     = rs.getString("description");
                        String process  = rs.getString("process_steps");
                        String award    = rs.getString("award_details");
                        String deadline = rs.getString("deadline_date");
                        String docs     = rs.getString("documents_needed");

                        Platform.runLater(() -> {
                            lblName.setText(name);
                            lblSponsor.setText(sponsor);
                            lblLocation.setText(location + " (Local)");
                            lblAcademicReq.setText("• Academic: Min Grade " + grade + "%");
                            lblDescription.setText(desc != null ? desc : "No description provided.");
                            lblProcess.setText(process != null ? process : "Process steps not specified.");
                            lblAward.setText(award != null ? award : "Full Tuition");
                            lblDeadline.setText(deadline != null ? deadline : "N/A");
                            lblDocuments.setText(docs != null ? docs : "No specific documents listed.");
                            mainPane.setCenter(root);
                            refreshApplyButton();
                        });
                    } else {
                        System.err.println("⚠️ Warning: Requirement record missing for ID: " + scholarshipId);
                        Platform.runLater(() -> mainPane.setCenter(root));
                    }
                }
            } catch (SQLException e) {
                System.err.println("JDBC Error loading scholarship details: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    // ─── Apply button logic ───────────────────────────────────────────────────
    private void handleApply() {
        if (currentStudentID <= 0) {
            showAlert(Alert.AlertType.WARNING, "Not Logged In", "Could not determine your student account. Please log out and log in again.");
            return;
        }

        // Check if already applied
        if (hasAlreadyApplied()) {
            showAlert(Alert.AlertType.INFORMATION, "Already Applied", "You have already applied for this scholarship.");
            return;
        }

        // Confirm
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Application");
        confirm.setHeaderText("Apply for: " + lblName.getText());
        confirm.setContentText("Are you sure you want to submit your application for this scholarship?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                submitApplication();
            }
        });
    }

    private boolean hasAlreadyApplied() {
        String check = "SELECT COUNT(*) FROM applications WHERE studentID = ? AND scholarshipID = ?";
        try (Connection conn = mySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(check)) {
            ps.setInt(1, currentStudentID);
            ps.setInt(2, currentScholarshipId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("Error checking existing application: " + e.getMessage());
        }
        return false;
    }

    private void submitApplication() {
        String insert = "INSERT INTO applications (studentID, scholarshipID, status) VALUES (?, ?, 'Pending')";
        try (Connection conn = mySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setInt(1, currentStudentID);
            ps.setInt(2, currentScholarshipId);
            ps.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Application Submitted",
                    "Your application for \"" + lblName.getText() + "\" has been submitted successfully!\n\nStatus: Pending Review.");

            // Disable button to prevent double-apply
            btnApply.setText("✅ Applied");
            btnApply.setStyle("-fx-background-color: #28a745; -fx-background-radius: 10;");
            btnApply.setDisable(true);

        } catch (SQLException e) {
            System.err.println("Error submitting application: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Submission Failed", "An error occurred. Please try again.");
        }
    }

    /** Update Apply button appearance based on existing application */
    private void refreshApplyButton() {
        if (currentStudentID > 0 && currentScholarshipId > 0 && hasAlreadyApplied()) {
            btnApply.setText("✅ Applied");
            btnApply.setStyle("-fx-background-color: #28a745; -fx-background-radius: 10;");
            btnApply.setDisable(true);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void handleBackAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scholarship-list.fxml"));
            Parent root = loader.load();
            BorderPane mainPane = (BorderPane) btnBack.getScene().lookup("#mainPane");
            if (mainPane != null) mainPane.setCenter(root);
        } catch (Exception e) {
            System.err.println("Error heading back: Could not find scholarship-list.fxml");
            e.printStackTrace();
        }
    }
}
