package practice.inheritance;

public class Employee {

    private String name;
    private double salary;

    public String getName() {
        return name;
    }
    /*
    setters should be void and not return anything
     */
    public void setName(String name) {
        this.name = name;
    }
    public double getSalary() {
        return salary;
    }
    public void setSalary(double salary) {
        this.salary = salary;
    }

}
