package practice.encapsulation;

public class User {

    private String username;
    private String password;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean setPassword(String password) {
        if (password.length() >= 8) {
            this.password = password;
            return true;
        } else {
            return false;
        }
    }
}
