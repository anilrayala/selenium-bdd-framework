package practice.polymorphism;

public class TestNotification {
    public static void main(String[] args){
        Notification n;

        n = new EmailNotification();
        n.notifyUser();

        n = new SMSNotification();
        n.notifyUser();
    }
}
