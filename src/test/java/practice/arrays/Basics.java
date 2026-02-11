package practice.arrays;

public class Basics {

    public static void main(String[] args) {

    /*
    An array is a data structure that can hold a fixed number of values of the same type.
     */
        int[] a = new int[5];
        a[0]= 10;
        a[1]= 20;
        a[2]= 30;
        a[3]= 40;
        a[4]= 50;

        for(int i : a){
            System.out.println(i);
        }

        int[][] matrix = new int[3][3];
        matrix[0][0] = 1;
        matrix[0][1] = 2;
        matrix[0][2] = 3;
        matrix[1][0] = 4;
        matrix[1][1] = 5;
        matrix[1][2] = 6;
        matrix[2][0] = 7;
        matrix[2][1] = 8;
        matrix[2][2] = 9;

        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[i].length; j++){
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }

    /*
    Enhanced for loop (for-each) for 2D array
    this is a more concise way to iterate over all elements in a 2D array without needing to manage index variables.
    It works because a 2D array in Java is essentially an array of arrays.
    The outer loop iterates over each row (which is itself an array), and the inner loop iterates over each element in that row.
    This approach is often cleaner and less error-prone than using traditional for loops with index variables, especially when you don't need to know the indices of the elements.
     */
        for(int[] row : matrix){
            for(int element : row){
                System.out.print(element + " ");
            }
            System.out.println();
        }
    }
}
