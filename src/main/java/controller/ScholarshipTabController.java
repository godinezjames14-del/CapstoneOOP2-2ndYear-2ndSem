package main.java.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import main.java.model.Scholarship;

public class ScholarshipTabController {
    @FXML private Button btnBack;
    @FXML private Button btnApply;
    @FXML private Label lblName;
    @FXML private Label lblSponsor;
    @FXML private Label lblAcademicReq;
    @FXML private Label lblLocation;
    @FXML private Label lblAward;
    @FXML private Label lblDeadline;

    @FXML
    public void initialize() {
        // Set up the Back Button logic
        btnBack.setOnAction(event -> handleBackAction());

        // Set up the Apply Button logic
        btnApply.setOnAction(event -> {
            System.out.println("Applying for: " + lblName.getText());
            // Logic for application goes here
        });
    }

    public void loadScholarshipData(int scholarshipId) {
        // Insert Database Logic here
        // ------


        // Test code
        Scholarship scholarship = new Scholarship(scholarshipId, "Sample Scholarship", "Sample Sponsor", 85.0, "Cebu City");
        populateUI(scholarship);
    }

    private void populateUI(Scholarship s) {
        lblName.setText(s.getName());
        lblSponsor.setText(s.getSponsor());
        lblLocation.setText(s.getLocation() + " (Local)");
        lblAcademicReq.setText("• Academic: Min Grade " + s.getGradeRequired() + "%");
        lblAward.setText("Full Tuition");
        lblDeadline.setText("Oct 24, 2026");
    }

    private void handleBackAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scholarship-list-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (Exception e) {
            System.err.println("Error: Could not find scholarship-list-view.fxml");
            e.printStackTrace();
        }
    }
}