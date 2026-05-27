# 5/26 Class Notes

---

# 1. Coding Standards

## Professional IDE Usage

Use IDE automation to generate:
- constructors
- getters/setters
- `toString()`
- `equals()`
- `hashCode()`

Using IDE shortcuts is considered professional Java development practice.

---

## Java Naming Convention

```java
class Employee { }     // PascalCase

String firstName;      // camelCase
```

---

# 2. Primitive vs Reference Types

## Primitive Types (8)

```java
byte
short
int
long
float
double
char
boolean
```

Primitive variables store actual values.

Example:

```java
int age = 20;
```

---

## Reference Types

Everything else is a reference type.

Examples:

```java
String
List
ArrayList
Employee
```

Reference variables store object memory addresses.

Example:

```java
Employee emp = new Employee();
```

---

# 3. String vs StringBuilder vs StringBuffer

## String

```java
String s = "hello";
```

- Immutable
- Stored in String Constant Pool
- Every modification creates a new object

---

## StringBuilder

```java
StringBuilder sb = new StringBuilder();
```

- Mutable
- Faster
- Not thread-safe

Use for most string concatenation.

---

## StringBuffer

```java
StringBuffer sb = new StringBuffer();
```

- Mutable
- Thread-safe
- Slower because synchronized

---

# 4. String Comparison

## Wrong

```java
String a = new String("abc");
String b = new String("abc");

System.out.println(a == b);
```

`==` compares memory addresses.

---

## Correct

```java
System.out.println(a.equals(b));
```

`.equals()` compares actual content.

---

# 5. Collections Framework

## Collections Hierarchy

```text
Iterable
   └── Collection
        ├── List
        ├── Set
        └── Queue
```

`Map` is NOT part of Collection because it does not inherit from `Iterable`.

---

# 6. Array vs ArrayList

## Array

```java
int[] arr = new int[5];
```

- Fixed size

---

## ArrayList

```java
List<Integer> list = new ArrayList<>();
```

- Dynamic size
- Supports resizing

---

# 7. Autoboxing & Wrapper Classes

## Wrapper Classes

| Primitive | Wrapper |
|---|---|
| int | Integer |
| double | Double |
| char | Character |

---

## Autoboxing

```java
List<Integer> list = new ArrayList<>();

list.add(1);
```

Java automatically converts:

```java
int -> Integer
```

---

# 8. List Implementations

## ArrayList

Best for:
- random access
- indexing

Time Complexity:

```text
get() -> O(1)
```

---

## LinkedList

Best for:
- insertion
- deletion

---

## PriorityQueue

Default:
- Min Heap

Example:

```java
PriorityQueue<Integer> pq = new PriorityQueue<>();
```

---

## ArrayDeque

Preferred over LinkedList for queue/deque operations.

---

# 9. List vs Set

## List

- Allows duplicates
- Maintains order

Example:

```java
List<Integer> list = new ArrayList<>();
```

---

## Set

- No duplicates

Example:

```java
Set<Integer> set = new HashSet<>();
```

---

## LinkedHashSet

- Maintains insertion order

---

## TreeSet

- Sorted order

Example:

```java
TreeSet<Integer> set = new TreeSet<>();
```

---

# 10. Comparable vs Comparator

## Comparable

Default sorting logic.

```java
class Employee implements Comparable<Employee> {

    @Override
    public int compareTo(Employee o) {
        return this.salary - o.salary;
    }
}
```

---

## Comparator

Custom sorting logic.

```java
Comparator<Employee> byAge =
    (a, b) -> a.age - b.age;
```

---

# 11. HashMap & Hash Collision

## HashMap Structure

```text
key -> hash -> bucket
```

---

## Collision

Different keys generate same hash.

Java handles collisions using:
- LinkedList
- Red-Black Tree (Java 8+)

If bucket size > 8:
- converts linked list into Red-Black Tree

---

## Important Rule

Always override BOTH:

```java
equals()
hashCode()
```

---

# 12. Map Implementations

## HashMap

- Fast lookup
- No ordering

---

## LinkedHashMap

- Maintains insertion order

---

## TreeMap

- Sorted by key

---

## ConcurrentHashMap

Thread-safe alternative to Hashtable.

Better performance because:
- fine-grained locking

---

# 13. Generics

## Generic Syntax

```java
List<String>
List<Integer>
```

Provides:
- type safety
- compile-time checking

---

# 14. PECS Principle

## Producer Extends

```java
<? extends Number>
```

Use when reading data.

---

## Consumer Super

```java
<? super Integer>
```

Use when writing data.

---

# 15. Interview Communication Tips

## Important Strategies

- Practice without scripts
- Speak clearly
- Explain thought process step-by-step
- Be able to drive the solution independently

Interviewers evaluate:
- communication
- confidence
- organization
- engineering habits

---

# 16. Professional Habits

Recommended habits:
- Maintain organized notes
- Keep GitHub updated
- Practice daily
- Review previous topics
- Use clean workspace during screen sharing