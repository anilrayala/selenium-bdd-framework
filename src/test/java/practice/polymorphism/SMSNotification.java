package practice.polymorphism;

public class SMSNotification extends Notification{

    @Override
    public void notifyUser(){
        System.out.println("SMS Notification");
    }
}
