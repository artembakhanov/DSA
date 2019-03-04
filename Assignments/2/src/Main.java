import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Solutions.task2_2();
    }


}

/**
 * This class is used for calculating difference between two strings.
 * This distance is called Damerau–Levenshtein distance because we are allowed to swap adjacent chars.
 * This is used for solution for all subtasks of task 2 from the assignment.
 * @see <a href="https://en.wikipedia.org/wiki/Damerau%E2%80%93Levenshtein_distance">"Damerau–Levenshtein distance" on Wikipedia</a>
 *
 * @author Artem Bahanov (BS18-03)
 * @version 1.0
 */
class DamerauLevenshteinDist {

    /**
     * Calculate the distance between 2 words.
     *
     *
     * @param a first string
     * @param b second string
     * @return the Damerau–Levenshtein distance between a and b
     */
    public static int estimate(String a, String b) {
        int m = a.length(), n = b.length();

        /*
           used for dynamic programming
           answer is stored in D[m][n]
         */
        int D[][] = new int[m + 1][n + 1];

        D[0][0] = 0;
        for (int i = 1; i <= m; i++) {
            D[i][0] = D[i - 1][0] + 1;
        }
        for (int i = 1; i <= n; i++) {
            D[0][i] = D[0][i - 1] + 1;
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) D[i][j] = D[i - 1][j - 1]; // 0 - replace cost (replace not needed here)
                else D[i][j] = D[i - 1][j - 1] + 1; // 1 - replace cost

                D[i][j] = min(D[i][j], D[i - 1][j] + 1, D[i][j - 1] + 1); // replace, delete and insert cost

                if (i > 1 && j > 1 && a.charAt(i - 1) == b.charAt(j - 2) && a.charAt(i - 2) == b.charAt(i - 1))
                    D[i][j] = min(D[i][j], D[i - 2][j - 2] + 1);
            }
        }

        return D[m][n];
    }

    /**
     * @param a number 1
     * @param b number 2
     * @param c number 3
     * @return the minimum of a, b and c
     */
    private static int min(int a, int b, int c) {
        return min(min(a, b), c);
    }

    /**
     * @param a number 1
     * @param b number 2
     * @return the minimum of a and b
     */
    private static int min(int a, int b) {
        return Math.min(a, b);
    }

}

/**
 * This class contains all solutions.
 *
 * @author Artem Bahanov (BS18-03)
 * @version 1.0
 */
class Solutions {

    /**
     * Solution for task 2.1 (Misspellings)
     */
    public static void task2_1() {
        Scanner scanner = new Scanner(System.in);

        int n = scanner.nextInt(); // number of words
        scanner.nextLine();

        for (int i = 0; i < n; i++) {
            String[] words = scanner.nextLine().split(" ");
            System.out.println(DamerauLevenshteinDist.estimate(words[0], words[1]));
        }
    }

    /**
     * Solution for task 2.2 (Correction suggestions)
     */
    public static void task2_2() {
        Scanner scanner = new Scanner(System.in);

        int n = scanner.nextInt(); // number of words in the dictionary
        scanner.nextLine();

        String[] dict = new String[n];
        int[] suggestions = new int[n]; // array that contains the indexes of words from dict.

        for (int i = 0; i < n; i++) {
            dict[i] = scanner.next();
        }

        Arrays.sort(dict); // lexicographic order

        String word = scanner.next();

        int sugNum = 0, curMin = n; // number of suggestions; current minimum number of suggestions

        for (int i = 0; i < n; i++) {
            int distance = DamerauLevenshteinDist.estimate(dict[i], word);
            if (distance == curMin) suggestions[sugNum++] = i; // if distance eq just add a suggestion
            else if (distance < curMin) { // if distance < minimum -> clear all previous suggestions; add current
                suggestions[(sugNum = 1) - 1] = i;
                curMin = distance;
            }
        }

        // print all suggestions
        for (int i = 0; i < sugNum; i++) {
            System.out.println(dict[suggestions[i]] + " ");
        }
    }

    public static void task2_3() {

    }

}
