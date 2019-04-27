import java.util.Objects;

public class Course {
    private String name;
    private int TAs;
    private int allowedStudents;
    private int id;

    public Course(String name, int id, int TAs, int allowedStudents) {
        this.name = name;
        this.TAs = TAs;
        this.allowedStudents = allowedStudents;
        this.id = id;
    }

    public Course(String name){
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getTAs() {
        return TAs;
    }

    public int getAllowedStudents() {
        return allowedStudents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(name, course.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
