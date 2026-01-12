package practice.inheritance;

public class Manager extends Employee{
    private double bonus;

    public double getBonus() {
        return bonus;
    }

    public void setBonus(double bonus) {
        this.bonus = bonus;
    }
    /*
    child class should add the behavior
     */
    public double getTotalSalary() {
        return getSalary() + bonus;
    }
}
