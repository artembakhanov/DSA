import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Scanner;

public class StupidBag {
    public static void main(String[] args) {
        
        
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int w = scanner.nextInt();
        scanner.nextLine();
        ArrayList<Pair<Integer, Integer>> items = new ArrayList<>(n);
        ArrayList<Integer> solution;
        ArrayList<Integer> maxSolution = new ArrayList<>();
        int maxCost = 0;
        for (int i = 0; i < n; i++) {
            items.add(new Pair<>(scanner.nextInt(), null));
        }
        scanner.nextLine();

        for (int i = 0; i < n; i++) {
            items.set(i, new Pair<>(items.get(i).getKey(), scanner.nextInt()));
        }
        int maxN = (int) Math.pow(2, n);
        for (int i = 0; i < maxN; i++) {
            solution = new ArrayList<>();
            int curSize = 0;
            int curCost = 0;
            int mask = 1;
            for (int j = 0; j < n; j++) {
                if ((i & mask) != 0) {
                    solution.add(j + 1);
                    curSize += items.get(j).getKey();
                    curCost += items.get(j).getValue();
                }
                mask *= 2;
            }
            if (curSize <= w && curCost > maxCost) {
                maxSolution = (ArrayList<Integer>) solution.clone();
                maxCost = curCost;
            }

            
        }

        System.out.println(maxSolution.size());
        for (int i = 0; i < maxSolution.size(); i++) {
            System.out.print(maxSolution.get(i) + " ");
        }

    }

}
