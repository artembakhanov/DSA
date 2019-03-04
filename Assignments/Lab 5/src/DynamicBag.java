import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Scanner;

public class DynamicBag {
    static int A[][];
    static ArrayList<Integer> solution = new ArrayList<>();
    static ArrayList<Pair<Integer, Integer>> items;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int w = scanner.nextInt();
        scanner.nextLine();

        items = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            items.add(new Pair<>(scanner.nextInt(), null));
        }
        scanner.nextLine();

        for (int i = 0; i < n; i++) {
            items.set(i, new Pair<>(items.get(i).getKey(), scanner.nextInt()));
        }

        A = new int[n + 1][w + 1];
        for (int i = 0; i <= w; i++) {
            A[0][i] = 0;
        }
        for (int i = 0; i <= n; i++) {
            A[i][0] = 0;
        }

        for (int i = 1; i <= n ; i++) {
            for (int j = 1; j <= w; j++) {
                if (j >= items.get(i - 1).getKey()) {
                    A[i][j] = Math.max(A[i - 1][j], A[i - 1][j - items.get(i - 1).getKey()] + items.get(i - 1).getValue());
                } else {
                    A[i][j] = A[i - 1][j];
                }
            }
        }

        findAns(n, w);
        System.out.println(solution.size());
        for (int i = 0; i < solution.size(); i++) {
            System.out.print(solution.get(i) + " ");
        }

    }

    static void findAns(int i, int j) {
        if (A[i][j] == 0) return;
        if (A[i - 1][j] == A[i][j])
            findAns(i - 1, j);
        else {
            findAns(i - 1, j - items.get(i - 1).getKey());
            solution.add(i);
        }
    }
}
