package practice.encapsulation;

public class TestUser {

    public static void main(String[] args){
        User user = new User();
        user.setUsername("John Doe");
       if(user.setPassword("mypassword123")){
           System.out.println("Password set successfully.");
       } else {
           System.out.println("Password must be at least 8 characters long.");
       }
        System.out.println("Username: " + user.getUsername());
    }
}
