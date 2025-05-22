package app.entities;

public class User {
    private int userId;
    private String email;
    private String password;
    private int role;
    private String name;
    private String phoneNumber;
    private String address;

    public User(int userId, String email, String password, int role, String name, String phoneNumber, String address) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.role = role;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
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


    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phonenumber) {
        this.phoneNumber = phonenumber;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +

                ", role=" + role +
                ", name='" + name + '\'' +
                ", telefonnr.=" + phoneNumber +
                ", adresse='" + address + '\'' +
                '}';

    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;

        return getUserId() == user.getUserId() && getRole() == user.getRole() && getEmail().equals(user.getEmail()) && getPassword().equals(user.getPassword()) && getName().equals(user.getName()) && getPhoneNumber().equals(user.getPhoneNumber()) && getAddress().equals(user.getAddress());
    }

    @Override
    public int hashCode() {
        int result = getUserId();
        result = 31 * result + getEmail().hashCode();
        result = 31 * result + getPassword().hashCode();
        result = 31 * result + getRole();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getPhoneNumber().hashCode();
        result = 31 * result + getAddress().hashCode();
        return result;
    }
}


