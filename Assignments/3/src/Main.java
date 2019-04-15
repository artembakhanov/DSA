import javafx.util.Pair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Solution.todoList();
    }
}

/**
 * This class contains the solution for task 2.2 (TO_DO-list).
 * @author Artem Bahanov (BS18-03)
 * @version 1.0
 */
class Solution {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");

    /**
     * This is the solution for task 2.2.
     */
    public static void todoList() {
        Scanner scanner = new Scanner(System.in);
        /* I store all to_do lists in map, with date as a key and pair of heaps with name of tasks as a value */
        /* I need two copies of the same heap because heap becomes empty after merging or extracting all elements */
        HashMap<Date,
                Pair<MergeableHeapADT<Integer, String>, MergeableHeapADT<Integer, String>>
                > tasks = new HashMap<>();

        int N = scanner.nextInt();
        scanner.nextLine(); // kostyl for scanner

        for (int i = 0; i < N; i++) {
            String[] currentLine = scanner.nextLine().split(" ");
            if (currentLine[0].matches("DO")) {

                try {
                    Date date = sdf.parse(currentLine[1]);
                    Pair<MergeableHeapADT<Integer, String>, MergeableHeapADT<Integer, String>> task  = tasks.get(date);
                    if (task != null) {
                        task.getKey().removeMax();
                        task.getValue().removeMax();
                    }
                } catch (ParseException ignored) {}

            } else {

                try {
                    Date date = sdf.parse(currentLine[0]);
                    Pair<MergeableHeapADT<Integer, String>, MergeableHeapADT<Integer, String>> task  = tasks.get(date);
                    if (task == null) {
                        MergeableHeapADT<Integer, String> heap1 = new BinomialHeap<>();
                        MergeableHeapADT<Integer, String> heap2 = new BinomialHeap<>();
                        task = new Pair<>(heap1, heap2);
                        tasks.put(date, task);
                    }
                    tasks.get(date).getKey().insert(Integer.parseInt(currentLine[2]), currentLine[1]);
                    tasks.get(date).getValue().insert(Integer.parseInt(currentLine[2]), currentLine[1]);
                } catch (ParseException ignored) {}

            }
        }

        /* sorting TO_DO lists by date (in ascending order) */
        List<Map.Entry<
                Date,
                Pair<MergeableHeapADT<Integer, String>, MergeableHeapADT<Integer, String>>
                >> entries = new ArrayList<>(tasks.entrySet());
        entries.sort(Map.Entry.comparingByKey()); // sort by value

        MergeableHeapADT<Integer, String> combinedTodoList = new BinomialHeap<>();

        for (Map.Entry<Date, Pair<MergeableHeapADT<Integer, String>, MergeableHeapADT<Integer, String>>> entry: entries) {
            System.out.println("TODOList " + sdf.format(entry.getKey()));
            combinedTodoList.merge(entry.getValue().getKey()); /* merge 1st copy with combined TO_DO list */

            /* emptying 2nd heap while printing out all tasks */
            while (entry.getValue().getValue().max() != null) {
                System.out.println("\t" + entry.getValue().getValue().removeMax());
            }
        }

        System.out.println("TODOList");
        while (combinedTodoList.max() != null) {
            System.out.println("\t" + combinedTodoList.removeMax());
        }
    }
}



/**
 * Interface for Mergeable Heap
 * @param <K> the type of keys
 * @param <V> the type of elements stored in the heap
 * @author Artem Bahanov (BS18-03)
 * @version 1.0
 */
interface MergeableHeapADT<K extends Comparable<? super K>, V> {
    /**
     * Insert value V with key K
     */
    void insert(K k, V v);

    /**
     * Find value with maximum key from the heap
     * @return value with maximum key
     */
    V max();

    /**
     * Extract value with maximum key from the heap
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
 * This class is an implementation of Mergeable Heap ADT.
 * I used Binomial Heap, that was shown us on tutorial.
 * All methods are taken from the class slides.
 * @author Artem Bahanov (BS18-03)
 * @version 1.0
 */
class BinomialHeap<K extends Comparable<? super K>, V> implements MergeableHeapADT<K, V> {
    private BinomialHeapNode<K, V> head;

    /**
     * Default constructor
     */
    public BinomialHeap() {
        head = null;
    }

    /**
     * Constructor for insertions.
     * Used for creating temporary heaps with one element.
     * @param key key
     * @param value value
     */
    private BinomialHeap(K key, V value) {
        if (key == null) throw new IllegalArgumentException("Key must be non null value!");

        head = new BinomialHeapNode<>(key, value);
    }

    /**
     * Insert value V with key K
     *
     * O(log n) complexity proof.
     * Creating new heap that consists of 1 node is O(1).
     * Merging is O(log n). Then overall time complexity is O(log n). QED.
     *
     * @see #merge(MergeableHeapADT)
     * @param key key (priority)
     * @param value value
     */
    @Override
    public void insert(K key, V value) {
        if (key == null) throw new IllegalArgumentException("Key must be non null value!");

        BinomialHeap<K, V> tempHeap = new BinomialHeap<>(key, value);
        merge(tempHeap);
    }

    /**
     * Find value with maximum key from the binomial heap
     *
     * O(log n) complexity proof.
     * By definition of binomial heap all maximum(in OUR case) values are stored in roots of binomial trees,
     * on the first level (head -> sibling1 -> sibling2 -> ...). The worst case when the number of nodes is n = 2^k - 1,
     * that means that the heap contains of k trees (B_0, B_1, B_2, ..., B_(k-1)), k ~ log n. So we need to perform
     * linear search on list of roots, therefore time complexity is O(log n). QED.
     *
     * @return value with maximum key
     */
    @Override
    public V max() {
        Pair<BinomialHeapNode<K, V>, BinomialHeapNode<K, V>> max = maxNode();
        return max.getValue() == null ? null : max.getValue().getValue();
    }

    /**
     * Extract value with maximum key from the heap
     *
     * O(log n) complexity proof.
     * Reversing of roots for new heap is linear depending on number of roots.
     * The worst case for removing max is when it is only one tree in heap (before removing) and it contains n = 2^k
     * nodes. So the number of roots will be exactly k = log n. So reversing has O(log n) time complexity.
     * Then merging with heap will take O(log n) time {@link #merge(MergeableHeapADT)}.
     * Overall time complexity is O(log n). QED.
     *
     * @return value with maximum key
     */
    @Override
    public V removeMax() {

        Pair<BinomialHeapNode<K, V>, BinomialHeapNode<K, V>> max = maxNode(); /* root that is going to be deleted */
        if (max.getValue() == null) return null; /* no such root? */

        if (max.getKey() != null) {
            max.getKey().setSibling(max.getValue().getSibling());
        } else {
            head = max.getValue().getSibling();
        }


        BinomialHeap<K, V> tempHeap = new BinomialHeap<>(); /* temporary heap for storing children of deleted root */

        /* Reverse the order of children of max */
        BinomialHeapNode<K, V> childNode = max.getValue().getChild();
        BinomialHeapNode<K, V> siblingNode;
        BinomialHeapNode<K, V> prevNode = null;

        while (childNode != null) {
            childNode.setParent(null);
            siblingNode = childNode.getSibling();
            childNode.setSibling(prevNode);
            prevNode = childNode;
            childNode = siblingNode;
        }


        tempHeap.head = prevNode;
        merge(tempHeap);

        return max.getValue().getValue();
    }

    /**
     * Taken from slides of tutorial from week 9.
     *
     * O(log n) complexity proof.
     * First we need to merge roots. As it was proven {@link #max()} the worst case for number of roots is log n.
     * So time complexity of just merging roots without obeying the properties of binomial heap
     * {@link #mergeRoots(BinomialHeap)} is O(max(log n_1, log n_2)) = O(log n), where n = n_1 + n_2 (number of nodes
     * in final heap). Then we go through all roots to restore heap, which is also O(log n).
     * So overall time complexity is O(log n). QED.
     *
     * @param heap another heap, which will become empty after the procedure
     */
    @Override
    public void merge(MergeableHeapADT<K, V> heap) {
        mergeRoots((BinomialHeap<K, V>) heap); /* lists of roots is linked now */

        if (head == null)
            return;


        BinomialHeapNode<K, V> newHead = head;


        BinomialHeapNode<K, V> prevX = null;
        BinomialHeapNode<K, V> x = head;
        BinomialHeapNode<K, V> nextX = x.getSibling();

        while (nextX != null) {
            if (x.getDegree() != nextX.getDegree() ||
                    (nextX.getSibling() != null && nextX.getSibling().getDegree() == x.getDegree())) {
                prevX = x;
                x = nextX;
            } else {
                if (x.getKey().compareTo(nextX.getKey()) >= 0) {
                    x.setSibling(nextX.getSibling());
                    binomialLink(nextX, x);
                } else {
                    if (prevX == null) {
                        newHead = nextX;
                    } else {
                        prevX.setSibling(nextX);
                    }
                    binomialLink(x, nextX);
                    x = nextX;
                }
            }
            nextX = x.getSibling();
        }

        head = newHead;
        ((BinomialHeap<K, V>) heap).head = null;
    }

    /**
     * Return pair of node that has maximum key and previous node
     * @return pair {some node -> node with maximum key}
     */
    private Pair<BinomialHeapNode<K, V>, BinomialHeapNode<K, V>> maxNode (){
        if (head == null) /* if the heap is empty */
            return new Pair<>(null, null);
        BinomialHeapNode<K, V> max = head;
        BinomialHeapNode<K, V> prevMax = null;
        BinomialHeapNode<K, V> prev = null;
        BinomialHeapNode<K, V> current = head;

        while (current != null) {
            if (max.getKey().compareTo(current.getKey()) < 0) {
                max = current;
                prevMax = prev;
            }
            prev = current;
            current = current.getSibling();
        }

        return new Pair<>(prevMax, max);
    }

    /**
     * Links 2 trees.
     * Taken from slides of tutorial from week 9.
     * Time complexity O(1)
     */
    private void binomialLink(BinomialHeapNode<K, V> y, BinomialHeapNode<K, V> z) {
        y.setParent(z);
        y.setSibling(z.getChild());
        z.setChild(y);
        z.setDegree(z.getDegree() + 1);
    }

    /**
     * Merges the root lists in the sorted by degree (not increasing) order
     * Time complexity O(log n), where n - maximum of numbers of elements in heaps.
     */
    private void mergeRoots(BinomialHeap<K, V> heap) throws IllegalArgumentException{
        if (heap == null) throw new IllegalArgumentException("Another heap must not be null.");

        BinomialHeapNode<K, V> newHead = new BinomialHeapNode<>(); /* new head for THIS instance */
        BinomialHeapNode<K, V> current = newHead; /* current node for new list, used for quick access to last root */

        /* Nodes from current heap and another heap */
        BinomialHeapNode<K, V> thisNode = this.head;
        BinomialHeapNode<K, V> anotherNode = heap.head;

        /* Making list ordered */
        while (thisNode != null && anotherNode != null) {
            if (thisNode.getDegree() <= anotherNode.getDegree()) {
                current.setSibling(thisNode);
                thisNode = thisNode.getSibling();
            } else {
                current.setSibling(anotherNode);
                anotherNode = anotherNode.getSibling();
            }

            current = current.getSibling();
        }

        while (thisNode != null) {
            current.setSibling(thisNode);
            thisNode = thisNode.getSibling();
            current = current.getSibling();
        }

        while (anotherNode != null) {
            current.setSibling(anotherNode);
            anotherNode = anotherNode.getSibling();
            current = current.getSibling();
        }

        this.head = newHead.getSibling();
    }
}

/**
 * This class implements a single node for Binomial Heap.
 * @param <K> the type of keys
 * @param <V> the type of elements stored in the heap
 * @author Artem Bahanov (BS18-03)
 * @version 1.0
 */
class BinomialHeapNode<K extends Comparable<? super K>, V> {
    private K key; /* priority of the node */
    private V value; /* values */
    private int degree; /* number of descendants */
    private BinomialHeapNode<K, V> parent; /* pointer to parent node */
    private BinomialHeapNode<K, V> child; /* pointer to the most left child node */
    private BinomialHeapNode<K, V> sibling; /* pointer to the right sibling node */

    /**
     * Default constructor.
     */
    public BinomialHeapNode() {
        this.key = null;
        this.value = null;
        this.degree = 0;
        this.parent = null;
        this.child = null;
        this.sibling = null;
    }

    public BinomialHeapNode(K key, V value) {
        this.key = key;
        this.value = value;
        this.degree = 0;
        this.parent = null;
        this.child = null;
        this.sibling = null;
    }

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

    public BinomialHeapNode<K, V> getParent() {
        return parent;
    }

    public BinomialHeapNode<K, V> setParent(BinomialHeapNode<K, V> parent) {
        this.parent = parent;
        return this;
    }

    public BinomialHeapNode<K, V> getChild() {
        return child;
    }

    public BinomialHeapNode<K, V> setChild(BinomialHeapNode<K, V> child) {
        this.child = child;
        return this;
    }

    public BinomialHeapNode<K, V> getSibling() {
        return sibling;
    }

    public BinomialHeapNode<K, V> setSibling(BinomialHeapNode<K, V> sibling) {
        this.sibling = sibling;
        return this;
    }
}

/**
 * This class was used for testing {@link BinomialHeap}.
 * @author Artem Bahanov (BS18-03)
 * @version 1.0
 */
class BinomialHeapTester {
    /**
     * Run some tests. Correct output is written in comments.
     */
    public static void test() {
        MergeableHeapADT<Integer, String> heap1 = new BinomialHeap<>();
        MergeableHeapADT<Integer, String> heap2 = new BinomialHeap<>();

        /* check if two empty mergeable heaps merges correctly */
        heap1.merge(heap2);
        System.out.println("Heap1 max: " + heap1.max()); // Heap1 max: null
        System.out.println("Heap2 max: " + heap2.max()); // Heap2 max: null

        System.out.println("Heap1 removed max: " + heap1.removeMax()); // Heap1 removed max: null
        System.out.println("Heap2 removed max: " + heap2.removeMax()); // Heap2 removed max: null

        /* check if insertion, deletion and taking (extracting maximum) works */
        heap1.insert(9, "Value with key 9");
        heap1.insert(11, "Value with key 11");
        System.out.println("Heap1 max: " + heap1.max()); // Heap1 max: Value with key 11
        System.out.println("Heap1 removed max: " + heap1.removeMax()); // Heap1 removed max: Value with key 11
        System.out.println("Heap1 max: " + heap1.max()); // Heap1 max: Value with key 9
        System.out.println("Heap1 removed max: " + heap1.removeMax()); // Heap1 removed max: Value with key 9
        System.out.println("Heap1 removed max: " + heap1.removeMax()); // Heap1 removed max: null

        System.out.println("Heap2 max: " + heap2.max()); // Heap2 max: null


        heap1.insert(7, "Value with key 7");
        heap1.insert(10, "Value with key 10");
        heap1.insert(2, "Value with key 2");
        heap1.insert(10, "Value with key 10(1)");
        heap1.insert(-1, "Value with key -1");
        heap1.insert(15, "Value with key 15");

        heap2.insert(5, "Value with key 5");
        heap2.insert(7, "Value with key 7");
        heap2.insert(3, "Value with key 3");
        heap2.insert(1, "Value with key 1");

        heap2.merge(heap1);

        System.out.println("Heap1 removed max: " + heap1.removeMax()); // Heap1 removed max: null

        System.out.println("Heap2 removed max: " + heap2.removeMax()); // Heap2 removed max: Value with key 15
        System.out.println("Heap2 removed max: " + heap2.removeMax()); // Heap2 removed max: Value with key 10(1)
        System.out.println("Heap2 removed max: " + heap2.removeMax()); // Heap2 removed max: Value with key 10
        System.out.println("Heap2 removed max: " + heap2.removeMax()); // Heap2 removed max: Value with key 7
        System.out.println("Heap2 removed max: " + heap2.removeMax()); // Heap2 removed max: Value with key 7
        System.out.println("Heap2 removed max: " + heap2.removeMax()); // Heap2 removed max: Value with key 5
        System.out.println("Heap2 removed max: " + heap2.removeMax()); // Heap2 removed max: Value with key 3
        System.out.println("Heap2 removed max: " + heap2.removeMax()); // Heap2 removed max: Value with key 2
        System.out.println("Heap2 removed max: " + heap2.removeMax()); // Heap2 removed max: Value with key 1
        System.out.println("Heap2 removed max: " + heap2.removeMax()); // Heap2 removed max: Value with key -1
        System.out.println("Heap2 removed max: " + heap2.removeMax()); // Heap2 removed max: null
    }
}



