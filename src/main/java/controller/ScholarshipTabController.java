package main.java.controller;

import JDBC.mySQLConnection;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    @FXML
    public void initialize() {
        btnBack.setOnAction(event -> handleBackAction());

        btnApply.setOnAction(event -> {
            System.out.println("Applying for: " + lblName.getText());
        });
    }

    public void loadScholarshipData(BorderPane mainPane, Parent root, int scholarshipId) {
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
                        String name = rs.getString("name");
                        String sponsor = rs.getString("sponsor");
                        double grade = rs.getDouble("gradeRequired");
                        String location = rs.getString("location");
                        String desc = rs.getString("description");
                        String process = rs.getString("process_steps");
                        String award = rs.getString("award_details");
                        String deadline = rs.getString("deadline_date");
                        String docs = rs.getString("documents_needed");

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

                            // FIXED: Injects layout into inner workspace only, preserving the sidebar frame shell
                            mainPane.setCenter(root);
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

    private void handleBackAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scholarship-list.fxml"));
            Parent root = loader.load();

            // Look up the shell BorderPane wrapper container frame structure dynamically
            BorderPane mainPane = (BorderPane) btnBack.getScene().lookup("#mainPane");
            if (mainPane != null) {
                // Drop the list container safely back into the center
                mainPane.setCenter(root);
            }
        } catch (Exception e) {
            System.err.println("Error heading back: Could not find scholarship-list.fxml");
            e.printStackTrace();
        }
    }
}