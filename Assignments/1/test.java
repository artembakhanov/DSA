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