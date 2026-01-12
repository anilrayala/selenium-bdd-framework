package practice.polymorphism;

public class CreditCardPayment extends Payment{

    @Override
    void pay() {
        System.out.println("Payment made using Credit Card");
    }
}
