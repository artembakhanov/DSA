import java.util.HashSet;
import java.util.Objects;

public class TA {
    private String name;
    private int id;
    private HashSet<String> courses;

    public TA(String name, int id) {
        this.name = name;
        this.id = id;
        this.courses = new HashSet<>();
    }

    public int getId() {
        return id;
    }

    public HashSet<String> getCourses() {
        return courses;
    }

    public String getName() {
        return name;
    }

    public TA setName(String name) {
        this.name = name;
        return this;
    }

    void addCourse(String course) {
        courses.add(course);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TA ta = (TA) o;
        return Objects.equals(name, ta.name) &&
                Objects.equals(courses, ta.courses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, courses);
    }
}
