import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            SolutionChecker solutionChecker = new SolutionChecker("names.txt", "grades.xls");
            solutionChecker.check(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
