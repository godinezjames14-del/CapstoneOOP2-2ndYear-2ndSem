package main.java.model;

import java.time.LocalDateTime;

public class Document {
    private int documentId;
    private String documentName;
    private String status;

    // Additional necessary attributes for system functionality
    private String documentType; // e.g., "Transcript", "Income Tax Return"
    private String filePath;     // Storage path on server
    private long fileSize;       // Size in bytes
    private String uploadDate;   // Timestamp of submission
    private int userId;          // Link to the Student
    private int applicationId;   // Link to the specific Application

    // Constructor
    public Document(int documentId, String documentName, String documentType, int userId) {
        this.documentId = documentId;
        this.documentName = documentName;
        this.documentType = documentType;
        this.userId = userId;
        this.status = "Pending"; // Default status
        this.uploadDate = LocalDateTime.now().toString();
    }

    public void uploadDocument() {
        System.out.println("Uploading " + this.documentName + " for User ID: " + this.userId);
    }

    public void deleteDocument() {
        System.out.println("Document ID " + this.documentId + " has been deleted.");
    }

    public void updateDocumentStatus(String newStatus) {
        this.status = newStatus;
        System.out.println("Document status updated to: " + this.status);
    }


    // --- Getters and Setters ---

    public int getDocumentId() { return documentId; }
    public void setDocumentId(int documentId) { this.documentId = documentId; }

    public String getDocumentName() { return documentName; }
    public void setDocumentName(String documentName) { this.documentName = documentName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public int getApplicationId() { return applicationId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + documentId +
                ", name='" + documentName + '\'' +
                ", type='" + documentType + '\'' +
                ", status='" + status + '\'' +
                ", uploaded='" + uploadDate + '\'' +
                '}';
    }
}