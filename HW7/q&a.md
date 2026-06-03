## 1. Optimized Singleton Pattern (Thread-Safe, Double-Checked Locking)

```java
public class Singleton {

    // 1. 'volatile' ensures that changes to this variable are visible
    //    to all threads immediately, and prevents instruction reordering
    //    by the JVM/CPU during the creation phase (critical for double-checked locking).
    private static volatile Singleton instance;

    // 2. Private constructor prevents any external class from calling 'new Singleton()'.
    //    This is the core enforcement mechanism of the pattern.
    private Singleton() {
        // Optionally: throw exception if reflection attempts to instantiate again
    }

    // 3. Public static factory method — the single global access point to the instance.
    public static Singleton getInstance() {

        // 4. FIRST CHECK (no lock): If the instance already exists, skip synchronization
        //    entirely. This is the performance optimization — after the first creation,
        //    99% of calls hit this fast path without acquiring any lock.
        if (instance == null) {

            // 5. SYNCHRONIZED BLOCK: Only one thread at a time can enter here.
            //    We synchronize on the class object itself, which is unique per JVM.
            synchronized (Singleton.class) {

                // 6. SECOND CHECK (inside lock): Another thread may have created
                //    the instance between the first null check and acquiring the lock.
                //    This guard prevents double instantiation in a race condition.
                if (instance == null) {

                    // 7. Actual object creation. Because 'instance' is volatile,
                    //    this write is guaranteed to complete before any thread
                    //    can read a non-null value from it.
                    instance = new Singleton();
                }
            }
        }

        // 8. Return the single shared instance (either freshly created or pre-existing).
        return instance;
    }
}
```

### Why each piece matters

| Element | Why it's there |
|---|---|
| `private static volatile Singleton instance` | Shared state; `volatile` prevents stale reads and reordering |
| `private Singleton()` | Blocks external instantiation |
| First `if (instance == null)` | Fast path — avoids lock on every call after init |
| `synchronized (Singleton.class)` | Mutual exclusion during first-time creation |
| Second `if (instance == null)` | Guards against race between first check and lock acquisition |
| `volatile` write | Guarantees full construction before other threads see the reference |

---

## 2. Design Pattern Use Cases by Industry (12 Total)

### Singleton

**Banking**
> A central **Fraud Detection Engine** runs as a singleton service. Every transaction routed through the application hits the same shared model state and rule set. Multiple instances would cause divergent detection results and race conditions on shared fraud counters.

**E-Commerce**
> The **Shopping Cart Session Manager** can use a singleton-per-user-session registry. The globally shared component that maps session tokens to cart state exists as a single coordinator — duplicating it would cause users to lose items or see stale counts.

**Health Insurance**
> A **Premium Rate Table Cache** is loaded once from the actuary database at startup and shared across all underwriting services. Because rate tables change infrequently, a singleton cache avoids redundant DB calls and ensures every underwriting decision uses the same rates.

---

### Factory

**Banking**
> A **Loan Product Factory** creates the appropriate loan object — `MortgageLoan`, `AutoLoan`, or `PersonalLoan` — based on input parameters. The approval workflow doesn't need to know which concrete class it's working with; it just calls `LoanFactory.create(type)` and processes the result uniformly.

**E-Commerce**
> A **Payment Method Factory** returns `CreditCardPayment`, `PayPalPayment`, `CryptoPayment`, or `BuyNowPayLaterPayment` based on user selection. The checkout service stays decoupled from concrete payment implementations; adding a new provider means adding one new class, not modifying checkout logic.

**Health Insurance**
> A **Claim Processor Factory** produces the right processor — `DentalClaimProcessor`, `VisionClaimProcessor`, `HospitalizationClaimProcessor` — based on claim type. Each processor has different adjudication rules, but the intake pipeline treats all of them through a common interface.

---

### Builder

**Banking**
> Building a **Complex Financial Report** (e.g., a quarterly regulatory filing with optional sections: risk exposure, capital ratios, stress-test results, footnotes) uses a builder so the caller assembles only the sections required for a given report type without dealing with a 20-parameter constructor.

**E-Commerce**
> Constructing an **Order Object** with optional fields (gift wrapping, express shipping, loyalty points redemption, coupon codes, split delivery addresses) is a classic builder scenario. `OrderBuilder.withItem(...).withCoupon(...).withGiftWrap(true).build()` is far more readable and maintainable than overloaded constructors.

**Health Insurance**
> Building an **Insurance Policy** involves dozens of optional riders (dental, vision, mental health, maternity, out-of-network), deductible levels, co-pay schedules, and network tiers. A builder lets the agent portal compose exactly the right policy configuration step-by-step without a combinatorial explosion of constructors.

---

### Proxy

**Banking**
> A **Database Access Proxy** sits between the application and the core banking database. It enforces row-level security (e.g., a teller can only see accounts in their branch), logs every query for audit purposes, and can add read-replica load balancing — all transparently, without changing the data-access code.

**E-Commerce**
> A **Product Image Proxy** (Virtual Proxy / Lazy Loading) defers fetching high-resolution product images until the user actually hovers or clicks. The product listing page loads instantly with lightweight placeholders; the expensive asset is only fetched on demand, dramatically improving Time-to-First-Paint.

**Health Insurance**
> A **HIPAA Compliance Proxy** wraps all patient-record service calls. Before forwarding the request, it checks that the caller has a valid, role-appropriate authorization token and logs access for regulatory audit trails. The underlying record service has no knowledge of access-control logic — it's entirely handled by the proxy.

---

## 3. What is Reflection?

Reflection is the ability of a program to **inspect and manipulate its own structure at runtime** — examining classes, fields, methods, constructors, and annotations without knowing them at compile time.

In Java, the `java.lang.reflect` package makes this possible:

```java
Class<?> clazz = Class.forName("com.example.MyService"); // load class by name
Method method = clazz.getDeclaredMethod("processOrder", String.class);
method.setAccessible(true);           // bypass private modifier
Object result = method.invoke(instance, "order-123");
```

**Why it matters:**

- Dependency injection frameworks (Spring) use reflection to wire beans at startup.
- ORM tools (Hibernate) use it to map Java fields to database columns.
- Testing frameworks (JUnit) discover and invoke test methods via reflection.
- Serialization libraries (Jackson) map JSON keys to object fields dynamically.
- It can break encapsulation (e.g., instantiate a Singleton via `Constructor.newInstance()`), which is why hardened Singleton implementations throw exceptions inside the private constructor if an instance already exists.

**Trade-offs:** Reflection is powerful but slow (bypasses JIT optimizations), unsafe (no compile-time checks), and can break encapsulation. Use it as a last resort, not a default tool.

---

## 4. HTTP Status Codes

HTTP status codes are the server's way of telling the client "here's what happened with your request." They're grouped in hundreds: 2xx = success, 3xx = redirect, 4xx = client's fault, 5xx = server's fault.

| Code | Name | Plain-English Meaning |
|---|---|---|
| **200** | OK | The request worked exactly as expected. You asked, I answered. |
| **201** | Created | A new resource was successfully created (e.g., new user registered, new order placed). Often returned with a `Location` header pointing to the new resource. |
| **202** | Accepted | The request was received and queued, but processing hasn't finished yet. Used for async jobs — "I'll get to it." |
| **204** | No Content | Success, but there's nothing to send back. Common for DELETE or PUT operations where you don't need a response body. |
| **301** | Moved Permanently | The resource has moved to a new URL forever. Clients and search engines should update their bookmarks. |
| **307** | Temporary Redirect | The resource is temporarily somewhere else, but keep using the original URL next time. Unlike 301, the HTTP method must not change. |
| **400** | Bad Request | The client sent garbage — malformed JSON, missing required fields, invalid parameter types. Fix your request. |
| **401** | Unauthorized | You're not authenticated. The server doesn't know who you are — send credentials (e.g., a token). |
| **403** | Forbidden | The server knows who you are, but you don't have permission. Authentication is fine; authorization is not. |
| **404** | Not Found | The resource doesn't exist at that URL. Either it never existed or it was deleted. |
| **500** | Internal Server Error | Something broke on the server's side. Not the client's fault — go check the server logs. |

**Key distinctions:**
- **401 vs 403**: 401 = "Who are you?" (unauthenticated). 403 = "I know who you are, but no." (unauthorized).
- **201 vs 202**: 201 = resource is already created right now. 202 = request accepted, creation is pending.
- **301 vs 307**: 301 = permanent, method can change; 307 = temporary, method must stay the same.

---

## 5. What is HTTP?

HTTP (HyperText Transfer Protocol) is the **foundation of data communication on the web**. It's a stateless, request-response protocol that defines how clients (browsers, mobile apps, API consumers) and servers talk to each other.

**Key characteristics:**

- **Stateless**: Every request is independent. The server doesn't remember the previous request. Session state must be managed externally (cookies, tokens, server-side sessions).
- **Request-Response model**: The client sends a request (with a method, URL, headers, optional body); the server replies with a status code, headers, and optional body.
- **Text-based and human-readable** (in HTTP/1.1). HTTP/2 uses binary framing for performance; HTTP/3 runs over QUIC (UDP-based) for lower latency.
- **Layered on TCP/IP** (HTTP/1.x, HTTP/2) or QUIC (HTTP/3).

A typical HTTP request looks like:

```
POST /api/orders HTTP/1.1
Host: api.example.com
Content-Type: application/json
Authorization: Bearer eyJhbGc...

{"productId": "42", "quantity": 2}
```

HTTPS is simply HTTP with TLS encryption layered on top.

---

## 6. HTTP Methods: GET, POST, PUT, DELETE, PATCH

| Method | Purpose | Has Request Body? | Safe? | Idempotent? |
|---|---|---|---|---|
| GET | Retrieve a resource | No | Yes | Yes |
| POST | Create a new resource or trigger an action | Yes | No | No |
| PUT | Replace an entire resource | Yes | No | Yes |
| DELETE | Remove a resource | Optional | No | Yes |
| PATCH | Partially update a resource | Yes | No | No |

### GET
Read-only. Fetches a resource without modifying anything. Parameters go in the URL query string. Never use GET to trigger side effects.

```
GET /api/users/42
```

### POST
Creates a new resource. The server decides the new resource's URL. Submitting a form, placing an order, creating a user — all POST. Calling it twice creates two resources.

```
POST /api/users
Body: { "name": "Xi", "email": "xi@example.com" }
```

### PUT
Replaces a resource completely. You send the full representation of the resource; whatever was there before is gone. If the field is absent in the body, it's treated as null/deleted.

```
PUT /api/users/42
Body: { "name": "Xi Zhang", "email": "xi@example.com", "role": "admin" }
```

### DELETE
Removes a resource. After a successful delete, subsequent requests for that resource return 404.

```
DELETE /api/users/42
```

### PATCH
Applies a partial update. You only send the fields you want to change; everything else stays untouched.

```
PATCH /api/users/42
Body: { "email": "new@example.com" }
```

---

## 7. POST vs PATCH

| | POST | PATCH |
|---|---|---|
| **Purpose** | Create a new resource | Partially update an existing resource |
| **URL** | Collection endpoint: `/api/users` | Resource endpoint: `/api/users/42` |
| **Body** | Full new resource | Only the fields being changed |
| **Idempotent?** | No — calling it twice creates two resources | No — but in practice, a well-designed PATCH with the same diff is idempotent |
| **Example** | Creating a new user | Changing only a user's email address |

**Summary:** POST is for creation; PATCH is for surgical partial updates. If you don't want to send the entire resource just to change one field, use PATCH.

---

## 8. POST vs PUT

| | POST | PUT |
|---|---|---|
| **Purpose** | Create a new resource | Fully replace an existing resource |
| **Who assigns the ID?** | The server assigns it | The client specifies the exact URL |
| **Body** | Partial or full new resource | Complete resource representation |
| **Idempotent?** | No | Yes — calling it twice with the same body produces the same result |
| **Missing fields** | N/A (creating new) | Treated as null / removed |
| **Example** | `POST /api/orders` → server creates order #101 | `PUT /api/orders/101` → replaces the entire order |

**Summary:** Use POST when the server owns the identity of the new resource. Use PUT when the client knows the exact URL and wants to do a full replacement. Accidentally using POST twice creates duplicates; PUT is safe to retry.

---

## 9. What is Idempotency? Which HTTP Methods are Idempotent?

### Definition

An operation is **idempotent** if calling it once produces the same result as calling it multiple times.

> Formally: `f(f(x)) = f(x)` — applying the function twice is the same as applying it once.

In HTTP terms: no matter how many times you repeat the same request, the server's state ends up the same. This matters enormously in distributed systems where network failures cause clients to retry requests — you need to know whether retrying is safe.

### Which methods are idempotent?

| Method | Idempotent? | Why |
|---|---|---|
| **GET** |  Yes | Read-only; no state change at all |
| **PUT** |  Yes | Replaces the resource with the same data every time; end state is identical |
| **DELETE** |  Yes | Deleting something that's already deleted still results in it being gone (404 on repeat is acceptable) |
| **PATCH** |  No (usually) | Depends on the patch semantics. `{"increment": 1}` applied twice changes state; but a set-based patch like `{"email": "x@y.com"}` could be considered idempotent in practice |
| **POST** |  No | Creates a new resource every call; two identical POST requests produce two separate resources |
| **HEAD** |  Yes | Like GET, read-only |
| **OPTIONS** |  Yes | Read-only metadata discovery |

### Why it matters

Distributed systems (microservices, message queues, event-driven architectures) frequently deal with **at-least-once delivery** — messages may be processed more than once due to retries or failures. If your endpoints are idempotent, duplicate processing is harmless. If they're not, you need **idempotency keys** (a unique request ID the server stores to detect and de-duplicate replays) — a technique used heavily in payment APIs like Stripe.

```
POST /api/payments
Idempotency-Key: 7f3d9c2a-...
Body: { "amount": 500, "currency": "USD" }
```

If the same `Idempotency-Key` is seen again, the server returns the original response instead of charging the customer twice.

---

## 10. Singleton in Java — All Implementations (Lazy Loading Focus)

There are several ways to implement Singleton in Java. Understanding all of them — and their trade-offs — is essential.

### Approach 1: Eager Initialization (Not Lazy)

```java
public class EagerSingleton {
    // Instance is created at class-loading time, before anyone calls getInstance().
    // Simple and thread-safe, but wastes memory if the instance is never used.
    private static final EagerSingleton INSTANCE = new EagerSingleton();

    private EagerSingleton() {}

    public static EagerSingleton getInstance() {
        return INSTANCE;
    }
}
```

**Trade-off:** Thread-safe by the JVM class-loader guarantee, but not lazy — the object is created even if the application never uses it.

---

### Approach 2: Synchronized Method (Lazy, but Slow)

```java
public class SynchronizedSingleton {
    private static SynchronizedSingleton instance;

    private SynchronizedSingleton() {}

    // Lazy: instance is only created on first call.
    // Problem: every single call acquires a lock — massive performance bottleneck
    // after the first initialization because the lock is unnecessary thereafter.
    public static synchronized SynchronizedSingleton getInstance() {
        if (instance == null) {
            instance = new SynchronizedSingleton();
        }
        return instance;
    }
}
```

**Trade-off:** Lazy and thread-safe, but `synchronized` on every call makes this 10–100x slower under contention.

---

### Approach 3: Double-Checked Locking (Lazy + Fast) ✅ Recommended

```java
public class DCLSingleton {

    // volatile: prevents the JVM from returning a partially-constructed object
    // due to instruction reordering. Without volatile, another thread could
    // see a non-null but incompletely initialized instance.
    private static volatile DCLSingleton instance;

    private DCLSingleton() {}

    public static DCLSingleton getInstance() {

        // First check (no lock): fast path for the 99% of calls after init.
        if (instance == null) {

            // Lock: only one thread creates the instance.
            synchronized (DCLSingleton.class) {

                // Second check (inside lock): guards against two threads that
                // both passed the first null-check before either acquired the lock.
                if (instance == null) {
                    instance = new DCLSingleton();
                }
            }
        }
        return instance;
    }
}
```

**Trade-off:** Lazy, thread-safe, and fast after the first creation. Requires Java 5+ (when the `volatile` memory model was fixed). This is the standard production answer.

---

### Approach 4: Bill Pugh / Initialization-on-Demand Holder (Best Lazy) ✅ Most Elegant

```java
public class HolderSingleton {

    private HolderSingleton() {}

    // The JVM loads inner static classes only when they are first referenced.
    // Class loading is inherently thread-safe — the JVM guarantees it happens
    // exactly once. So there is no need for synchronized or volatile at all.
    private static class Holder {
        // This line runs only when Holder is first accessed, which happens
        // only when getInstance() is called for the first time.
        private static final HolderSingleton INSTANCE = new HolderSingleton();
    }

    public static HolderSingleton getInstance() {
        // Triggers Holder class loading if not already loaded.
        // From that point on, INSTANCE is a constant — no locking needed.
        return Holder.INSTANCE;
    }
}
```

**Why this is the best:** Lazy initialization is guaranteed by the JVM's class-loading mechanism. No `synchronized`, no `volatile`, no performance overhead, no boilerplate. Thread safety comes for free from the JLS (Java Language Specification) class initialization guarantee.

---

### Approach 5: Enum Singleton (Reflection-Proof + Serialization-Safe) ✅ Josh Bloch's Pick

```java
// Enum constants are initialized exactly once by the JVM.
// Enums are inherently serializable and immune to reflection attacks
// (you cannot call Constructor.newInstance() on an enum).
public enum EnumSingleton {

    INSTANCE; // The one and only instance

    // Add your singleton methods here
    public void doSomething() {
        System.out.println("Singleton logic here");
    }
}

// Usage:
EnumSingleton.INSTANCE.doSomething();
```

**Why it matters:** The double-checked locking approach can be broken by serialization (deserialization creates a new object) and by reflection (`setAccessible(true)` on the private constructor). Enum sidesteps both. Joshua Bloch (author of *Effective Java*) calls this the best Singleton approach.

---

### Comparison Table

| Approach | Lazy? | Thread-Safe? | Reflection-Proof? | Serialization-Safe? | Performance |
|---|---|---|---|---|---|
| Eager | ❌ | ✅ (JVM) | ❌ | ❌ | Fast |
| Synchronized method | ✅ | ✅ | ❌ | ❌ | Slow (every call locks) |
| Double-checked locking | ✅ | ✅ | ❌ | ❌ | Fast |
| Holder pattern | ✅ | ✅ (JVM) | ❌ | ❌ | Fastest |
| Enum | ❌ (class load) | ✅ (JVM) | ✅ | ✅ | Fast |

**Recommendation:** Lead with DCL, then mention the Holder pattern as the most elegant lazy solution, then mention Enum if serialization or reflection resistance is a requirement.

---

## 11. New Features in Java 8

Java 8 (March 2014) was the most transformative Java release — it brought functional programming to the JVM.

### 1. Lambda Expressions
Concise syntax to represent a function as a value. Eliminates anonymous inner classes for single-method interfaces.

```java
// Before Java 8
Runnable r = new Runnable() {
    @Override public void run() { System.out.println("Running"); }
};

// Java 8
Runnable r = () -> System.out.println("Running");

// With parameters
Comparator<String> c = (a, b) -> a.compareTo(b);
```

### 2. Functional Interfaces & `@FunctionalInterface`
Any interface with exactly one abstract method is a functional interface and can be used with lambdas. Java 8 ships four key built-in ones:

```java
// Predicate<T>: takes T, returns boolean
Predicate<String> isEmpty = s -> s.isEmpty();

// Function<T, R>: takes T, returns R
Function<String, Integer> length = s -> s.length();

// Consumer<T>: takes T, returns void
Consumer<String> printer = s -> System.out.println(s);

// Supplier<T>: takes nothing, returns T
Supplier<List<String>> listFactory = ArrayList::new;
```

### 3. Stream API
Declarative, lazy, pipeline-based data processing for collections.

```java
List<String> names = List.of("Alice", "Bob", "Charlie", "Anna");

List<String> result = names.stream()
    .filter(n -> n.startsWith("A"))   // intermediate, lazy
    .map(String::toUpperCase)         // intermediate, lazy
    .sorted()                         // intermediate, lazy
    .collect(Collectors.toList());    // terminal — triggers execution

// ["ALICE", "ANNA"]
```

### 4. Optional\<T\>
A container that may or may not hold a value. Forces the caller to handle the null case explicitly instead of getting a NullPointerException at runtime.

```java
Optional<String> name = Optional.ofNullable(getNameFromDB()); // may be null

// Instead of: if (name != null) { ... }
name.ifPresent(n -> System.out.println("Hello, " + n));

String upper = name.map(String::toUpperCase).orElse("Unknown");
```

### 5. Method References
Shorthand for lambdas that just call an existing method.

```java
// Lambda           →    Method reference
s -> s.toUpperCase()     String::toUpperCase   // instance method
s -> System.out.println(s)  System.out::println  // instance on specific object
() -> new ArrayList<>()  ArrayList::new         // constructor reference
(a, b) -> Math.max(a,b)  Math::max              // static method
```

### 6. Default and Static Methods in Interfaces
Interfaces can now have concrete method implementations.

```java
public interface Greeter {
    // Default: concrete, inherited by all implementors
    default String greet(String name) {
        return "Hello, " + name;
    }

    // Static: called on the interface itself
    static Greeter formal() {
        return name -> "Good day, " + name;
    }
}
```

This allowed Java to add methods to `List`, `Map`, and `Collection` without breaking every existing implementation (e.g., `List.sort()`, `Map.forEach()`).

### 7. New Date/Time API (`java.time`)
Replaced the broken, mutable `java.util.Date` and `Calendar`.

```java
LocalDate today = LocalDate.now();                    // date only
LocalTime now = LocalTime.now();                      // time only
LocalDateTime dt = LocalDateTime.of(2024, 3, 15, 10, 30); // both
ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("America/New_York"));

Period period = Period.between(LocalDate.of(2000, 1, 1), today); // date diff
Duration dur = Duration.between(startTime, endTime);              // time diff
```

All classes are **immutable** and **thread-safe**.

### 8. CompletableFuture
Async programming with chainable, non-blocking operations.

```java
CompletableFuture.supplyAsync(() -> fetchUser(id))       // async task
    .thenApply(user -> enrichWithOrders(user))           // transform result
    .thenAccept(enriched -> sendResponse(enriched))      // consume result
    .exceptionally(ex -> { log(ex); return null; });     // handle errors
```

---

## 12. New Features in Java 11 (LTS)

Java 11 (September 2018) was the first long-term support release after Java 8. Key additions:

### 1. `var` for Local Variables (actually Java 10, but part of the LTS baseline)
```java
var list = new ArrayList<String>(); // type inferred as ArrayList<String>
var name = "Xi";                    // inferred as String
// Only works for local variables, not fields, parameters, or return types
```

### 2. New String Methods

```java
// isBlank(): true if empty or only whitespace (unlike isEmpty())
"   ".isBlank();              // true
"   ".isEmpty();              // false

// strip() / stripLeading() / stripTrailing(): Unicode-aware trim()
"  hello  ".strip();          // "hello" (handles Unicode whitespace)
"  hello  ".trim();           // "hello" (only handles ASCII whitespace \u0020)

// lines(): splits a string into a Stream<String> by line breaks
"a\nb\nc".lines().collect(Collectors.toList()); // ["a","b","c"]

// repeat(n): repeats the string n times
"ab".repeat(3);               // "ababab"
```

### 3. `HttpClient` (Standard, not incubator)
A modern, async HTTP client that replaced the ancient `HttpURLConnection`.

```java
HttpClient client = HttpClient.newHttpClient();

HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.example.com/users"))
    .header("Accept", "application/json")
    .GET()
    .build();

HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
System.out.println(response.statusCode()); // 200
System.out.println(response.body());
```

Supports HTTP/2, WebSockets, and async via `sendAsync()` returning `CompletableFuture`.

### 4. Running Java Files Directly (Single-File Execution)
```bash
# No need to compile first
java HelloWorld.java
```
Great for scripting and quick prototyping.

### 5. `Optional.isEmpty()`
```java
Optional<String> opt = Optional.empty();
opt.isEmpty();   // true  (new in Java 11)
opt.isPresent(); // false (existed before)
```

### 6. `Files` Utility Methods

```java
// Read entire file to string in one line
String content = Files.readString(Path.of("file.txt"));

// Write string to file in one line
Files.writeString(Path.of("out.txt"), "Hello, World!");
```

### 7. `Collection.toArray(IntFunction)`
```java
List<String> list = List.of("a", "b", "c");
String[] arr = list.toArray(String[]::new); // cleaner than toArray(new String[0])
```

### 8. Removed and Deprecated
- Removed: Java EE and CORBA modules (`javax.xml.bind`, `java.xml.ws`, etc.) — now use external dependencies.
- Removed: Applet API, `Thread.destroy()`.

---

## 13. New Features in Java 17 (LTS)

Java 17 (September 2021) is the current dominant LTS in production. It finalized many preview features from Java 14–16.

### 1. Sealed Classes (`sealed`, `permits`)
Restricts which classes can extend or implement a class/interface. Perfect for modeling closed type hierarchies (like algebraic data types).

```java
// Only Circle, Rectangle, and Triangle can extend Shape.
// The compiler knows the complete list — enabling exhaustive pattern matching.
public sealed class Shape permits Circle, Rectangle, Triangle {}

public final class Circle extends Shape {
    double radius;
}
public final class Rectangle extends Shape {
    double width, height;
}
public non-sealed class Triangle extends Shape {
    // non-sealed: Triangle's subclasses are unrestricted
}
```

### 2. Pattern Matching for `instanceof`
Eliminates the redundant cast after an `instanceof` check.

```java
// Before Java 16+
if (obj instanceof String) {
    String s = (String) obj; // redundant cast
    System.out.println(s.length());
}

// Java 16+ (finalized)
if (obj instanceof String s) {
    System.out.println(s.length()); // 's' is already typed and in scope
}
```

### 3. Records (Finalized in Java 16, standard in 17)
Immutable data carriers. The compiler auto-generates constructor, getters, `equals()`, `hashCode()`, and `toString()`.

```java
// One line replaces ~50 lines of boilerplate
public record Point(int x, int y) {}

// Usage
Point p = new Point(3, 4);
p.x();          // 3 (generated accessor — NOT getX())
p.toString();   // "Point[x=3, y=4]"
p.equals(new Point(3, 4)); // true
```

Perfect for DTOs, value objects, API response models.

### 4. Text Blocks (Finalized in Java 15, standard in 17)
Multi-line strings without escape hell.

```java
// Before
String json = "{\n  \"name\": \"Xi\",\n  \"age\": 25\n}";

// Java 15+ text block
String json = """
        {
          "name": "Xi",
          "age": 25
        }
        """;
```

The compiler strips the common leading indentation automatically.

### 5. `switch` Expressions (Finalized in Java 14, standard in 17)
`switch` can now be an expression (returns a value) and uses `->` arrow syntax.

```java
// Old: statement switch with fall-through risk
String label;
switch (day) {
    case MONDAY: label = "Start"; break;
    default:     label = "Other";
}

// New: expression switch, no fall-through, exhaustive
String label = switch (day) {
    case MONDAY -> "Start";
    case FRIDAY -> "End";
    default     -> "Other";
};
```

### 6. Enhanced `NullPointerException` Messages
The JVM now tells you *which* variable was null.

```java
user.getAddress().getCity().toUpperCase();
// Before: NullPointerException (which object??)
// Java 17: Cannot invoke "Address.getCity()" because the return value
//          of "User.getAddress()" is null
```

### 7. Strong Encapsulation of JDK Internals
`--illegal-access` is removed. Reflective access to internal JDK APIs (`sun.misc.Unsafe`, etc.) is now blocked by default. This matters for frameworks like Netty or older Spring versions.

---

## 14. New Features in Java 21 (LTS)

Java 21 (September 2023) is the newest LTS. Its flagship feature is virtual threads — the biggest concurrency change in Java's history.

### 1. Virtual Threads (Project Loom) — The Headliner
Traditional Java threads map 1:1 to OS threads. OS threads are expensive — each costs ~1MB of stack memory and context-switching is slow. This limits Java servers to thousands of concurrent threads.

Virtual threads are **lightweight threads managed by the JVM**, not the OS. You can create **millions** of them. They park (not block) when waiting on I/O, freeing the carrier OS thread to do other work.

```java
// Before: thread-per-request with OS threads (expensive)
ExecutorService pool = Executors.newFixedThreadPool(200); // hard ceiling

// Java 21: thread-per-request with virtual threads (scales to millions)
ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

executor.submit(() -> {
    // When this blocks on DB/HTTP/IO, the virtual thread parks.
    // The underlying OS thread is freed and picks up another virtual thread.
    String result = database.query("SELECT ...");
    return result;
});

// Even simpler: create a single virtual thread
Thread vt = Thread.ofVirtual().start(() -> System.out.println("I'm virtual!"));
```

**Why it's a big deal:** You can now write simple blocking code (no reactive/async plumbing) and get the scalability of non-blocking I/O. Spring Boot 3.2+ uses virtual threads by default.

### 2. Pattern Matching for `switch` (Finalized)
Combining `switch` with `instanceof` pattern matching and sealed classes.

```java
sealed interface Shape permits Circle, Rectangle {}
record Circle(double radius) implements Shape {}
record Rectangle(double w, double h) implements Shape {}

double area(Shape s) {
    return switch (s) {
        case Circle c    -> Math.PI * c.radius() * c.radius();
        case Rectangle r -> r.w() * r.h();
        // Exhaustive: compiler knows all permitted subtypes from sealed interface
    };
}
```

### 3. Record Patterns
Deconstruct record components directly inside pattern matching.

```java
record Point(int x, int y) {}

Object obj = new Point(3, 4);

// Deconstruct Point directly in the pattern
if (obj instanceof Point(int x, int y)) {
    System.out.println("x=" + x + ", y=" + y); // x=3, y=4
}

// Works in switch too
String describe(Object o) {
    return switch (o) {
        case Point(int x, int y) when x == y -> "On diagonal: " + x;
        case Point(int x, int y)              -> "Point at " + x + "," + y;
        default                               -> "Not a point";
    };
}
```

### 4. Sequenced Collections
New interfaces `SequencedCollection`, `SequencedSet`, `SequencedMap` that guarantee access to first and last elements — something weirdly missing from the collections API until now.

```java
List<String> list = new ArrayList<>(List.of("a", "b", "c"));

list.getFirst();   // "a" (new)
list.getLast();    // "c" (new)
list.addFirst("z"); // ["z","a","b","c"] (new)
list.reversed();   // lazy reversed view: ["c","b","a"] (new)
```

### 5. Structured Concurrency (Preview)
Treats multiple concurrent tasks as a single unit of work. If any subtask fails, all are cancelled together — no orphaned threads.

```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Future<String> user   = scope.fork(() -> fetchUser(id));
    Future<String> orders = scope.fork(() -> fetchOrders(id));

    scope.join();           // wait for both
    scope.throwIfFailed();  // propagate any failure

    return new Response(user.resultNow(), orders.resultNow());
}
// Scope closes here: all tasks are guaranteed to be done or cancelled
```

### 6. String Templates (Preview in 21)
Interpolation with validation — safer than string concatenation.

```java
String name = "Xi";
int age = 25;

// STR template processor performs interpolation
String msg = STR."Hello, \{name}! You are \{age} years old.";
// "Hello, Xi! You are 25 years old."

// FMT processor applies format specifiers
String formatted = FMT."Pi is approximately %,.2f\{Math.PI}";
// "Pi is approximately 3.14"
```

---

## 15. Preventing Memory Leaks in Java

Despite garbage collection, Java applications can still leak memory — whenever live references prevent the GC from reclaiming objects that are logically dead. A senior engineer knows the common patterns and how to prevent them.

### Common Causes and Fixes

**1. Static Collections that grow unboundedly**
```java
// LEAK: 'cache' is static — lives as long as the class.
// Items are added but never removed.
public class UserService {
    private static final Map<Integer, User> cache = new HashMap<>();

    public User getUser(int id) {
        cache.put(id, fetchFromDB(id)); // grows forever
        return cache.get(id);
    }
}

// FIX: Use a bounded cache or a WeakReference-backed map
private static final Map<Integer, User> cache =
    Collections.synchronizedMap(new LinkedHashMap<>(100, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, User> eldest) {
            return size() > 100; // evict when exceeding 100 entries
        }
    });

// OR: Use WeakHashMap — GC can collect values when keys are no longer referenced
private static final Map<Integer, User> cache = new WeakHashMap<>();
```

**2. Not Closing Resources (Streams, Connections, Files)**
```java
// LEAK: If an exception occurs, the connection is never closed.
Connection conn = dataSource.getConnection();
ResultSet rs = conn.createStatement().executeQuery("SELECT ...");
// ... if exception here, conn is never closed → connection pool exhausted

// FIX: try-with-resources (AutoCloseable) — guaranteed close in all paths
try (Connection conn = dataSource.getConnection();
     ResultSet rs = conn.createStatement().executeQuery("SELECT ...")) {
    // use rs
} // conn and rs are closed automatically here, even on exception
```

**3. Inner Class Holding Implicit Reference to Outer Class**
```java
// LEAK: Non-static inner class holds an implicit reference to OuterService.
// If the Runnable is stored somewhere long-lived, OuterService can never be GC'd.
public class OuterService {
    private byte[] largeData = new byte[1024 * 1024]; // 1MB

    public Runnable createTask() {
        return new Runnable() { // non-static inner class
            @Override
            public void run() {
                System.out.println("Running");
                // Implicitly holds 'OuterService.this' → largeData leaks
            }
        };
    }
}

// FIX 1: Use a static nested class (no implicit outer reference)
public static class Task implements Runnable {
    @Override public void run() { System.out.println("Running"); }
}

// FIX 2: Use a lambda (lambdas only capture what they actually use)
public Runnable createTask() {
    return () -> System.out.println("Running"); // captures nothing
}
```

**4. Listeners and Callbacks Never Deregistered**
```java
// LEAK: EventBus holds a strong reference to listener.
// If 'this' should be GC'd but the bus lives on, it won't be.
eventBus.register(this);

// FIX: Always deregister when the object is no longer needed
// (in onDestroy, close(), or a try-finally block)
try {
    eventBus.register(this);
    // ... do work
} finally {
    eventBus.unregister(this);
}
```

**5. ThreadLocal Not Cleaned Up**
```java
// LEAK: In a thread-pool environment (e.g., Tomcat), threads are reused.
// A ThreadLocal value set in one request persists into the next request's thread.
private static final ThreadLocal<UserContext> context = new ThreadLocal<>();

// FIX: Always remove after the request is done (e.g., in a servlet Filter)
try {
    context.set(new UserContext(request));
    chain.doFilter(request, response);
} finally {
    context.remove(); // CRITICAL — prevents cross-request contamination and leaks
}
```

**6. Overriding `equals()`/`hashCode()` incorrectly in Collections**
If you override `equals()` but not `hashCode()`, objects stored in `HashMap`/`HashSet` can never be found or removed — they accumulate silently.

```java
// FIX: Always override both together. In modern Java, use records
// (which auto-generate correct equals/hashCode) or IDE generation.
public record UserId(int value) {} // equals + hashCode generated correctly
```

### Detection Tools
- **JVM flags**: `-verbose:gc`, `-Xlog:gc*` — watch if GC runs frequently but heap keeps growing.
- **Heap dump**: `jmap -dump:format=b,file=heap.hprof <pid>` then analyze with **Eclipse MAT** or **VisualVM**.
- **Profilers**: IntelliJ Profiler, YourKit, JProfiler — identify what's holding references.
- **`-XX:+HeapDumpOnOutOfMemoryError`**: auto-dump on OOM for post-mortem analysis.

---

## 16. Why Do We Need Custom Exceptions?

Java's built-in exceptions (`NullPointerException`, `IllegalArgumentException`, etc.) are generic. Custom exceptions let you express domain-specific error semantics, carry structured data, and allow callers to handle failures precisely.

### Reasons to Create Custom Exceptions

**1. Semantic clarity — the exception name tells you exactly what went wrong**
```java
// Generic — caller has no idea what "illegal argument" means here
throw new IllegalArgumentException("user not found");

// Custom — self-documenting
throw new UserNotFoundException("No user with id: " + userId);
throw new InsufficientFundsException(balance, withdrawalAmount);
throw new ClaimAlreadyProcessedException(claimId);
```

**2. Carry structured domain data**
```java
// A custom exception can hold relevant context — not just a message string
public class InsufficientFundsException extends RuntimeException {
    private final BigDecimal available;
    private final BigDecimal required;

    public InsufficientFundsException(BigDecimal available, BigDecimal required) {
        super(String.format("Insufficient funds: have %.2f, need %.2f", available, required));
        this.available = available;
        this.required = required;
    }

    public BigDecimal getAvailable() { return available; }
    public BigDecimal getRequired() { return required; }
}

// Caller can extract structured data for logging or user-facing error responses
catch (InsufficientFundsException e) {
    log.warn("Transaction rejected: available={}, required={}", e.getAvailable(), e.getRequired());
    return ErrorResponse.of("INSUFFICIENT_FUNDS", e.getMessage());
}
```

**3. Precise catch blocks — handle different failures differently**
```java
try {
    paymentService.process(transaction);
} catch (InsufficientFundsException e) {
    notifyUser("Top up your account"); // user error
} catch (PaymentGatewayTimeoutException e) {
    scheduleRetry(transaction);       // transient error — retry
} catch (FraudDetectedException e) {
    lockAccount(transaction.userId()); // security action
}
// vs. catching a generic Exception and doing brittle string matching on messages
```

**4. Checked vs Unchecked — forcing callers to handle errors**
```java
// Checked: caller MUST handle or declare it — use for recoverable, expected failures
public class PolicyExpiredException extends Exception {
    public PolicyExpiredException(String policyId) {
        super("Policy " + policyId + " has expired");
    }
}

// Unchecked: runtime exception — use for programming errors or unrecoverable failures
public class ConfigurationException extends RuntimeException {
    public ConfigurationException(String key) {
        super("Required configuration key missing: " + key);
    }
}
```

**5. Exception hierarchy for broad catching**
```java
// All domain exceptions extend a common base
public class BankingException extends RuntimeException { ... }
public class InsufficientFundsException extends BankingException { ... }
public class AccountFrozenException extends BankingException { ... }
public class DailyLimitExceededException extends BankingException { ... }

// A global error handler can catch all banking exceptions in one place
@ExceptionHandler(BankingException.class)
public ResponseEntity<ErrorResponse> handleBankingError(BankingException e) {
    return ResponseEntity.status(400).body(ErrorResponse.of(e.getMessage()));
}
```

**Best practices:**
- Extend `RuntimeException` by default (unchecked) unless the failure is expected and recoverable.
- Always include a message. Consider including a cause (`super(message, cause)`) for wrapping lower-level exceptions.
- Keep exception names as nouns ending in `Exception`.
- Don't create a custom exception just to rename a built-in one — it needs to add semantic value or carry additional data.

---

## 17. What is the String Constant Pool?

The String Constant Pool (also called the String Intern Pool) is a special region inside the Java Heap where the JVM stores unique string literals. Its purpose is to avoid creating duplicate `String` objects for equal string values, saving memory.

### How it works

```java
// String literals are automatically interned into the pool
String a = "hello";  // JVM puts "hello" in the pool; 'a' points to it
String b = "hello";  // JVM finds "hello" already in pool; 'b' points to the SAME object

System.out.println(a == b);      // true  — same object reference in pool
System.out.println(a.equals(b)); // true  — same content

// 'new String()' bypasses the pool — always creates a new heap object
String c = new String("hello");  // creates a NEW object on the heap

System.out.println(a == c);      // false — different objects
System.out.println(a.equals(c)); // true  — same content

// intern() manually adds a heap string to the pool and returns the pooled reference
String d = c.intern();
System.out.println(a == d);      // true  — 'd' now points to the pooled "hello"
```

### Memory layout

```
Java Heap
├── String Constant Pool
│   └── "hello"  ←── 'a' and 'b' both point here
│   └── "world"
│
└── Regular Heap
    └── new String("hello")  ←── 'c' points here (duplicate, not pooled)
```

### Why Strings are immutable (and why it enables pooling)

Strings must be immutable for the pool to work safely. If `a` could modify the content of `"hello"`, then `b` (pointing to the same object) would also see the change, breaking the contract. Immutability means:
- The pool is safe to share between threads.
- Strings are safe to use as `HashMap` keys (hashCode doesn't change).
- String literals in code are constants that can be verified at compile time.

### Where the pool lives

- Before Java 7: PermGen (fixed-size, outside the main heap — a common source of `OutOfMemoryError: PermGen space`).
- Java 7+: Moved into the main Heap — now subject to GC and no longer a fixed-size bottleneck.

### Key insight

```java
// This is why you should ALWAYS use .equals() to compare strings, not ==
String input = getUserInput(); // comes from network/DB — NOT pooled
String expected = "admin";    // literal — pooled

input == expected       // WRONG — false even if content matches
input.equals(expected)  // CORRECT — compares content
```

---

## 18. Interface vs Abstract Class

Both define contracts and share code, but they serve different design purposes.

### Key Differences

| | Interface | Abstract Class |
|---|---|---|
| **Purpose** | Define a capability / contract ("can do") | Define a base type with shared behavior ("is a") |
| **Instantiation** | Cannot be instantiated | Cannot be instantiated |
| **Fields** | Only `public static final` constants | Any field: instance, static, private |
| **Methods** | `abstract` (implicit), `default`, `static`, `private` (Java 9+) | Any method: abstract, concrete, private, protected |
| **Constructors** | None | Yes — subclass calls via `super()` |
| **Multiple inheritance** | A class can implement many interfaces | A class can extend only ONE abstract class |
| **Access modifiers** | Methods are `public` by default | Methods can be any modifier |
| **State** | Stateless (no instance variables) | Can carry state (instance fields) |
| **When to use** | When unrelated classes share a behavior | When related classes share code and identity |

### Code Example

```java
// Interface: defines capability — Flyable is something an object CAN DO
// Bird, Plane, and Drone are unrelated classes that all share this capability
public interface Flyable {
    void fly();                             // abstract
    default void land() {                   // default — optional to override
        System.out.println("Landing...");
    }
}

// Abstract class: defines identity — Animal IS A base type
// Dog, Cat, Bird share common state (name) and behavior (eat), but have
// a type relationship — they are all Animals
public abstract class Animal {
    private String name;          // instance field — not possible in interface

    public Animal(String name) {  // constructor — not possible in interface
        this.name = name;
    }

    public String getName() { return name; }  // concrete method

    public abstract String speak(); // must be implemented by subclass
}

// A class can do both: inherit identity AND declare a capability
public class Bird extends Animal implements Flyable {
    public Bird(String name) { super(name); }

    @Override public String speak() { return "Tweet!"; }
    @Override public void fly()    { System.out.println(getName() + " is flying!"); }
}
```

### Decision Rule

- **Use an interface** when you're defining a capability that multiple unrelated classes might share (`Serializable`, `Comparable`, `Runnable`, `Flyable`). Interfaces are also the right choice for defining service contracts in a layered architecture (e.g., `UserRepository`, `PaymentGateway`).

- **Use an abstract class** when you're modeling a type hierarchy where subclasses share state (fields) or template behavior — particularly when you want to use the Template Method Pattern: define the skeleton of an algorithm in the base class and let subclasses fill in the steps.

```java
// Template Method pattern — only possible with abstract class (needs shared state)
public abstract class DataProcessor {
    // Template method — defines the algorithm skeleton
    public final void process() {
        readData();       // step 1
        processData();    // step 2 — subclass-specific
        writeData();      // step 3
    }

    protected abstract void processData(); // subclass fills this in

    private void readData()  { System.out.println("Reading..."); }
    private void writeData() { System.out.println("Writing..."); }
}
```

**Modern Java note:** With `default` methods, interfaces can now provide behavior, blurring the line. The rule of thumb remains: if you need state (instance fields) or constructors, you need an abstract class. If you just need a contract with optional default behavior, prefer an interface for its flexibility (multiple implementation).

---

## 19. How to Avoid Race Conditions

A race condition occurs when two or more threads access shared mutable state concurrently and the final result depends on the unpredictable order of execution.

### Strategy 1: `synchronized` — Mutual Exclusion

```java
public class BankAccount {
    private int balance = 1000;

    // Only one thread can execute this method at a time.
    // Other threads block at the method entry until the lock is released.
    public synchronized void withdraw(int amount) {
        if (balance >= amount) {
            // Without synchronized: two threads could both pass the check
            // then both subtract, producing a negative balance.
            balance -= amount;
        }
    }

    public synchronized int getBalance() { return balance; }
}
```

**Trade-off:** Simple and correct, but `synchronized` is coarse-grained — one thread holds the entire object lock, even for reads.

---

### Strategy 2: `ReentrantLock` — Explicit, Flexible Locking

```java
import java.util.concurrent.locks.ReentrantLock;

public class BankAccount {
    private int balance = 1000;
    // ReentrantLock gives more control: try-lock with timeout, interruptible lock,
    // and separating read/write locks.
    private final ReentrantLock lock = new ReentrantLock();

    public void withdraw(int amount) {
        lock.lock(); // acquire the lock
        try {
            if (balance >= amount) {
                balance -= amount;
            }
        } finally {
            lock.unlock(); // ALWAYS release in finally — even if exception occurs
        }
    }
}
```

---

### Strategy 3: `ReadWriteLock` — Concurrent Reads, Exclusive Writes

```java
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Config {
    private Map<String, String> settings = new HashMap<>();
    // Multiple threads can read simultaneously.
    // A write locks out all readers and other writers.
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public String get(String key) {
        rwLock.readLock().lock();  // shared lock — multiple readers allowed
        try {
            return settings.get(key);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void set(String key, String value) {
        rwLock.writeLock().lock(); // exclusive lock — blocks all readers and writers
        try {
            settings.put(key, value);
        } finally {
            rwLock.writeLock().unlock();
        }
    }
}
```

---

### Strategy 4: Atomic Variables — Lock-Free for Single Variables

```java
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Counter {
    // AtomicInteger uses CPU-level Compare-And-Swap (CAS) instructions.
    // No lock acquisition — much faster than synchronized for simple counters.
    private final AtomicInteger count = new AtomicInteger(0);

    public void increment() {
        count.incrementAndGet(); // atomic: read-modify-write in one instruction
    }

    public int get() {
        return count.get();
    }

    // CAS pattern: only update if the current value matches what we expect
    public boolean compareAndSet(int expected, int newValue) {
        return count.compareAndSet(expected, newValue);
        // If another thread changed the value, this returns false — caller retries
    }
}
```

Other atomics: `AtomicLong`, `AtomicBoolean`, `AtomicReference<T>`, `LongAdder` (better than `AtomicLong` under high contention).

---

### Strategy 5: Thread-Safe Collections

```java
// ConcurrentHashMap: lock-striping — only locks the segment being written to,
// not the entire map. Much better throughput than Collections.synchronizedMap().
Map<String, Integer> scores = new ConcurrentHashMap<>();
scores.put("Alice", 100);
scores.putIfAbsent("Bob", 50);     // atomic check-then-act
scores.computeIfAbsent("Charlie", k -> fetchScore(k)); // atomic

// CopyOnWriteArrayList: every mutation creates a full copy of the array.
// Ideal for read-heavy, write-rare scenarios (e.g., a list of event listeners).
List<String> listeners = new CopyOnWriteArrayList<>();

// BlockingQueue: thread-safe producer-consumer queue with blocking semantics
BlockingQueue<Task> queue = new LinkedBlockingQueue<>(100);
queue.put(task);  // blocks if full
Task t = queue.take(); // blocks if empty
```

---

### Strategy 6: Immutability — No Shared Mutable State

The safest approach: if an object can't be changed after construction, there's nothing to race over.

```java
// Immutable class: all fields final, no setters, defensive copies of mutable inputs
public final class Transaction {
    private final String id;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;

    public Transaction(String id, BigDecimal amount, LocalDateTime timestamp) {
        this.id = id;
        this.amount = amount;           // BigDecimal is already immutable
        this.timestamp = timestamp;     // LocalDateTime is already immutable
    }

    public String getId()              { return id; }
    public BigDecimal getAmount()      { return amount; }
    public LocalDateTime getTimestamp(){ return timestamp; }
    // No setters — object state can never change after construction
}
```

Or in modern Java, use `record` — immutable by default:

```java
public record Transaction(String id, BigDecimal amount, LocalDateTime timestamp) {}
```

---

### Strategy 7: `volatile` — Visibility Without Atomicity

```java
public class FeatureFlag {
    // volatile guarantees that every read sees the most recent write from any thread.
    // It does NOT make compound operations atomic (read-check-write is still a race).
    private volatile boolean enabled = false;

    public void enable()        { enabled = true; }  // single write — safe with volatile
    public boolean isEnabled()  { return enabled; }  // single read — safe with volatile
}

// volatile is NOT enough for:
// enabled = !enabled; // read-modify-write — use AtomicBoolean.compareAndSet() instead
```

### Summary — When to Use What

| Tool | Best For | Atomicity | Performance |
|---|---|---|---|
| `synchronized` | Simple shared state on an object | Method/block level | Medium |
| `ReentrantLock` | Need timeout/interruptible lock | Block level | Medium |
| `ReadWriteLock` | Many readers, few writers | Read/write separation | Good |
| `Atomic*` | Single variable, high-throughput counters | Single variable | Best |
| `ConcurrentHashMap` | Thread-safe map with high concurrency | Entry-level | Best |
| `volatile` | Single flag/reference visibility | Visibility only | Best |
| Immutability | Eliminate shared mutable state entirely | N/A (no mutation) | Best |

---

## 20. `wait()` vs `sleep()`

Both pause thread execution, but they are fundamentally different in purpose, behavior, and who controls them.

### Quick Comparison

| | `wait()` | `sleep()` |
|---|---|---|
| **Defined in** | `Object` | `Thread` |
| **Purpose** | Inter-thread communication / coordination | Pause execution for a fixed duration |
| **Lock behavior** | **Releases** the object monitor lock while waiting | **Holds** all locks — does NOT release anything |
| **Must be in `synchronized`?** | Yes — throws `IllegalMonitorStateException` otherwise | No |
| **Woken up by** | `notify()` / `notifyAll()` from another thread, or timeout | Timeout expires, or `interrupt()` |
| **Checked exception** | `InterruptedException` | `InterruptedException` |
| **Use case** | Producer-consumer, condition variables | Rate limiting, retry delays, polling intervals |

### Code Example — `wait()` / `notify()` (Producer-Consumer)

```java
public class MessageQueue {
    private final Queue<String> queue = new LinkedList<>();
    private final int MAX = 10;

    public synchronized void produce(String msg) throws InterruptedException {
        while (queue.size() == MAX) {
            // Queue is full: release the lock and wait.
            // Another thread (consumer) will call notify() after consuming.
            wait(); // releases lock — consumer can now enter synchronized block
        }
        queue.offer(msg);
        notifyAll(); // wake up waiting consumers
    }

    public synchronized String consume() throws InterruptedException {
        while (queue.isEmpty()) {
            // Queue is empty: release the lock and wait.
            wait(); // releases lock — producer can now enter synchronized block
        }
        String msg = queue.poll();
        notifyAll(); // wake up waiting producers
        return msg;
    }
}
```

### Code Example — `sleep()` (Simple Delay)

```java
public class RetryService {
    public void callWithRetry(Runnable task, int maxRetries) {
        for (int i = 0; i < maxRetries; i++) {
            try {
                task.run();
                return; // success
            } catch (Exception e) {
                try {
                    // Wait 2 seconds before retrying. Does NOT release any locks.
                    // Good for: retry backoff, rate limiting, scheduled polling.
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt(); // restore interrupt flag
                    return;
                }
            }
        }
    }
}
```

### The Critical Distinction — Lock Release

```java
Object lock = new Object();

// Thread A (wait) — releases lock
synchronized (lock) {
    lock.wait(); // lock is RELEASED. Thread B can now acquire it.
}

// Thread B (sleep inside synchronized) — holds lock
synchronized (lock) {
    Thread.sleep(5000); // lock is HELD for 5 full seconds.
                        // Thread A is blocked at 'synchronized (lock)' for this entire time.
}
```

**One-liner:** `sleep()` says "I'm done for now, but I'm keeping my locks." `wait()` says "I'm done for now, please take the lock back and wake me when things change."

### Why `wait()` must be in a `while` loop (not `if`)

```java
// WRONG — susceptible to spurious wakeups
synchronized (lock) {
    if (queue.isEmpty()) {
        lock.wait(); // might wake up spuriously even with nothing in the queue
    }
    process(queue.poll()); // NullPointerException if spurious wakeup!
}

// CORRECT — re-check the condition after every wakeup
synchronized (lock) {
    while (queue.isEmpty()) { // loop re-evaluates the condition
        lock.wait();
    }
    process(queue.poll()); // safe — we know queue is non-empty
}
```

The JVM specification permits **spurious wakeups** — `wait()` may return even without `notify()`. Always guard `wait()` with a `while` loop checking the actual condition.