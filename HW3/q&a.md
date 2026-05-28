# Homework 3 Q&A

# 1. Java Access Modifiers / Scope

## Access Modifier Table

| Modifier                  | Same Class | Same Package | Subclass (Different Package) | Other Package |
| ------------------------- |------------|--------------|------------------------------|---------------|
| public                    | Y          | Y            | Y                            | Y             |
| private                   | Y          | N            | N                            | N             |
| protected                 | Y          | Y            | Y                            | N             |
| default (package-private) | Y          | Y            | N                            | N             |

## Example

```java
public class AccessDemo {
    public String publicName = "public";
    private String privateName = "private";
    protected String protectedName = "protected";
    String defaultName = "default";

    public void show() {
        System.out.println(privateName);
    }
}
```

## Script

Java provides four access modifiers:

* `public` → accessible everywhere.
* `private` → accessible only within the same class.
* `protected` → accessible within the package and by subclasses.
* default/package-private → accessible only within the same package.

---

# 2. What is Static Scope?

A static member belongs to the class rather than an object instance.

## Example

```java
public class StaticDemo {

    static int count = 0;

    public StaticDemo() {
        count++;
    }

    public static void main(String[] args) {

        new StaticDemo();
        new StaticDemo();

        System.out.println(StaticDemo.count);
    }
}
```

## Output

```text
2
```

## Script

Static variables and methods belong to the class itself and are shared among all objects. They can be accessed using the class name without creating an object.

---

# 3. How Does ClassLoader Work?

Java loads classes dynamically using ClassLoaders.

## ClassLoader Hierarchy

```text
Bootstrap ClassLoader
        ↓
Platform ClassLoader
        ↓
Application ClassLoader
```

## Loading Process

1. Load class bytecode (.class file)
2. Verify bytecode
3. Prepare memory
4. Resolve references
5. Initialize static variables and blocks

## Script

The ClassLoader loads classes into JVM memory when needed. Java uses parent delegation, meaning a child loader asks its parent to load the class first before attempting to load it itself.

---

# 4. Checked vs Unchecked Exceptions

## Comparison

| Feature                 | Checked Exception | Unchecked Exception  |
| ----------------------- | ----------------- | -------------------- |
| Checked at compile time | ✅                 | ❌                    |
| Must handle or declare  | ✅                 | ❌                    |
| Parent class            | Exception         | RuntimeException     |
| Example                 | IOException       | NullPointerException |

## Example

```java
import java.io.FileReader;
import java.io.IOException;

public class ExceptionDemo {

    public static void main(String[] args) {

        try {
            FileReader reader = new FileReader("test.txt");
        } catch (IOException e) {
            System.out.println("File not found");
        }

        String name = null;
        // name.length(); // NullPointerException
    }
}
```

## Script

Checked exceptions are verified by the compiler and must be handled. Unchecked exceptions occur during runtime and typically indicate programming errors.

---

# 5. Difference Between final, finally, and finalize

## Comparison

| Keyword    | Purpose                                        |
| ---------- | ---------------------------------------------- |
| final      | Prevent modification                           |
| finally    | Cleanup block in exception handling            |
| finalize() | Garbage collection cleanup method (deprecated) |

## Example

```java
public class FinalDemo {

    public static void main(String[] args) {

        final int x = 10;

        try {
            System.out.println("Try");
        } finally {
            System.out.println("Finally");
        }
    }
}
```

## Script

* `final` prevents inheritance, overriding, or reassignment.
* `finally` executes after try/catch for cleanup.
* `finalize()` was called by the garbage collector before object destruction but is deprecated.

---

# 6. Try-With-Resources

Automatically closes resources that implement `AutoCloseable`.

## Example

```java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TryWithResourcesDemo {

    public static void main(String[] args) {

        try (BufferedReader br =
                     new BufferedReader(new FileReader("data.txt"))) {

            System.out.println(br.readLine());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

## Script

Try-with-resources automatically closes resources, making code safer and cleaner than traditional try-finally blocks.

---

# 7. What is RuntimeException?

A RuntimeException is an unchecked exception that occurs during execution.

## Example

```java
public class RuntimeExceptionDemo {

    public static void main(String[] args) {

        int[] nums = {1, 2, 3};

        System.out.println(nums[5]);
    }
}
```

## Exception

```text
ArrayIndexOutOfBoundsException
```

## Script

RuntimeExceptions occur at runtime and are not checked by the compiler. Examples include NullPointerException and ArrayIndexOutOfBoundsException.

---

# 8. NoClassDefFoundError vs ClassNotFoundException

## Comparison

| Feature      | ClassNotFoundException | NoClassDefFoundError           |
| ------------ | ---------------------- | ------------------------------ |
| Type         | Exception              | Error                          |
| Compile Time | Class absent           | Class existed                  |
| Runtime      | Dynamic loading failed | Class missing during execution |

## Example

```java
public class ClassNotFoundDemo {

    public static void main(String[] args) {

        try {
            Class.forName("com.example.Test");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
```

## Script

ClassNotFoundException occurs when loading a class dynamically. NoClassDefFoundError occurs when a class was present during compilation but missing at runtime.

---

# 9. Why Clean Up Resources in Finally?

Resources such as files, sockets, and database connections consume system resources.

## Example

```java
import java.io.FileReader;
import java.io.IOException;

public class CleanupDemo {

    public static void main(String[] args) {

        FileReader reader = null;

        try {
            reader = new FileReader("data.txt");

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
```

## Script

Resources should be cleaned up to avoid memory leaks and resource exhaustion. Modern Java prefers try-with-resources.

---

# 10. OutOfMemoryError

Occurs when JVM cannot allocate additional memory.

## Example

```java
import java.util.ArrayList;
import java.util.List;

public class OutOfMemoryDemo {

    public static void main(String[] args) {

        List<int[]> list = new ArrayList<>();

        while (true) {
            list.add(new int[1000000]);
        }
    }
}
```

## Script

OutOfMemoryError indicates the JVM heap has been exhausted. It is an Error, not an Exception, and usually results from excessive object creation or memory leaks.

---

# 11. What is Generics?

Generics provide compile-time type safety.

## Example

```java
import java.util.ArrayList;
import java.util.List;

public class GenericsDemo {

    public static void main(String[] args) {

        List<String> names = new ArrayList<>();

        names.add("Alice");
        names.add("Bob");

        String first = names.get(0);

        System.out.println(first);
    }
}
```

## Advantages

1. Type safety
2. Eliminates casting
3. Code reuse
4. Compile-time error detection

## Script

Generics allow classes and methods to operate on different data types while maintaining type safety.

---

# 12. How Generics Work (Type Erasure)

Java removes generic type information during compilation.

## Example

```java
import java.util.ArrayList;
import java.util.List;

public class TypeErasureDemo {

    public static void main(String[] args) {

        List<String> names = new ArrayList<>();
        List<Integer> numbers = new ArrayList<>();

        System.out.println(
                names.getClass() == numbers.getClass()
        );
    }
}
```

## Output

```text
true
```

## Script

Java implements generics using type erasure. Generic information exists only during compilation and is removed at runtime.

---

# 13. Difference Between List<? extends T> and List<? super T>

## PECS Rule

```text
Producer Extends
Consumer Super
```

## Example

```java
import java.util.ArrayList;
import java.util.List;

class Animal {}
class Dog extends Animal {}

public class WildcardDemo {

    public static void readAnimals(List<? extends Animal> animals) {
        Animal animal = animals.get(0);
    }

    public static void addDogs(List<? super Dog> dogs) {
        dogs.add(new Dog());
    }

    public static void main(String[] args) {

        List<Dog> dogList = new ArrayList<>();
        readAnimals(dogList);

        List<Animal> animalList = new ArrayList<>();
        addDogs(animalList);
    }
}
```

## Script

Use `extends` when reading from a collection. Use `super` when writing to a collection.

---

# 14. Optional Class

Optional helps avoid NullPointerException.

## Example

```java
import java.util.Optional;

public class OptionalDemo {

    public static void main(String[] args) {

        String name = null;

        Optional<String> optionalName =
                Optional.ofNullable(name);

        String result1 =
                optionalName.orElse("Default Name");

        System.out.println(result1);

        String result2 =
                Optional.ofNullable("Alice")
                        .orElse("Default Name");

        System.out.println(result2);

        String result3 =
                Optional.ofNullable("Bob")
                        .orElseThrow(
                                () -> new RuntimeException("Name Missing")
                        );

        System.out.println(result3);
    }
}
```

## Script

* `ofNullable()` creates an Optional that may contain null.
* `orElse()` returns a default value.
* `orElseThrow()` throws an exception if no value is present.

---

# 15. What is OOP?

Object-Oriented Programming organizes code around objects.

## Four Pillars of OOP

### Encapsulation

Hide internal data using private fields.

### Inheritance

Child classes inherit behavior from parent classes.

### Polymorphism

One interface, many implementations.

### Abstraction

Hide implementation details.

## Example

```java
abstract class Vehicle {

    private String brand;

    public Vehicle(String brand) {
        this.brand = brand;
    }

    public String getBrand() {
        return brand;
    }

    abstract void move();
}

class Car extends Vehicle {

    public Car(String brand) {
        super(brand);
    }

    @Override
    void move() {
        System.out.println(getBrand() + " car is moving");
    }
}

class Bike extends Vehicle {

    public Bike(String brand) {
        super(brand);
    }

    @Override
    void move() {
        System.out.println(getBrand() + " bike is moving");
    }
}

public class OOPDemo {

    public static void main(String[] args) {

        Vehicle v1 = new Car("Toyota");
        Vehicle v2 = new Bike("Giant");

        v1.move();
        v2.move();
    }
}
```

## Script

OOP is a programming paradigm based on objects. Its four pillars are:

1. Encapsulation
2. Inheritance
3. Polymorphism
4. Abstraction

These principles improve code reuse, maintainability, and scalability.
