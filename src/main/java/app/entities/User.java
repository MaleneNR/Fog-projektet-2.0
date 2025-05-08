package app.entities;

public class User {
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
}
