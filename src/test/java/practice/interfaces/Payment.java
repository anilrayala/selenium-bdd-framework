package practice.interfaces;

public interface Payment {

    void pay(double amount);
}

/*
| Method Type       | Allowed |
| ----------------- | ------- |
| abstract          | ✔       |
| default           | ✔       |
| static            | ✔       |
| private (Java 9+) | ✔       |
 */

/*
Key Rules of Interface (VERY IMPORTANT)
🔹 Variables
    int x = 10;

Means:
    public static final int x = 10;

✔ Constants only
❌ No instance variables
 */

/*
Interface in Java – Deep Understanding
🔹 What is an Interface?
    An interface defines a contract.
    Tells WHAT a class must do
    Does NOT tell HOW (mostly)
    Achieves 100% abstraction (pre-Java 8)

🔹 Why Interfaces Exist (Interview Perspective)

    Interfaces solve:
        Multiple inheritance problem
        Loose coupling
        Standardization (contracts)

💡 Think: “Any class that implements this interface MUST follow the rules.”
 */

/*
How to Explain This in Interview

“Interface defines a contract. Different classes implement it in their own way,
    allowing runtime polymorphism and loose coupling.”
 */