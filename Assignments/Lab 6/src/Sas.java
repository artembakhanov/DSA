import java.util.Arrays;
import java.util.Scanner;

public class Sas {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        Bid[] bids = new Bid[n];
        for (int i = 0; i < n; i++) {
            int a = scanner.nextInt();
            int b = scanner.nextInt();

            scanner.nextLine();

            bids[i] = new Bid(i, a, b);


        }
        Arrays.sort(bids);

        for (int j = 0; j < n; j++) {
            System.out.println(bids[j].getNumber() + 1 + " ");
        }

    }

    public static void sort(Bid[] bids) {

    }

}

class Bid implements Comparable<Bid> {
    private int number;
    private int min;
    private int max;

    public Bid(int number, int min, int max) {
        this.number = number;
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public Bid setMin(int min) {
        this.min = min;
        return this;
    }

    public int getMax() {
        return max;
    }

    public Bid setMax(int max) {
        this.max = max;
        return this;
    }

    public int getNumber() {
        return number;
    }

    public Bid setNumber(int number) {
        this.number = number;
        return this;
    }

    @Override
    public int compareTo(Bid o) {
        return min == o.getMin() ? Integer.compare(max, o.getMax()) : -Integer.compare(min, o.getMin());
    }
}
