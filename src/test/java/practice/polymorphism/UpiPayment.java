package practice.polymorphism;

public class UpiPayment extends Payment{

    @Override
    void pay() {
        System.out.println("Payment made using UPI");
    }
}
