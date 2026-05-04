package main.java.model;

public class Student extends User {
    private static final long serialVersionUID = 2L;
    private String LRN;

    public Student(int userID, String name, String LRN) {
        super(userID, name, "STUDENT");
        this.LRN = LRN;
    }
}