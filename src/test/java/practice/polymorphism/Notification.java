package practice.polymorphism;

public abstract class Notification {

    /*
    Your base class has no real behavior.
    It just defines a method that must be implemented by subclasses.
    Forces child classes to implement behavior
    Prevents meaningless “generic notification”
     */
    public abstract void notifyUser();

}
