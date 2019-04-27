import java.util.HashSet;
import java.util.Objects;

public class Professor {
    private String name;
    private int id;
    private HashSet<String> courses;

    public Professor(String name, int id) {
        this.name = name;
        this.id = id;
        this.courses = new HashSet<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Professor setName(String name) {
        this.name = name;
        return this;
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
        Professor professor = (Professor) o;
        return Objects.equals(name, professor.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
