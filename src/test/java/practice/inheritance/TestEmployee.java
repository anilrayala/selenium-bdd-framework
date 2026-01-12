package practice.inheritance;


public class TestEmployee {

    public static void main(String[] args) {
        Manager manager = new Manager();
        manager.setName("Alice");
        manager.setSalary(80000);
        manager.setBonus(15000);
        System.out.println("Manager Name: " + manager.getName());
        System.out.println("Base Salary: " + manager.getSalary());
        System.out.println("Bonus: " + manager.getBonus());
        System.out.println("Total Salary: " + manager.getTotalSalary());
    }
}
