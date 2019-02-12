public class Main {
    public static void main(String[] args) {
 //       DynamicArray<Integer> a = new DynamicArray<>();
//        a.addLast(2);
//        a.addLast(3);
//        a.addLast(5);
//        a.addLast(3);
//        a.addLast(10);
//        System.out.println(a.toString());
//        a.remove((Integer) 3);
//        System.out.println(a.toString());
//        System.out.println(a.delete(2));
//        System.out.println(a.toString());
        DoublyLinkedList<Integer> aaa = new DoublyLinkedList<>();
        aaa.addLast(1);
        System.out.println(aaa.toString());
        aaa.addFirst(2);
        System.out.println(aaa.toString());
        aaa.add(1, 3);
        System.out.println(aaa.toString());
        aaa.addLast(4);
        System.out.println(aaa.toString());
        aaa.delete(0);
        System.out.println(aaa.toString());
        aaa.addLast(4);
        System.out.println(aaa.toString());
        aaa.addLast(5);
        System.out.println(aaa.toString());
        aaa.delete(1);
        System.out.println(aaa.toString());
        aaa.remove(4);
        System.out.println(aaa.toString());
        aaa.remove(4);
        System.out.println(aaa.toString());
        aaa.remove(5);
        System.out.println(aaa.toString());
        aaa.deleteFirst();
        System.out.println(aaa.toString());
        aaa.deleteFirst();
        System.out.println(aaa.toString());
        aaa.deleteFirst();
        System.out.println(aaa.toString());
        aaa.deleteFirst();
        System.out.println(aaa.toString());
        aaa.deleteFirst();
        System.out.println(aaa.toString());
        aaa.addLast(4);
        System.out.println(aaa.toString());
        aaa.addLast(99);
        System.out.println(aaa.toString());
        aaa.deleteLast();
        System.out.println(aaa.toString());

    }
}

/**
 * @author Artem Bahanov (BS18-03)
 * @version 1.0
 * @param <T> Generic parameter
 */
interface List<T> {

    boolean isEmpty();
    void add(int i, T e) throws IndexOutOfBoundsException;
    void addFirst(T e);
    void addLast(T e);
    T remove(T e); /*Please read!!! I renamed this method to "remove" because in case when a client
                     uses a List with integers (int) it becomes undefined whether delete(E e) or delete(int i) is called
                     unless the client casts e to Integer him-/herself explicitly. */
    T delete(int i) throws IndexOutOfBoundsException;
    T deleteFirst();
    T deleteLast();
    void set(int i, T e) throws IndexOutOfBoundsException;
    T get(int i) throws IndexOutOfBoundsException;
    public void swap(int a, int b) throws IndexOutOfBoundsException;

}


/* ----------------------------------------------
   #       Implementation of DynamicArray       #
   ----------------------------------------------*/


/**
 * @author Artem Bahanov (BS18-03)
 * @version 1.0
 * @param <T> Generic parameter
 */
class DynamicArray<T> implements List<T> {

    private int size; // number of elements stored in the list
    private int arraySize; // current size of inner container (array)
    private T[] array; // array for storing data; doubling every time there is no space


    /**
     * Default constructor
     */
    DynamicArray() {
        size = 0;
        arraySize = 1;
        array = (T[]) new Object[arraySize];
    }


    /**
     * Check whether the list is empty
     *
     * @return <code>true</code> if list is empty;
     *         <code>false</code> otherwise
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Add an element at given position
     *
     * @param i Position where element should be placed
     * @param e An element
     * @throws IndexOutOfBoundsException If given position is wrong (less than 0 or more or equal than the size of list)
     */
    @Override
    public void add(int i, T e) throws IndexOutOfBoundsException {
        checkIndex(i); // an exception is possible here

        checkSpaceOrIncrease();

        shiftElementsRight(i);
        this.array[i] = e;
        this.size++;
    }

    /**
     * Add an element to the start of the list
     *
     * @param e Element to be added
     */
    @Override
    public void addFirst(T e) {
        add(0, e);
    }

    /**
     * Add an element to the end of the list
     *
     * @param e Element to be added
     */
    @Override
    public void addLast(T e) {
        checkSpaceOrIncrease();

        this.array[size] = e;
        size++;
    }

    /**
     * Delete an element if it is in the list (nearest to the first element)
     *
     * @param e Element to be deleted
     * @return Removed element
     */
    @Override
    public T remove(T e) {
        int index = findIndex(e);
        if (index != -1) delete(index);
        return e;
    }

    /**
     * Delete an element at given position
     *
     * @param i Position
     * @return Removed element
     */
    @Override
    public T delete(int i) throws IndexOutOfBoundsException {
        checkIndex(i);

        T deleted = this.array[i];
        shiftElementsLeft(i);
        size--;
        return deleted;
    }

    /**
     * Remove the first element from the list
     *
     * @return Removed element
     */
    @Override
    public T deleteFirst() {
        try {
            return delete(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Remove the last element from the list
     *
     * @return Removed element
     */
    @Override
    public T deleteLast() {
        try {
            return delete(size - 1);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Replace element at given position with new element
     *
     * @param i Given position
     * @param e Given value
     * @throws IndexOutOfBoundsException If given position is wrong (less than 0 or more or equal than the size of list)
     */
    @Override
    public void set(int i, T e) throws IndexOutOfBoundsException {
        checkIndex(i);

        this.array[i] = e;
    }

    /**
     * Retrieve element at given position
     *
     * @param i Given position
     * @return Element that is at position i
     * @throws IndexOutOfBoundsException If given position is wrong (less than 0 or more or equal than the size of list)
     */
    @Override
    public T get(int i) throws IndexOutOfBoundsException {
        checkIndex(i);

        return this.array[i];
    }

    @Override
    public void swap(int a, int b) throws IndexOutOfBoundsException {
        checkIndex(a);
        checkIndex(b);
        T temp = get(a);
        set(a, get(b));
        set(b, temp);
    }

    /**
     * Get string representation of the list
     *
     * @return String representation
     */
    @Override
    public String toString() {
        return "DynamicArray{" + getPrintedValues()+ "}";
    }


    //***********************************************************
    //*                     PRIVATE METHODS                     *
    //***********************************************************

    private void resize() {
        T temp[] = this.array;
        this.arraySize *= 2;
        this.array = (T[]) new Object[arraySize];
        for (int i = 0; i < size; i++) {
            this.array[i] = temp[i];
        }
    }

    private void checkIndex(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= this.size) throw new IndexOutOfBoundsException();
    }

    private void checkSpaceOrIncrease() {
        if (this.size == this.arraySize) resize();
    }

    private void shiftElementsRight(int start) {
        for (int i = this.size; i > start; i--) { // 0 1 2 3
            this.array[i] = this.array[i-1];
        }
    }

    private void shiftElementsLeft(int start) {
        for (int i = start; i < size - 1; i++) { // 0 1 2 3
            this.array[i] = this.array[i+1];
        }
    }

    private int findIndex(T e) {
        for (int i = 0; i < this.size; i++) {
            if (e.equals(this.array[i])) return i;
        }
        return -1;
    }

    private String getPrintedValues() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < this.size; i++) {
            s.append(this.array[i].toString());
            if (i != this.size - 1) s.append(", ");
        }
        return s.toString();
    }
}


/* ----------------------------------------------
   #     Implementation of DoublyLinkedList     #
   ----------------------------------------------*/


/**
 * @author Artem Bahanov (BS18-03)
 * @version 1.0
 * @param <T> Generic parameter
 */
class DoublyLinkedList<T> implements List<T> {
    Node<T> head;
    Node<T> tail;
    int size;

    public DoublyLinkedList() {
        size = 0;
        head = null;
        tail = null;
    }

    /**
     * Check whether the list is empty
     *
     * @return <code>true</code> if list is empty; <code>false</code> otherwise
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Add an element at given position
     *
     * @param i Position where element should be placed
     * @param e An element
     * @throws IndexOutOfBoundsException If given position is wrong (less than 0 or more or equal than the size of list)
     */
    @Override
    public void add(int i, T e) throws IndexOutOfBoundsException {
        checkIndex(i);
        if (i == 0) {
            addFirst(e);
            return;
        }

        Node<T> current = getNode(i);
        Node<T> prev = current.getPrev();
        prev.setNext(new Node<T>(e).setNext(current).setPrev(prev));
        current.setPrev(prev.getNext());

        size++;
    }

    /**
     * Add an element to the start of the list
     *
     * @param e Element to be added
     */
    @Override
    public void addFirst(T e) {
        if (size == 0) {
            head = new Node<>(e);
            tail = head;
        } else {
            head.setPrev(new Node<T>(e).setNext(head));
            head = head.getPrev();
        }
        size++;
    }

    /**
     * Add an element to the end of the list
     *
     * @param e Element to be added
     */
    @Override
    public void addLast(T e) {
        if (size == 0) { //
            head = new Node<>(e);
            tail = head;
        } else {
            tail.setNext((new Node<>(e)).setPrev(tail));
            tail = tail.getNext();
        }
        size++;
    }

    /**
     * Delete an element if it is in the list (nearest to the first element)
     *
     * @param e Element to be deleted
     * @see #delete(int)
     * @return Removed element
     */
    @Override
    public T remove(T e) {
        Node<T> temp = null;
        Node<T> current = head;
        int i = 0;
        while (i < size && !current.getValue().equals(e)) { // go forward until it is out of list or the given value is found
            current = current.getNext();
            i++;
        }
        if (current != null && current.getValue().equals(e)) {
            return delete(i); // delete by index
        }
        return null;
    }

    /**
     * Delete an element at given position
     *
     * @param i Position
     * @return Removed element
     */
    @Override
    public T delete(int i) throws IndexOutOfBoundsException {
        checkIndex(i); // check if index is wrong

        if (i == 0) return deleteFirst(); // if head is supposed to be deleted
        else if (i == size - 1) return deleteLast(); // if tail; otherwise something between them (common case)
        Node<T> current = getNode(i); // prev <-> current <-> next
        Node<T> prev = current.getPrev();
        Node<T> next = current.getNext();

        prev.setNext(next); // prev <- current <-> next (but prev -> next)
        next.setPrev(prev); // prev <- current -> next; got prev <->; current is deleted by garbage collector
        size--; // decrement size
        return current.getValue();
    }

    /**
     * Remove the first element from the list
     *
     * @return Removed element
     */
    @Override
    public T deleteFirst() {
        if (size > 0) {
            Node<T> temp = head;
            if (size == 1) { // if head and tail are the same element
                head = null;
                tail = null;
            } else {
                head = head.getNext(); // head is moved forward
                head.setPrev(null);
            }
            size--;
            return temp != null ? temp.getValue() : null;
        }
        return null;
    }

    /**
     * Remove the last element from the list
     *
     * @return Removed element
     */
    @Override
    public T deleteLast() {
        if (size > 0) {
            if (size == 1) { // if head and tail are the same element
                tail = null;
                head = null;
            } else {
                tail = tail.getPrev(); // tail becomes the prev element of tail
                tail.setNext(null);
            }
            size--;
        }
        return null;
    }

    /**
     * Replace element at given position with new element
     *
     * @param i Given position
     * @param e Given value
     * @throws IndexOutOfBoundsException If given position is wrong (less than 0 or more or equal than the size of list)
     */
    @Override
    public void set(int i, T e) throws IndexOutOfBoundsException {
        checkIndex(i);
        getNode(i).setValue(e);
    }

    /**
     * Retrieve element at given position
     *
     * @param i Given position
     * @return Element that is at position i
     * @throws IndexOutOfBoundsException If given position is wrong (less than 0 or more or equal than the size of list)
     */
    @Override
    public T get(int i) throws IndexOutOfBoundsException {
        checkIndex(i);
        return getNode(i).getValue();
    }

    @Override
    public void swap(int a, int b) {

    }

    /**
     * Get string representation of the list
     *
     * @return String representation
     */
    @Override
    public String toString() {
        Node<T> current = head;
        if (current == null) return "DoublyLinkedList{}";
        StringBuilder s = new StringBuilder(current.getValue().toString() + (size > 1 ? ", " : ""));
        while (current.getNext() != null) {
            current = current.getNext();
            s.append(current.getValue().toString()).append(current.getNext() != null ? ", " : "");
        }
        return "DoublyLinkedList{" + s.toString() + "}";
    }

    //***********************************************************
    //*                     PRIVATE METHODS                     *
    //***********************************************************

    private void checkIndex(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
    }

    private Node<T> getNode(int i) {
        Node<T> current = head;
        for (int j = 0; j < i; j++) {
            current = current.getNext(); // find i-th element
        }
        return current;
    }
}


/* ----------------------------------------------
   # Implementation of Node of DoublyLinkedList #
   ----------------------------------------------*/


class Node<T> {
    private Node<T> prev = null;
    private Node<T> next = null;
    private T value;


    public Node(T value) {
        this.value = value;
    }


    public Node<T> getPrev() {
        return prev;
    }

    public Node<T> setPrev(Node<T> prev) {
        this.prev = prev;
        return this;
    }

    public Node<T> getNext() {
        return next;
    }

    public Node<T> setNext(Node<T> next) {
        this.next = next;
        return this;
    }

    public T getValue() {
        return value;
    }

    public Node<T> setValue(T value) {
        this.value = value;
        return this;
    }
}

