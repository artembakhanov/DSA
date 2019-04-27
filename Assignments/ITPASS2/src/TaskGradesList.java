import java.util.ArrayList;

public class TaskGradesList {
    public ArrayList<Integer> grades;

    public TaskGradesList(int size) {
        grades = new ArrayList<>(size);
        while (size--==0) {grades.add(0);}
    }
}
