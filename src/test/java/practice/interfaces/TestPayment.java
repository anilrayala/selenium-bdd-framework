package practice.interfaces;

public class TestPayment {

    public static void main(String[] args){
        Payment payment;

        payment = new CreditCardPayment();
        payment.pay(1500.00);

        payment = new UpiPayment();
        payment.pay(750.00);
    }
}

/*
✔ Runtime polymorphism
✔ Loose coupling
✔ Interface-driven design
 */

/*
1. The class is a simple test/demo with a main method that exercises polymorphism.
2. It declares a variable of the interface type Payment.
3. It first assigns an instance of CreditCardPayment to that variable and
    calls pay(1500.00).
4. Then it assigns an instance of UpiPayment and calls pay(750.00).
5. This demonstrates runtime (dynamic) binding: the pay implementation that
    runs depends on the actual object (CreditCardPayment or UpiPayment) even
    though the variable type is Payment.
6. For this to compile and run you need the Payment interface and
    the two implementing classes (CreditCardPayment, UpiPayment) in the same package
    or importable. The pay method signature must match (e.g., void pay(double amount) or similar).
7. Typical use: verify different payment behaviors (logging, validation, different
    processing) without changing the test code.
 */
