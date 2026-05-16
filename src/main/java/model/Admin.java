package main.java.model;

public class Admin extends User {
    private static final long serialVersionUID = 3L;

    public Admin(int userID, String name) {
        super(userID, name, "ADMIN");
    }
}