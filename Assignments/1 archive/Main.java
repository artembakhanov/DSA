import java.util.Comparator;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TournamentsTask.solve();
    }
}

/**
 * Class that is used for solving task 2.3 from Assignment #1 (DSA Course)
 * Solution is checked on codeforces.com (The submission number: 50036757)
 *
 * For task 2.1 check List, DynamicArrayList, and DoublyLinkedList
 * For task 2.2 check class Sorter
 * Tests for lists and sorting are in Tester
 *
 * Everything here is written by Artem Bahanov (Innopolis University student) in 2019 as a solution for Assignment #1 (DSA Course).
 *
 * @author Artem Bahanov (BS18-03)
 * @see List
 * @see DynamicArrayList
 * @see DoublyLinkedList
 * @see Sorter
 * @see Tester
 */
class TournamentsTask{
    public static void solve() {
        Scanner input = new Scanner(System.in);
        int tournamentNumber = input.nextInt(); // read number of tournaments
        input.nextLine();
        for (int i = 0; i < tournamentNumber; i++) {

            String tournamentName = input.nextLine(); // name of i-th tournament
            int teamNumber = input.nextInt(); // read number of teams
            input.nextLine();

            // creating list of teams
            List<Team> teams = new DynamicArrayList<>();
            List<String> teamNames = new DynamicArrayList<>();
            for (int j = 0; j < teamNumber; j++) {
                String teamName = input.nextLine();
                teams.addLast(new Team(teamName));
                teamNames.addLast(teamName);
            }

            // reading games of the tournament
            int gameNumber = input.nextInt();
            input.nextLine();
            for (int j = 0; j < gameNumber; j++) {
                String[] game = input.nextLine().split(":");

                // parse 1st team
                String team1Name = game[0].split("#")[0];
                Team team1 = null;
                int team1Goals = Integer.parseInt(game[0].split("#")[1]);

                // parse 2nd team
                String team2Name = game[1].split("#")[1];
                Team team2 = null;
                int team2Goals = Integer.parseInt(game[1].split("#")[0]);

                // find team in list of teams for updating data
                for (int k = 0; k < teamNumber; k++) {
                    if (team1Name.equals(teamNames.get(k))) team1 = teams.get(k);
                    else if (team2Name.equals(teamNames.get(k))) team2 = teams.get(k);
                }

                // update data about teams
                team1.addGame(team1Goals, team2Goals);
                team2.addGame(team2Goals, team1Goals);

            }
            Comparator<Team> comparator = new TeamPointsComparator()
                    .thenComparing(new TeamWinsComparator())
                    .thenComparing(new TeamGoalDifferenceComparator())
                    .thenComparing(new TeamNameComparator());
            Sorter.insertionSort(teams, comparator); // sort list of teams

            // output
            System.out.println(tournamentName);
            for (int j = 0; j < teamNumber; j++) {
                System.out.println((j + 1) + ") " + teams.get(j).teamInfo());
            }
            System.out.println();
        }
        input.close();
    }
}



/**
 * @param <T> Generic parameter
 * @author Artem Bahanov (BS18-03)
 * @version 1.0
 */
interface List<T> {

    /**
     * Check whether the list is empty
     *
     * @return <code>true</code> if list is empty;
     * <code>false</code> otherwise
     */
    boolean isEmpty();

    /**
     * Add an element at given position
     *
     * @param i Position where element should be placed
     * @param e An element
     * @throws IndexOutOfBoundsException If given position is wrong (less than 0 or more or equal than the size of list)
     */
    void add(int i, T e) throws IndexOutOfBoundsException;

    /**
     * Add an element to the start of the list
     *
     * @param e Element to be added
     */
    void addFirst(T e);

    /**
     * Add an element to the end of the list
     *
     * @param e Element to be added
     */
    void addLast(T e);

    /**
     * Delete an element if it is in the list (nearest to the first element)
     *
     * @param e Element to be deleted
     * @return Removed element
     */
    T remove(T e); /*Please read!!! I renamed this method to "remove" because there is a case when a client
                     uses a List with integers (int) it becomes undefined whether delete(E e) or delete(int i) is called
                     unless the client casts e to Integer him-/herself explicitly. */

    /**
     * Delete an element at given position
     *
     * @param i Position
     * @return Removed element
     */
    T delete(int i) throws IndexOutOfBoundsException;

    /**
     * Remove the first element from the list
     *
     * @return Removed element
     */
    T deleteFirst();

    /**
     * Remove the last element from the list
     *
     * @return Removed element
     */
    T deleteLast();

    /**
     * Replace element at given position with new element
     *
     * @param i Given position
     * @param e Given value
     * @throws IndexOutOfBoundsException If given position is wrong (less than 0 or more or equal than the size of list)
     */
    void set(int i, T e) throws IndexOutOfBoundsException;

    /**
     * Retrieve element at given position
     *
     * @param i Given position
     * @return Element that is at position i
     * @throws IndexOutOfBoundsException If given position is wrong (less than 0 or more or equal than the size of list)
     */
    T get(int i) throws IndexOutOfBoundsException;

    /**
     * Get size of list
     * @return Size of list
     */
    int size();
}

/* ----------------------------------------------
   #     Implementation of DynamicArrayList     #
   ----------------------------------------------*/

/**
 * Dynamic Array is one of the implementations of List ADT. It consists of an array, which contains all data.
 * Every time there is no space for new element capacity of the inner array is doubled
 *
 * @param <T> Generic parameter
 * @author Artem Bahanov (BS18-03)
 * @version 1.0
 */
class DynamicArrayList<T> implements List<T> {

    private int size; // number of elements stored in the list
    private int arraySize; // current size of inner container (array)
    private T[] array; // array for storing data; doubling every time there is no space


    /**
     * Default constructor
     */
    @SuppressWarnings("unchecked")
    public DynamicArrayList() {
        size = 0;
        arraySize = 1;
        array = (T[]) new Object[arraySize];
    }

    /**
     * Create list from array
     * @param array The given array
     */
    DynamicArrayList(T[] array) {
        size = array.length;
        arraySize = array.length;
        this.array = array.clone();
    }

    /**
     * Check whether the list is empty
     *
     * @return <code>true</code> if list is empty;
     * <code>false</code> otherwise
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
        if (i < 0 || i > size) throw new IndexOutOfBoundsException();

        checkSpaceOrIncrease();

        if (i < size)
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

    /**
     * Get size of list
     * @return Size of list
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Get string representation of the list
     *
     * @return String representation
     */
    @Override
    public String toString() {
        return "DynamicArrayList{" + getPrintedValues() + "}";
    }


    //***********************************************************
    //*                     PRIVATE METHODS                     *
    //***********************************************************

    // resize inner array by copying previous version to new bigger array
    private void resize() {
        T temp[] = this.array;
        this.arraySize *= 2;
        this.array = (T[]) new Object[arraySize];
        for (int i = 0; i < size; i++) {
            this.array[i] = temp[i];
        }
    }

    // check if index is in bounds
    private void checkIndex(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= this.size) throw new IndexOutOfBoundsException();
    }

    // check it there is free space for new element in list. If no resize
    private void checkSpaceOrIncrease() {
        if (this.size == this.arraySize) resize();
    }

    // shift elements right starting from given position. used for adding elements
    private void shiftElementsRight(int start) {
        for (int i = this.size; i > start; i--) { // 0 1 2 3 -> 0 1 1 2 3
            this.array[i] = this.array[i - 1];
        }
    }
    // shift elements left starting from given position. used for deleting elements
    private void shiftElementsLeft(int start) {
        for (int i = start; i < size - 1; i++) { // 0 1 2 3
            this.array[i] = this.array[i + 1];
        }
    }

    // find index of element with value e
    private int findIndex(T e) {
        for (int i = 0; i < this.size; i++) {
            if (e.equals(this.array[i])) return i;
        }
        return -1;
    }

    // get string with all elements
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
 * Doubly Linked List is one of the implementations of List ADT. It uses objects of class Node for storing data.
 * Each node has references to previous and next node and field value.
 * This implementation has fields head and tail of class Node that refer the first and the last nodes.
 *
 * @param <T> Generic parameter
 * @author Artem Bahanov (BS18-03)
 * @version 1.0
 */
class DoublyLinkedList<T> implements List<T> {
    private Node<T> head; // head of the list
    private Node<T> tail; // tail of the list
    private int size; // size

    /**
     * Default constructor
     */
    public DoublyLinkedList() {
        size = 0;
        head = null;
        tail = null;
    }

    /**
     * Create list from array
     * @param array The given array
     */
    public DoublyLinkedList(T[] array) {
        this();
        for (T t: array) {
            addLast(t);
        }
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
        if (i < 0 || i > size) throw new IndexOutOfBoundsException();
        if (i == 0) {
            addFirst(e);
            return;
        } else if (i == size) {
            addLast(e);
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
        if (size == 0) { // special case
            head = new Node<>(e); // create head (and a tail at the same time)
            tail = head;
        } else {
            tail.setNext((new Node<>(e)).setPrev(tail)); // next element of CURRENT tail becomes new node with value e and which links to CURRENT tail
            tail = tail.getNext(); // tail is updated
        }
        size++;
    }

    /**
     * Delete an element if it is in the list (nearest to the first element)
     *
     * @param e Element to be deleted
     * @return Removed element
     * @see #delete(int)
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
        next.setPrev(prev); // prev <- current -> next; got prev <-> next; current is deleted by garbage collector
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
        checkIndex(i); // check if index is wrong
        getNode(i).setValue(e); // get node and then change its value
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
        checkIndex(i); // check if index is wrong
        return getNode(i).getValue(); // get node and then retrieve its value
    }

    /**
     * Get size of list
     * @return Size of list
     */
    @Override
    public int size() {
        return size;
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

    // check if index is out of bounds
    private void checkIndex(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
    }

    // get node by index
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


/**
 * Node is used by DoublyLinkedList.
 * It contains references to previous and next elements and value of type T.
 *
 * @param <T> Generic parameter
 */
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


/* ----------------------------------------------
   #            Sorter for List ADT             #
   ----------------------------------------------*/


/**
 * This class contains methods that sort the given List (any of its implementations can be used).
 * All methods use insertion sorting algorithm.
 * For custom sorting use comparator
 *
 * @author Artem Bahanov (BS18-03)
 * @version 1.0
 */
final class Sorter {

    /**
     * Sort the given list using insertion sorting algorithm
     *
     * @param list List to be sorted. It must contain comparable elements.
     */
    public static <T extends Comparable<T>> void insertionSort(List<T> list) {
        Comparator<T> comparator = Comparator.comparing(t -> t); // create "default" comparator (t -> t return an element itself)

        insertionSort(list, comparator);
    }

    /**
     * Sort the given list by using the given comparator
     *
     * @param list List to be sorted
     * @param c    Comparator
     * @see Comparator
     */
    public static <T> void insertionSort(List<T> list, Comparator<? super T> c) {
        int size = list.size();

        // insertion sort starts
        for (int i = 1; i < size; i++) {
            T key = list.get(i); // key to be inserted
            int j = i - 1;
            T temp; // temporary var - current element to compare with key
            while (j >= 0 && c.compare(key, temp = list.get(j)) > 0) {
                list.set(j + 1, temp);
                j--;
            }
            list.set(j + 1, key);
        }
    }
}


/**
 * This class represents teams from task 2.3
 * It just stores the information about the team
 */
class Team {
    private String name;
    private int goalsScored = 0;
    private int goalsAgainst = 0;
    private int wins = 0;
    private int ties = 0;
    private int loses = 0;
    private int games = 0;


    public Team(String name) {
        this.name = name;
    }

    /**
     * Update information about team using info about game.
     *
     * @param goalsScored Number of goals got by this team
     * @param goalsAgainst Number of goals got by another team
     */
    public void addGame(int goalsScored, int goalsAgainst) {
        games++;
        this.goalsScored += goalsScored;
        this.goalsAgainst += goalsAgainst;
        if (goalsScored > goalsAgainst) wins++;
        else if (goalsScored == goalsAgainst) ties++;
        else loses++;
    }

    /**
     * @return Number of points
     */
    public int points() {
        return wins * 3 + ties;
    }

    /**
     * @return Number of wins
     */
    public int wins() {
        return wins;
    }

    /**
     * @return Difference between goals scored and against
     */
    public int goalDifference() {
        return goalsScored - goalsAgainst;
    }

    /**
     * @return Name of the team
     */
    public String name() {
        return name;
    }

    /**
     * @return Info about this team in format needed in task 2.3 & is used for output in task 2.3
     */
    public String teamInfo() {
        return String.format("%s %dp, %dg (%d-%d-%d), %dgd (%d-%d)",
                name, points(), games, wins, ties, loses, goalDifference(), goalsScored, goalsAgainst);
    }

    @Override
    public String toString() {
        return "Team{" +
                "name='" + name + '\'' +
                ", goalsScored=" + goalsScored +
                ", goalsAgainst=" + goalsAgainst +
                ", wins=" + wins +
                ", ties=" + ties +
                ", loses=" + loses +
                ", games=" + games +
                '}';
    }
}


/**
 * Comparator for comparing teams by number of points.
 *
 * @author Artem Bahanov (BS18-03)
 * @see Team
 */
class TeamPointsComparator implements Comparator<Team> {

    @Override
    public int compare(Team o1, Team o2) {
        return Integer.compare(o1.points(), o2.points());
    }
}


/**
 * Comparator for comparing teams by number of wins.
 *
 * @author Artem Bahanov (BS18-03)
 * @see Team
 */
class TeamWinsComparator implements Comparator<Team> {

    @Override
    public int compare(Team o1, Team o2) {
        return Integer.compare(o1.wins(), o2.wins());
    }
}


/**
 * Comparator for comparing teams by goal difference.
 *
 * @author Artem Bahanov (BS18-03)
 * @see Team
 * @see Team#goalDifference()
 */
class TeamGoalDifferenceComparator implements Comparator<Team> {

    @Override
    public int compare(Team o1, Team o2) {
        return Integer.compare(o1.goalDifference(), o2.goalDifference());
    }
}


/**
 * Comparator for comparing teams for sorting by names according case-insensitive lexicographic order.
 *
 * @author Artem Bahanov (BS18-03)
 * @see Team
 */
class TeamNameComparator implements Comparator<Team> {

    @Override
    public int compare(Team o1, Team o2) {
        return o1.name().toLowerCase().compareTo(o2.name().toLowerCase()); // toLowerCase is used here because of case-insensitivity
    }
}


/**
 * This class was used for fast testing of implemented data structures and algorithms.
 * After each line with println function there is a comment with expected result.
 */
class Tester {
    public static void testArrayedList() {
        Integer [] a = {2, 5, 1, 2, 0, -3, 4};
        List<Integer> list = new DynamicArrayList<>(a);
        System.out.println(list); // DynamicArrayList{2, 5, 1, 2, 0, -3, 4}

        list.addLast(100);
        System.out.println(list); // DynamicArrayList{2, 5, 1, 2, 0, -3, 4, 100}

        list.deleteFirst();
        System.out.println(list); // DynamicArrayList{5, 1, 2, 0, -3, 4, 100}

        list.add(0, 7);
        System.out.println(list); // DynamicArrayList{7, 5, 1, 2, 0, -3, 4, 100}

        System.out.println(list.size()); // 8

        System.out.println(list.isEmpty()); // false

        list.remove(5);
        System.out.println(list); // DynamicArrayList{7, 1, 2, 0, -3, 4, 100}

        // case when 2 same elements (7)
        list.addLast(7);
        list.remove(7);
        System.out.println(list); // DynamicArrayList{1, 2, 0, -3, 4, 100, 7}

        list.remove(99);
        System.out.println(list); // DynamicArrayList{1, 2, 0, -3, 4, 100, 7}

        System.out.println(list.delete(3)); // -3

        try {
            list.delete(6);
        } catch (IndexOutOfBoundsException e){
            System.out.println("Exception");
        } // Exception

        System.out.println(list.get(2)); // 0

        // sorting test
        Sorter.insertionSort(list);
        System.out.println(list); // DynamicArrayList{0, 1, 2, 4, 7, 100}

        // case when it is empty or almost empty
        list = new DynamicArrayList<>();
        System.out.println(list); // DynamicArrayList{}

        list.addFirst(-2);
        System.out.println(list); // DynamicArrayList{-2}

        Sorter.insertionSort(list);
        System.out.println(list); // // DynamicArrayList{-2}

        list.deleteLast();
        System.out.println(list); // DynamicArrayList{}

        list.deleteLast();
        System.out.println(list); // DynamicArrayList{}

        list.add(0, -2);
        list.set(0, 2);
        System.out.println(list); // DynamicArrayList{2}
    }

    public static void testLinkedList() {
        Integer [] a = {2, 5, 1, 2, 0, -3, 4};
        List<Integer> list = new DoublyLinkedList<>(a);
        System.out.println(list); // DoublyLinkedList{2, 5, 1, 2, 0, -3, 4}

        list.addLast(100);
        System.out.println(list); // DoublyLinkedList{2, 5, 1, 2, 0, -3, 4, 100}

        list.deleteFirst();
        System.out.println(list); // DoublyLinkedList{5, 1, 2, 0, -3, 4, 100}

        list.add(0, 7);
        System.out.println(list); // DoublyLinkedList{7, 5, 1, 2, 0, -3, 4, 100}

        System.out.println(list.size()); // 8

        System.out.println(list.isEmpty()); // false

        list.remove(5);
        System.out.println(list); // DoublyLinkedList{7, 1, 2, 0, -3, 4, 100}

        // case when 2 same elements (7)
        list.addLast(7);
        list.remove(7);
        System.out.println(list); // DoublyLinkedList{1, 2, 0, -3, 4, 100, 7}

        list.remove(99);
        System.out.println(list); // DoublyLinkedList{1, 2, 0, -3, 4, 100, 7}

        System.out.println(list.delete(3)); // -3

        try {
            list.delete(6);
        } catch (IndexOutOfBoundsException e){
            System.out.println("Exception");
        } // Exception

        System.out.println(list.get(2)); // 0

        // sorting test
        Sorter.insertionSort(list);
        System.out.println(list); // DoublyLinkedList{0, 1, 2, 4, 7, 100}

        // case when it is empty or almost empty
        list = new DoublyLinkedList<>();
        System.out.println(list); // DoublyLinkedList{}

        list.addFirst(-2);
        System.out.println(list); // DoublyLinkedList{-2}

        Sorter.insertionSort(list);
        System.out.println(list); // // DoublyLinkedList{-2}

        list.deleteLast();
        System.out.println(list); // DoublyLinkedList{}

        list.deleteLast();
        System.out.println(list); // DoublyLinkedList{}

        list.add(0, -2);
        list.set(0, 2);
        System.out.println(list); // DoublyLinkedList{2}
    }

    public static void testSort() {
        Integer[] a = {1, 2, -5, 4, 2, 0, -22, 8};
        List<Integer> list = new DynamicArrayList<>(a);
        Sorter.insertionSort(list);
        System.out.println(list); // DynamicArrayList{-22, -5, 0, 1, 2, 2, 4, 8}

        list = new DoublyLinkedList<>(a);
        Sorter.insertionSort(list);
        System.out.println(list); // DoublyLinkedList{-22, -5, 0, 1, 2, 2, 4, 8}

        a = new Integer[]{1, 1, 1, 1, 1, 1, 1};
        list = new DynamicArrayList<>(a);
        Sorter.insertionSort(list);
        System.out.println(list); // DynamicArrayList{1, 1, 1, 1, 1, 1, 1}

        list = new DoublyLinkedList<>(a);
        Sorter.insertionSort(list);
        System.out.println(list); // DoublyLinkedList{1, 1, 1, 1, 1, 1, 1}

        // comparable case
        DynamicArrayList<Person> people = new DynamicArrayList<>();
        people.addLast(new Person(2, 5));
        people.addLast(new Person(5, 4));
        people.addLast(new Person(-4, 4));

        Sorter.insertionSort(people);
        System.out.println(people); // DynamicArrayList{Person{age=-4, height=4}, Person{age=2, height=5}, Person{age=5, height=4}}

        // comparator case
        Comparator<Person> comparator= new PersonAgeComparator().thenComparing(new PersonHeightComparator());
        people = new DynamicArrayList<>();
        people.addLast(new Person(2, 5));
        people.addLast(new Person(5, 4));
        people.addLast(new Person(-4, 4));
        people.addLast(new Person(2, -3));

        Sorter.insertionSort(people, comparator);
        System.out.println(people); // DynamicArrayList{Person{age=-4, height=4}, Person{age=2, height=-3}, Person{age=2, height=5}, Person{age=5, height=4}}

    }

    // used for testing sorting
    static class Person implements Comparable<Person> {
        int age;
        int height;

        public Person(int age, int height) {
            this.age = age;
            this.height = height;
        }

        @Override
        public int compareTo(Person o) {
            return Integer.compare(age, o.age);
        }

        @Override
        public String toString() {
            return "Person{" +
                    "age=" + age +
                    ", height=" + height +
                    '}';
        }
    }

    static class PersonAgeComparator implements Comparator<Person> {

        @Override
        public int compare(Person o1, Person o2) {
            return Integer.compare(o1.age, o2.age);
        }
    }

    static class PersonHeightComparator implements Comparator<Person> {

        @Override
        public int compare(Person o1, Person o2) {
            return Integer.compare(o1.height, o2.height);
        }
    }
}