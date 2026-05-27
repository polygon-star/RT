import java.util.*;

public class JavaDemo {
    public static void main(String[] args) {
        stringDemo();
        comparableComparatorDemo();
        overloadingOverridingDemo();
        primitiveReferenceDemo();
        gcDemo();
    }

    static void stringDemo() {
        String s = "hello";
        s += " world";

        StringBuilder sb = new StringBuilder("hello");
        sb.append(" world");

        StringBuffer sf = new StringBuffer("hello");
        sf.append(" world");

        System.out.println("String: " + s);
        System.out.println("StringBuilder: " + sb);
        System.out.println("StringBuffer: " + sf);
    }

    static void comparableComparatorDemo() {
        List<Student> students = new ArrayList<>();
        students.add(new Student("Tom", 20));
        students.add(new Student("Alice", 18));
        students.add(new Student("Bob", 22));

        Collections.sort(students); // Comparable: sort by age
        System.out.println("Sort by age: " + students);

        students.sort((a, b) -> a.name.compareTo(b.name)); // Comparator
        System.out.println("Sort by name: " + students);
    }

    static void overloadingOverridingDemo() {
        Calculator calc = new Calculator();
        System.out.println(calc.add(1, 2));
        System.out.println(calc.add(1.5, 2.5));

        Animal animal = new Dog();
        animal.speak();
    }

    static void primitiveReferenceDemo() {
        int x = 10;
        Student s = new Student("Xi", 23);

        System.out.println("Primitive int: " + x);
        System.out.println("Reference object: " + s);
    }

    static void gcDemo() {
        Student s = new Student("Garbage", 99);
        s = null;

        System.gc(); // requests GC, not guaranteed
        System.out.println("GC requested");
    }
}

class Student implements Comparable<Student> {
    String name;
    int age;

    Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public int compareTo(Student other) {
        return this.age - other.age;
    }

    @Override
    public String toString() {
        return name + "(" + age + ")";
    }
}

class Calculator {
    int add(int a, int b) {
        return a + b;
    }

    double add(double a, double b) {
        return a + b;
    }
}

class Animal {
    void speak() {
        System.out.println("Animal speaks");
    }
}

class Dog extends Animal {
    @Override
    void speak() {
        System.out.println("Dog barks");
    }
}