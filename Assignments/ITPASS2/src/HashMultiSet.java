import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class HashMultiSet<T> implements MultiSet<T> {
    private Map<T, Integer> map = new HashMap<>();
	private int size;

    @Override
    public void add(T item) {
       add(item, 1);
    }

    @Override
    public void add(T item, int occurrences) {
        if (map.containsKey(item)) {
            map.put(item, map.get(item) + occurrences);
        } else  {
            map.put(item, occurrences);
        }
		size++;
    }

    @Override
    public boolean contains(T element) {
        return map.containsKey(element);
    }

    @Override
    public int getCount(T item) {
        return map.getOrDefault(item, 0);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean remove(T item) {
        if (map.containsKey(item)) {
            Integer quantity = map.get(item);
            if (quantity == 1) map.remove(item);
            else map.put(item, quantity - 1);
            size--;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Collection<T> keys() {
        return map.keySet();
    }
}
