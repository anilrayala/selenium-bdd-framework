package practice.strings;

/*
    * Reverse a string in Java
    * Example: Input: "Hello World" Output: "dlroW olleH"
    * Approach:
        * 1. Create a StringBuilder to build the reversed string.
        * 2. Iterate through the original string from the end to the beginning.
        * 3. Append each character to the StringBuilder.
        * 4. Convert the StringBuilder to a String and return it.
 */
public class ReverseString {

    public static void main(String[] args) {
        String str = "Hello World";
        String reversed = reverseString(str);
        System.out.println("Original: " + str);
        System.out.println("Reversed: " + reversed);
    }

    public static String reverseString(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = s.length() - 1; i >= 0; i--) {
            sb.append(s.charAt(i));
        }
        return sb.toString();
    }

}
