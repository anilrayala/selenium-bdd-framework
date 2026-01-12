package practice.encapsulation;

class TestBankAccount {

    public static void main(String[] args){
        BankAccount account = new BankAccount();
        account.deposit(500);
        System.out.println("Current Balance: " + account.getBalance());
    }
}
