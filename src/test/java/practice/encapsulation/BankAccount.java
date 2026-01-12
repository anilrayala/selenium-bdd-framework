package practice.encapsulation;

import lombok.Getter;

public class BankAccount {

    private double balance; // data hiding

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        } else {
            System.out.println("Deposit amount must be positive.");
        }
    }
    public double getBalance() {
        return balance;
    }

}
