package practice.polymorphism;

public class TestPayment {

    public static void main(String[]args){
        Payment p;

        p = new CreditCardPayment();
        p.pay();

        p = new UpiPayment();
        p.pay();
    }

}
