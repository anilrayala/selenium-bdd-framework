package practice.strings;

public class Basics {
    public static void main(String[] args) {
        String s1 = "Hello";
        String s2 = "Hello";
        String s3 = new String("Hello");

        System.out.println(s1 == s2); // true, because string literals are interned
        System.out.println(s1 == s3); // false, because s3 is a new object
        System.out.println(s1.equals(s3)); // true, because equals() compares content

        // String concatenation
        String s4 = s1 + " World";
        System.out.println(s4); // Hello World

        // String length
        System.out.println("Length of s4: " + s4.length()); // 11

        // Substring
        String s5 = s4.substring(0, 5);
        System.out.println(s5); // Hello

        // String immutability
        s1 = "Hi";
        System.out.println(s1); // Hi

        String str="Payment $1000 received";
        System.out.println(str.indexOf("P"));


    }
}
