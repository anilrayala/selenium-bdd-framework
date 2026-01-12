package practice.polymorphism;

public class EmailNotification extends Notification{

    @Override
    public void notifyUser(){
        System.out.println("Email Notification");
    }
}
