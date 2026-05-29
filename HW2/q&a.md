## 1. String vs StringBuilder vs StringBuffer

### Interview Answer

In Java, `String` is immutable, which means once a `String` object is created, its value cannot be changed. Any operation like concatenation creates a new `String` object.

`StringBuilder` and `StringBuffer` are mutable, so they can modify the same object without creating many temporary objects.

The main difference is:

* `String`: immutable, thread-safe by immutability, but inefficient for frequent modifications
* `StringBuilder`: mutable, not thread-safe, faster
* `StringBuffer`: mutable, thread-safe because its methods are synchronized, but slower

In most single-threaded situations, I use `StringBuilder`. If multiple threads need to modify the same string object, I use `StringBuffer`, although in modern Java we usually prefer better concurrency designs.

### Code Example

```java
public class StringExample {
    public static void main(String[] args) {
        String s = "Hello";
        s += " World"; // creates a new String object
        System.out.println(s);

        StringBuilder sb = new StringBuilder("Hello");
        sb.append(" World"); // modifies the same object
        System.out.println(sb.toString());

        StringBuffer buffer = new StringBuffer("Hello");
        buffer.append(" World"); // synchronized, thread-safe
        System.out.println(buffer.toString());
    }
}
```

---

## 2. Comparator vs Comparable

### Interview Answer

`Comparable` is used when a class has a natural ordering. The class itself implements `Comparable` and overrides the `compareTo()` method.

`Comparator` is used when we want to define custom ordering outside the class. It is useful when we need multiple sorting rules.

For example, if a `Student` should naturally be sorted by ID, I would use `Comparable`. But if I want to sort students by name, GPA, or age depending on the situation, I would use `Comparator`.

### Code Example: Comparable

```java
class Student implements Comparable<Student> {
    int id;
    String name;

    Student(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int compareTo(Student other) {
        return this.id - other.id;
    }
}
```

### Code Example: Comparator

```java
import java.util.*;

class Student {
    int id;
    String name;

    Student(int id, String name) {
        this.id = id;
        this.name = name;
    }
}

public class ComparatorExample {
    public static void main(String[] args) {
        List<Student> students = new ArrayList<>();
        students.add(new Student(2, "Bob"));
        students.add(new Student(1, "Alice"));

        students.sort(Comparator.comparing(s -> s.name));

        for (Student s : students) {
            System.out.println(s.id + " " + s.name);
        }
    }
}
```

### When to Use Which

```text
Use Comparable when:
- The class has one natural default order
- Example: sort Employee by employeeId

Use Comparator when:
- You need multiple sorting strategies
- You cannot modify the original class
- Example: sort Employee by name, salary, or age
```

---

## 3. Overriding vs Overloading

### Interview Answer

Overloading means defining multiple methods with the same name but different parameter lists in the same class. It is resolved at compile time, so it is also called compile-time polymorphism.

Overriding means a subclass provides its own implementation of a method already defined in the parent class. It is resolved at runtime, so it is also called runtime polymorphism.

### Code Example: Overloading

```java
class Calculator {
    int add(int a, int b) {
        return a + b;
    }

    double add(double a, double b) {
        return a + b;
    }

    int add(int a, int b, int c) {
        return a + b + c;
    }
}
```

### Code Example: Overriding

```java
class Animal {
    void makeSound() {
        System.out.println("Animal sound");
    }
}

class Dog extends Animal {
    @Override
    void makeSound() {
        System.out.println("Bark");
    }
}
```

### Key Difference

```text
Overloading:
- Same method name
- Different parameters
- Same class or subclass
- Compile-time polymorphism

Overriding:
- Same method signature
- Parent-child relationship
- Runtime polymorphism
```

---

## 4. JRE vs JDK vs JVM

### Interview Answer

The JVM, JRE, and JDK are related but different.

The JVM, or Java Virtual Machine, runs Java bytecode. It provides platform independence because the same `.class` file can run on different operating systems as long as a JVM is available.

The JRE, or Java Runtime Environment, contains the JVM plus standard libraries needed to run Java applications.

The JDK, or Java Development Kit, contains the JRE plus development tools such as the Java compiler `javac`, debugger, and other tools.

So in simple terms:

```text
JDK = JRE + development tools
JRE = JVM + libraries
JVM = runs Java bytecode
```

### Example Flow

```text
.java source file
    ↓ javac compiler
.class bytecode
    ↓ JVM
machine code execution
```

---

## 5. Java 8 Basic Data Types

### Interview Answer

Java has 8 primitive data types:

| Type    |          Size | Example                |
| ------- | ------------: | ---------------------- |
| byte    |        1 byte | `byte b = 10;`         |
| short   |       2 bytes | `short s = 100;`       |
| int     |       4 bytes | `int i = 1000;`        |
| long    |       8 bytes | `long l = 10000L;`     |
| float   |       4 bytes | `float f = 3.14f;`     |
| double  |       8 bytes | `double d = 3.14;`     |
| char    |       2 bytes | `char c = 'A';`        |
| boolean | JVM-dependent | `boolean flag = true;` |

### Code Example

```java
public class PrimitiveExample {
    public static void main(String[] args) {
        byte b = 10;
        short s = 100;
        int i = 1000;
        long l = 10000L;
        float f = 3.14f;
        double d = 3.14159;
        char c = 'A';
        boolean flag = true;
    }
}
```

---

## 6. Primitive Type vs Reference Type

### Interview Answer

Primitive types store actual values directly. Examples include `int`, `double`, `char`, and `boolean`.

Reference types store references, or memory addresses, to objects. Examples include `String`, arrays, classes, interfaces, and collections.

Primitive types usually have better performance and cannot be `null`. Reference types can be `null` and have methods and fields.

### Code Example

```java
public class TypeExample {
    public static void main(String[] args) {
        int a = 10; // primitive type

        String str = "Hello"; // reference type

        int[] numbers = {1, 2, 3}; // reference type

        System.out.println(a);
        System.out.println(str.length());
        System.out.println(numbers[0]);
    }
}
```

### Key Difference

```text
Primitive type:
- Stores actual value
- Cannot be null
- Has no methods
- Example: int, double, boolean

Reference type:
- Stores reference to object
- Can be null
- Can have methods and fields
- Example: String, ArrayList, custom classes
```

---

## 7. How Does JVM Work?

### Interview Answer

The JVM allows Java to be platform-independent. Java source code is first compiled by `javac` into bytecode. The JVM loads this bytecode, verifies it, interprets it, and may also compile frequently used code into native machine code using the Just-In-Time compiler.

The main steps are:

1. Java source code is compiled into bytecode.
2. ClassLoader loads `.class` files into memory.
3. Bytecode Verifier checks code safety.
4. Execution Engine runs the bytecode.
5. JIT compiler optimizes frequently executed code.
6. Garbage Collector manages memory automatically.

### JVM Execution Flow

```text
Java Source Code (.java)
        ↓
Java Compiler (javac)
        ↓
Bytecode (.class)
        ↓
ClassLoader
        ↓
Bytecode Verifier
        ↓
Execution Engine
        ↓
Interpreter + JIT Compiler
        ↓
Native Machine Code
```

---

## 8. JVM Memory Data Model

### Interview Answer

The JVM memory model is divided into several runtime memory areas.

The main areas are:

* Method Area
* Heap
* Stack
* Program Counter Register
* Native Method Stack

The heap is shared by all threads and stores objects. The stack is private to each thread and stores method call frames, local variables, and references. The method area stores class metadata, static variables, and method information.

### Main JVM Memory Areas

```text
Heap:
- Stores objects and arrays
- Shared by all threads
- Managed by Garbage Collector

Stack:
- Stores method call frames
- Stores local variables and references
- One stack per thread

Method Area:
- Stores class metadata
- Stores static variables
- Stores method information

PC Register:
- Stores the address of the current instruction
- One per thread

Native Method Stack:
- Used for native methods written in languages like C/C++
```

### Code Example

```java
public class MemoryExample {
    static int staticValue = 100; // method area

    public static void main(String[] args) {
        int localValue = 10; // stack

        String name = new String("Alice"); // object in heap, reference in stack

        Person p = new Person(); // object in heap, reference in stack
    }
}

class Person {
    int age;
}
```

---

## 9. How Does Garbage Collection Work?

### Interview Answer

Garbage Collection, or GC, is the process by which the JVM automatically removes objects that are no longer reachable.

An object becomes eligible for garbage collection when there are no live references pointing to it.

The GC starts from GC Roots, such as local variables, static variables, and active threads. It finds all reachable objects. Objects that are not reachable are considered garbage and can be removed.

### Example

```java
public class GCExample {
    public static void main(String[] args) {
        Person p = new Person();
        p = null; // the Person object is now eligible for GC
    }
}

class Person {
}
```

### GC Basic Process

```text
1. Start from GC Roots
2. Mark reachable objects
3. Identify unreachable objects
4. Remove unreachable objects
5. Compact memory if needed
```

---

## 10. Young Generation, Old Generation, and Permanent Generation

### Interview Answer

In the traditional JVM heap model, the heap is divided into Young Generation and Old Generation.

The Young Generation stores newly created objects. Most objects die young, so minor GC happens frequently here.

The Old Generation stores objects that survive multiple garbage collections. Major GC or Full GC happens here and is usually more expensive.

Permanent Generation, or PermGen, existed before Java 8 and stored class metadata. In Java 8, PermGen was removed and replaced by Metaspace, which uses native memory instead of heap memory.

### Memory Layout

```text
Heap
├── Young Generation
│   ├── Eden
│   ├── Survivor 0
│   └── Survivor 1
│
└── Old Generation

Before Java 8:
PermGen stored class metadata.

Java 8 and later:
Metaspace replaced PermGen.
```

### Interview Tip

A strong answer is:

> Most new objects are allocated in Eden. If they survive minor GC, they move between Survivor spaces. After surviving enough GC cycles, they are promoted to the Old Generation.

---

## 11. Different Types of Garbage Collectors

### Interview Answer

Java provides different garbage collectors for different performance goals. Some focus on throughput, some focus on low pause time, and some are designed for large heaps.

### Common GC Types

```text
Serial GC:
- Single-threaded
- Simple
- Good for small applications

Parallel GC:
- Multi-threaded
- Focuses on high throughput
- Good for batch processing

CMS GC:
- Concurrent Mark-Sweep
- Tries to reduce pause time
- Deprecated in later Java versions

G1 GC:
- Garbage First GC
- Splits heap into regions
- Balances throughput and pause time
- Common default choice in modern Java

ZGC:
- Very low pause time
- Designed for large heaps

Shenandoah GC:
- Low pause time
- Performs more work concurrently
```

### Simple Comparison

| GC Type       | Main Goal            | Use Case                     |
| ------------- | -------------------- | ---------------------------- |
| Serial GC     | Simplicity           | Small apps                   |
| Parallel GC   | Throughput           | Batch jobs                   |
| CMS GC        | Lower pauses         | Older low-latency apps       |
| G1 GC         | Balanced performance | General server apps          |
| ZGC           | Very low pauses      | Large heap, low-latency apps |
| Shenandoah GC | Very low pauses      | Low-latency apps             |

---

## 12. Full Interview-Style Summary

### Sample Answer

Java is platform-independent because source code is compiled into bytecode, and bytecode runs on the JVM. The JVM loads classes through the ClassLoader, verifies bytecode for safety, and executes it using the interpreter and JIT compiler.

Memory in the JVM is divided into areas like heap, stack, method area, PC register, and native method stack. Objects are stored in the heap, while method calls and local variables are stored in the stack.

Garbage Collection automatically manages memory by removing unreachable objects. It starts from GC Roots, marks reachable objects, and removes the rest. The heap is usually divided into Young Generation and Old Generation. New objects are created in Eden, surviving objects move to Survivor spaces, and long-lived objects are promoted to Old Generation.

For string handling, `String` is immutable, while `StringBuilder` and `StringBuffer` are mutable. `StringBuilder` is faster but not thread-safe, while `StringBuffer` is synchronized and thread-safe.

For sorting, `Comparable` defines the natural order of a class, while `Comparator` defines external custom ordering. Method overloading happens at compile time with the same method name but different parameters, while overriding happens at runtime when a subclass provides a new implementation of a parent method.

---

## 13. Quick Cheat Sheet

```text
String:
Immutable

StringBuilder:
Mutable, fast, not thread-safe

StringBuffer:
Mutable, synchronized, thread-safe

Comparable:
Natural ordering, compareTo()

Comparator:
Custom ordering, compare()

Overloading:
Same method name, different parameters, compile-time

Overriding:
Same method signature, subclass changes parent behavior, runtime

JDK:
Tools + JRE

JRE:
JVM + libraries

JVM:
Runs bytecode

Primitive type:
Stores actual value

Reference type:
Stores object reference

Young Generation:
New objects

Old Generation:
Long-lived objects

PermGen:
Class metadata before Java 8

Metaspace:
Replacement for PermGen in Java 8+
```
