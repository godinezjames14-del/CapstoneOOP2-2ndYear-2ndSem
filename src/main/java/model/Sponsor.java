package main.java.model;

// Sponsor = Donor in the class diagram
public class Sponsor extends User {
    private static final long serialVersionUID = 3L;

    private String organizationName;
    private double totalDonated;
    private String donationType;

    // Full constructor
    public Sponsor(int userID, String name, int age, String gender, String email, String password,
                   String organizationName, double totalDonated, String donationType) {
        super(userID, name, age, gender, email, password, "SPONSOR");
        this.organizationName = organizationName;
        this.totalDonated = totalDonated;
        this.donationType = donationType;
    }

    // Minimal constructor (for login/session use)
    public Sponsor(int userID, String name, String email, String organizationName) {
        super(userID, name, 0, "", email, "", "SPONSOR");
        this.organizationName = organizationName;
        this.totalDonated = 0.0;
        this.donationType = "";
    }

    // Getters
    public String getOrganizationName() { return organizationName; }
    public double getTotalDonated() { return totalDonated; }
    public String getDonationType() { return donationType; }

    // Setters
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }
    public void setTotalDonated(double totalDonated) { this.totalDonated = totalDonated; }
    public void setDonationType(String donationType) { this.donationType = donationType; }

    // Methods from class diagram
    public void donate() {
        // TODO: implement donation logic
    }

    public void viewDonationHistory() {
        // TODO: implement view donation history logic
    }

    public void getTaxReceipt() {
        // TODO: implement tax receipt logic
    }

    public void viewImpactReport() {
        // TODO: implement impact report logic
    }

    @Override
    public String toString() {
        return "Sponsor{" +
                "userID=" + getUserID() +
                ", name='" + getName() + '\'' +
                ", organizationName='" + organizationName + '\'' +
                ", donationType='" + donationType + '\'' +
                ", totalDonated=" + totalDonated +
                '}';
    }
}