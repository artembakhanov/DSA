import java.util.LinkedList;

public class Main {

}

/**
 * Interface for Mergeable Heap
 * @param <K> the type of keys
 * @param <V> the type of elements stored in the heap
 */
interface MergeableHeapADT<K extends Comparable, V> {
    /**
     * Insert value V with key K
     */
    void insert(K k, V v);

    /**
     * Extract value with maximum key from the heap
     * @return value with maximum key
     */
    V max();

    /**
     * Extract and remove value with maximum key from the heap
     * @return value with maximum key
     */
    V removeMax();

    /**
     * Merge another heap with the present one, incorporating
     * all entries into the current one while emptying another heap
     * @param heap another heap
     */
    void merge(MergeableHeapADT<K, V> heap);
}


/**
 * This class is an impleme
 */
class BinomialHeap<K extends Comparable, V> implements MergeableHeapADT<K, V> {
    LinkedList<BinomialHeap<K, V>> roolList; /* list of roots */



    @Override
    public void insert(K k, V v) {

    }

    @Override
    public V max() {
        return null;
    }

    @Override
    public V removeMax() {
        return null;
    }

    @Override
    public void merge(MergeableHeapADT<K, V> heap) {

    }
}

/**
 * This class implements a single node for Binomial Heap.
 * @param <K> the type of keys
 * @param <V> the type of elements stored in the heap
 */
class BinomialHeapNode<K extends Comparable, V> {
    private K key; /* priority of the node */
    private V value; /* values */
    private int degree; /* number of descendants */
    private BinomialHeap<K, V> parent; /* pointer to parent node */
    private BinomialHeap<K, V> child; /* pointer to the most left child node */
    private BinomialHeap<K, V> sibling; /* pointer to the right sibling node */

    public K getKey() {
        return key;
    }

    public BinomialHeapNode<K, V> setKey(K key) {
        this.key = key;
        return this;
    }

    public V getValue() {
        return value;
    }

    public BinomialHeapNode<K, V> setValue(V value) {
        this.value = value;
        return this;
    }

    public int getDegree() {
        return degree;
    }

    public BinomialHeapNode<K, V> setDegree(int degree) {
        this.degree = degree;
        return this;
    }

    public BinomialHeap<K, V> getParent() {
        return parent;
    }

    public BinomialHeapNode<K, V> setParent(BinomialHeap<K, V> parent) {
        this.parent = parent;
        return this;
    }

    public BinomialHeap<K, V> getChild() {
        return child;
    }

    public BinomialHeapNode<K, V> setChild(BinomialHeap<K, V> child) {
        this.child = child;
        return this;
    }

    public BinomialHeap<K, V> getSibling() {
        return sibling;
    }

    public BinomialHeapNode<K, V> setSibling(BinomialHeap<K, V> sibling) {
        this.sibling = sibling;
        return this;
    }
}


