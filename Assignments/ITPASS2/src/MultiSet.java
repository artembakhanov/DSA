import java.util.Collection;

public interface MultiSet<T> {

    void add(T item);

    void add(T item, int occupancies);

    boolean contains(T element);

    int getCount(T item);

    int size();

    boolean remove(T item);

    boolean isEmpty();

    Collection<T> keys();
}
