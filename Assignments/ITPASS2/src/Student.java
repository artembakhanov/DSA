import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Student {
    private String name;
    private int id;
    private HashSet<String> courses;

    public Student(String name, int id) {
        this.name = name;
        this.id = id;
        courses = new HashSet<>();
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public HashSet<String> getCourses() {
        return courses;
    }

    public void addCourse(String course) {
        courses.add(course);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(name, student.name) &&
                Objects.equals(courses, student.courses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, courses);
    }
}
