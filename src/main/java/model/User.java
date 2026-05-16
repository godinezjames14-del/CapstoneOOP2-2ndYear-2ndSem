package main.java.model;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private int userID;
    private String name;
    private String role;

    public User(int userID, String name, String role) {
        this.userID = userID;
        this.name = name;
        this.role = role;
    }

    public int getUserID() { return userID; }
    public String getName() { return name; }
    public String getRole() { return role; }
}