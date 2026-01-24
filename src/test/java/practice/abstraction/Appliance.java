package practice.abstraction;

public abstract class Appliance {

    public abstract void turnOn();

    public void plugIn(){
        System.out.println("Appliance is plugged in.");
    }
}
