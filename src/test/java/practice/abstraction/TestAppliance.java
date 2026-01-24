package practice.abstraction;

public class TestAppliance {

    public static void main(String[] args){

        Appliance a = new Fan();
        a.plugIn();
        a.turnOn();

        Appliance b = new WashingMachine();
        b.plugIn();
        b.turnOn();
    }
}
