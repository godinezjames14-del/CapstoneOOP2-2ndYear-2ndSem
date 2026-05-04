package main.java.model;

public class Scholarship {
    private int id;
    private String name;
    private String sponsor;
    private double gradeRequired;
    private String location;

    public Scholarship(int id, String name, String sponsor, double gradeRequired, String location) {
        this.id = id;
        this.name = name;
        this.sponsor = sponsor;
        this.gradeRequired = gradeRequired;
        this.location = location;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getSponsor() { return sponsor; }
    public double getGradeRequired() { return gradeRequired; }
    public String getLocation() { return location; }
}