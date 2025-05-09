package app.entities;

import java.util.Objects;

public class User {
    private int userId;
    private String email;
    private String password;
    private int role;
    private String name;
    private int telefon;
    private String adresse;

    public User(String email, String password, int role, String name, String adresse, int telefon) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.telefon = telefon;
        this.name=name;
        this.adresse = adresse;


    }

    public User(int userId, String email, String password, int role) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTelefon() {
        return this.telefon;
    }

    public void setTelefon(int phonenumber) {
        this.telefon = phonenumber;
    }

    public String getAdresse() {
        return this.adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }



    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +

                ", role=" + role +
                ", name='" + name + '\'' +
                ", telefon=" + telefon +
                ", adresse='" + adresse + '\'' +
                '}';

    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;

        return getUserId() == user.getUserId() && getRole() == user.getRole() && getPhonenumber() == user.getPhonenumber() && getEmail().equals(user.getEmail()) && getPassword().equals(user.getPassword()) && getName().equals(user.getName()) && getAdresse().equals(user.getAdresse());
    }

    @Override
    public int hashCode() {
        int result = getUserId();
        result = 31 * result + getEmail().hashCode();
        result = 31 * result + getPassword().hashCode();
        result = 31 * result + getRole();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getPhonenumber();
        result = 31 * result + getAdresse().hashCode();
        return result;
    }
}
