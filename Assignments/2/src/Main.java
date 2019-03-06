import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        Solutions.task2_3();
    }
}

/**
 * This class is used for calculating difference between two strings.
 * This distance is called Damerau–Levenshtein distance (Optimal string alignment distance) because we are allowed to swap adjacent chars.
 * This is used for solution for all subtasks of task 2 from the assignment.
 * Task 2.1 codeforces submission number: 50853226
 * Task 2.2 codeforces submission number: 50853832
 * Task 2.3 codeforces submission number: 50890388
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

                D[i][j] = min(D[i][j], D[i - 1][j] + 1, D[i][j - 1] + 1); // 1, 1, 1 - replace, delete and insert cost

                // check for swap
                if (i > 1 && j > 1 && a.charAt(i - 1) == b.charAt(j - 2) && a.charAt(i - 2) == b.charAt(j - 1))
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
 * For each task algorithm that estimates Damerau–Levenshtein distance between words is used.
 * @see DamerauLevenshteinDist
 *
 * @author Artem Bahanov (BS18-03)
 * @version 1.0
 */
class Solutions {

    /**
     * Solution for task 2.1 (Misspellings)
     * Codeforces submission number: 50853226
     *
     * Here I do not use special algorithm strategies and data structures.
     * Array of strings is used to store 2 words.
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
     * Codeforces submission number: 50853832
     *
     * For this solution I use array of strings (dictionary) that is sorted using Timsort algorithm
     * that is built in standard library. I used it since it is the best stable sorting algorithm.
     * For finding minimum I use "on-time" strategy: just check if current value is less than the current minimum value.
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
            System.out.print(dict[suggestions[i]] + (i != sugNum - 1 ? " " : ""));
        }
    }

    /**
     * Solution for task 2.3 (Text autocorrection)
     * Codeforces submission number: 50890388
     *
     * For this solution I use regular expressions because it is much easier to parse texts with them than with my own solution.
     * I use hashmap for storing number of occurrences of a particular word, the words is key, value - the number.
     * After the first line is fully read, hashmap is converted to arraylist of Map entries, then it is sorted
     * by value (number of occurrences) in reverse order using built-in sorting algorithm (Timsort).
     * For words of text I use arraylist, because it is dynamic (I do not know the exact number of words in the text).
     */
    public static void task2_3() {
        Scanner scanner = new Scanner(System.in);

        String keys[] = scanner.nextLine().split("[^a-z]+"); // split by any non word element. word is [a-z]+
        Map<String, Integer> wordsCount = new HashMap<>();

        // count all words in a hashmap
        // key - a word, value - number of repetitions
        for (String key: keys) {
            wordsCount.putIfAbsent(key, 0);
            wordsCount.put(key, wordsCount.get(key) + 1);
        }

        // sorted map of words (by frequency)
        // Map.Entry<String, Integer>wordsCount.entrySet().toArray();
        List<Map.Entry<String, Integer>> dict = new ArrayList<>(wordsCount.entrySet());
        dict.sort(Collections.reverseOrder(Map.Entry.comparingByValue())); // sort by value

        // read next line
        String text = scanner.nextLine();

        // divide the text to substrings.
        // substring is either [a-z]+ (words) or [^a-z]+ (punctuation or other symbols)
        ArrayList<String> words = new ArrayList<>();
        Matcher m = Pattern.compile("([a-z]+)|([^a-z]+)").matcher(text);
        while (m.find()) {
            words.add(m.group()); // find all matches
        }

        for (int i = 0; i < words.size(); i++) {
            // if it is not a word
            if (!words.get(i).matches("[a-z]+")) continue;

            int min = Integer.MAX_VALUE, minIndex = -1;
            for (int j = 0; j < dict.size(); j++) {
                int curDist;

                // if we found word with smaller distance
                if ((curDist = DamerauLevenshteinDist.estimate(words.get(i), dict.get(j).getKey())) < min) {
                    min = curDist;
                    minIndex = j;
                }
            }
            words.set(i, dict.get(minIndex).getKey()); // replace i-th word from text with suggested word

        }

        // print text (with saved punctuation)
        for (String word: words) {
            System.out.print(word);
        }
    }
}
