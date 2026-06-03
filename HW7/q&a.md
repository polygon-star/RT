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

Reflection is the ability of a program to **inspect and manipulate its own structure at runtime** examining classes, fields, methods, constructors, and annotations without knowing them at compile time.

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

**Key distinctions to remember in interviews:**
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

**summary:** POST is for creation; PATCH is for surgical partial updates. If you don't want to send the entire resource just to change one field, use PATCH.

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

**summary:** Use POST when the server owns the identity of the new resource. Use PUT when the client knows the exact URL and wants to do a full replacement. Accidentally using POST twice creates duplicates; PUT is safe to retry.

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