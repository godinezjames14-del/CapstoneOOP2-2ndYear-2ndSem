package main.java.controller;

import JDBC.mySQLConnection;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.java.model.Document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StudentProfileController {

    @FXML private Label lblFullName;
    @FXML private Label lblUserID;
    @FXML private Label lblLRN;
    @FXML private Label lblAvatarInitials;
    @FXML private Label lblStatusMessage;



    @FXML private TextField txtFullName;
    @FXML private TextField txtEmail;
    @FXML private TextField txtLRN;
    @FXML private PasswordField txtPassword;

    @FXML private TableView<Document> tableDocuments;
    @FXML private TableColumn<Document, String> colDocName;
    @FXML private TableColumn<Document, String> colDocType;
    @FXML private TableColumn<Document, String> colDocStatus;

    @FXML private ComboBox<String> comboDocType;
    @FXML private Label lblSelectedFile;
    @FXML private Button btnUploadConfirm;

    private int currentUserID;
    private File selectedUploadFile;
    private ObservableList<Document> documentList = FXCollections.observableArrayList();

    private static final String UPLOAD_DIR = "uploads/documents/";

    @FXML
    public void initialize() {
        colDocName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDocumentName()));
        colDocType.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDocumentType()));
        colDocStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        tableDocuments.setItems(documentList);

        if (comboDocType != null) {
            comboDocType.getItems().addAll(
                    "Transcript of Records",
                    "Certificate of Enrollment",
                    "Income Tax Return",
                    "Birth Certificate",
                    "Barangay Certificate",
                    "Medical Certificate",
                    "Other"
            );
            comboDocType.getSelectionModel().selectFirst();
        }
    }

    public void setUserID(int userID) {
        this.currentUserID = userID;
        loadProfileFromDB();
        loadDocumentsFromDB();
    }

    private void loadProfileFromDB() {
        String query = "SELECT userID, name, email, lrn FROM users WHERE userID = ?";
        try (Connection conn = mySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, currentUserID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                String lrn = rs.getString("lrn");
                int id = rs.getInt("userID");

                lblFullName.setText(name);
                lblUserID.setText("#" + id);
                lblLRN.setText(lrn != null ? lrn : "—");
                txtFullName.setText(name);
                txtEmail.setText(email);
                txtLRN.setText(lrn != null ? lrn : "");

                String[] parts = name.trim().split(" ");
                String initials = parts.length >= 2
                        ? "" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)
                        : "" + parts[0].charAt(0);
                lblAvatarInitials.setText(initials.toUpperCase());
            }

        } catch (Exception e) {
            System.err.println("Error loading student profile:");
            e.printStackTrace();
        }
    }



    private void loadDocumentsFromDB() {
        documentList.clear();
        String query = "SELECT documentID, documentName, documentType, status FROM documents WHERE userID = ?";
        try (Connection conn = mySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, currentUserID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Document doc = new Document(
                        rs.getInt("documentID"),
                        rs.getString("documentName"),
                        rs.getString("documentType"),
                        currentUserID
                );
                doc.setStatus(rs.getString("status"));
                documentList.add(doc);
            }

        } catch (Exception e) {
            System.err.println("Error loading documents:");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBrowseFile(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Document");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Allowed Files", "*.pdf", "*.jpg", "*.jpeg", "*.png", "*.docx"),
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png"),
                new FileChooser.ExtensionFilter("Word Documents", "*.docx")
        );

        Stage stage = (Stage) lblSelectedFile.getScene().getWindow();
        selectedUploadFile = fileChooser.showOpenDialog(stage);

        if (selectedUploadFile != null) {
            lblSelectedFile.setText(selectedUploadFile.getName());
            lblSelectedFile.setTextFill(Color.web("#212529"));
        }
    }

    @FXML
    private void handleUploadDocument(MouseEvent event) {
        if (selectedUploadFile == null) {
            showStatus("Please select a file first.", false);
            return;
        }

        String docType = comboDocType.getValue();
        if (docType == null || docType.isEmpty()) {
            showStatus("Please select a document type.", false);
            return;
        }

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            Files.createDirectories(uploadPath);

            String fileName = "user" + currentUserID + "_" + System.currentTimeMillis() + "_" + selectedUploadFile.getName();
            Path destination = uploadPath.resolve(fileName);
            Files.copy(selectedUploadFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

            String query = "INSERT INTO documents (documentName, documentType, status, filePath, fileSize, userID) VALUES (?, ?, 'Pending', ?, ?, ?)";
            try (Connection conn = mySQLConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(query)) {

                ps.setString(1, selectedUploadFile.getName());
                ps.setString(2, docType);
                ps.setString(3, destination.toString());
                ps.setString(4, String.valueOf(selectedUploadFile.length()));
                ps.setInt(5, currentUserID);

                ps.executeUpdate();
            }

            selectedUploadFile = null;
            lblSelectedFile.setText("No file selected");
            lblSelectedFile.setTextFill(Color.web("#6C757D"));
            comboDocType.getSelectionModel().selectFirst();

            showStatus("Document uploaded successfully!", true);
            loadDocumentsFromDB();

        } catch (IOException e) {
            showStatus("Error saving file. Please try again.", false);
            e.printStackTrace();
        } catch (Exception e) {
            showStatus("Error uploading document. Please try again.", false);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSaveChanges(MouseEvent event) {
        String newName = txtFullName.getText().trim();
        String newEmail = txtEmail.getText().trim();
        String newLRN = txtLRN.getText().trim();
        String newPassword = txtPassword.getText().trim();

        if (newName.isEmpty() || newEmail.isEmpty()) {
            showStatus("Name and email cannot be empty.", false);
            return;
        }

        try (Connection conn = mySQLConnection.getConnection()) {
            String updateQuery = newPassword.isEmpty()
                    ? "UPDATE users SET name = ?, email = ?, lrn = ? WHERE userID = ?"
                    : "UPDATE users SET name = ?, email = ?, lrn = ?, password = ? WHERE userID = ?";

            PreparedStatement ps = conn.prepareStatement(updateQuery);
            ps.setString(1, newName);
            ps.setString(2, newEmail);
            ps.setString(3, newLRN);
            if (!newPassword.isEmpty()) {
                ps.setString(4, newPassword);
                ps.setInt(5, currentUserID);
            } else {
                ps.setInt(4, currentUserID);
            }

            int rows = ps.executeUpdate();
            if (rows > 0) {
                lblFullName.setText(newName);
                lblLRN.setText(newLRN.isEmpty() ? "—" : newLRN);
                txtPassword.clear();

                String[] parts = newName.trim().split(" ");
                String initials = parts.length >= 2
                        ? "" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)
                        : "" + parts[0].charAt(0);
                lblAvatarInitials.setText(initials.toUpperCase());

                showStatus("Profile updated successfully!", true);
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
        lblStatusMessage.setVisible(false);
    }

    private void showStatus(String message, boolean success) {
        lblStatusMessage.setText(message);
        lblStatusMessage.setTextFill(Color.web(success ? "#27ae60" : "#e74c3c"));
        lblStatusMessage.setVisible(true);
    }
}