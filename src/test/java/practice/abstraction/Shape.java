package practice.abstraction;

abstract class Shape {

    /* An abstract method doesn't have a body */
    abstract void draw();

    /* A regular or concrete method with a body */
    void info(){
        System.out.println("This is a shape.");
    }

}
