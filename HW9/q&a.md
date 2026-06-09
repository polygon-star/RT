## 1. What is Spring IoC?

**IoC (Inversion of Control)** is a design principle where the control of object creation and dependency management is transferred from application code to the Spring framework.

Without IoC:

```java
EmailService emailService = new EmailService();
OrderService orderService = new OrderService(emailService);
```

With IoC:

```java
@Service
public class OrderService {
    private final EmailService emailService;

    public OrderService(EmailService emailService) {
        this.emailService = emailService;
    }
}
```

Spring creates and injects the dependencies automatically.

### Interview Answer

> IoC means that instead of my application creating and managing objects, Spring manages the lifecycle of those objects and provides them when needed.

---

# 2. What is IoC Container?

The **IoC Container** is the core Spring component responsible for:

* Creating beans
* Managing bean lifecycle
* Injecting dependencies
* Configuring objects

Common container:

```java
ApplicationContext
```

Example:

```java
ApplicationContext context =
        SpringApplication.run(App.class, args);
```

### Interview Answer

> The IoC container acts as an object factory and lifecycle manager. It creates beans, wires dependencies, and manages their lifecycle throughout the application.

---

# 3. Advantages of IoC

## Loose Coupling

Classes depend on interfaces instead of concrete implementations.

## Better Testability

Dependencies can be mocked easily.

```java
OrderService service =
        new OrderService(mockEmailService);
```

## Easier Maintenance

Object creation logic is centralized.

## Reusability

Components can be reused independently.

## Lifecycle Management

Spring handles initialization and destruction.

### Interview Answer

> The biggest advantage of IoC is loose coupling, which improves maintainability, scalability, and testability.

---

# 4. What is Dependency Injection (DI)?

Dependency Injection is Spring's implementation of IoC.

Instead of creating dependencies internally:

```java
private EmailService emailService =
        new EmailService();
```

Dependencies are injected:

```java
public OrderService(EmailService emailService) {
    this.emailService = emailService;
}
```

### Interview Answer

> Dependency Injection is the process of supplying dependencies to a class from an external source rather than allowing the class to create them itself.

---

# 5. Dependency Injection Demo

```java
@Component
class EmailSender {

    void send(String message) {
        System.out.println("Email sent: " + message);
    }
}

@Service
class OrderService {

    private final EmailSender emailSender;

    public OrderService(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void placeOrder() {
        emailSender.send("Order placed");
    }
}

@RestController
class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders")
    public String createOrder() {
        orderService.placeOrder();
        return "Success";
    }
}
```

### Flow

```text
OrderController
        ↓
OrderService
        ↓
EmailSender
```

Spring creates all objects and injects dependencies automatically.

---

# 6. Types of Dependency Injection

## Constructor Injection

```java
@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }
}
```

## Setter Injection

```java
@Service
public class UserService {

    private UserRepository repository;

    @Autowired
    public void setRepository(UserRepository repository) {
        this.repository = repository;
    }
}
```

## Field Injection

```java
@Service
public class UserService {

    @Autowired
    private UserRepository repository;
}
```

---

# 7. Pros and Cons of DI Types

## Constructor Injection

### Pros

* Immutable dependencies
* Easy unit testing
* Clear required dependencies
* Recommended by Spring

### Cons

* Large constructors may indicate poor design

---

## Setter Injection

### Pros

* Good for optional dependencies
* Allows dependency replacement

### Cons

* Object may be partially initialized

---

## Field Injection

### Pros

* Less code

### Cons

* Hard to unit test
* Hidden dependencies
* Not recommended for production

### Senior Recommendation

> Always prefer Constructor Injection unless there is a strong reason to use Setter Injection.

---

# 8. @Component vs @Bean

## @Component

Used on classes that Spring automatically discovers.

```java
@Component
public class EmailService {
}
```

---

## @Bean

Used inside configuration classes.

```java
@Configuration
public class AppConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
```

### When to Use

| Annotation | Use Case                                     |
| ---------- | -------------------------------------------- |
| @Component | Classes you own                              |
| @Bean      | Third-party classes or custom initialization |

### Interview Answer

> Use @Component when Spring can auto-scan the class. Use @Bean when I need full control over object creation.

---

# 9. What is @Configuration and @ComponentScan?

## @Configuration

Marks a configuration class.

```java
@Configuration
public class AppConfig {
}
```

---

## @ComponentScan

Tells Spring where to find components.

```java
@Configuration
@ComponentScan("com.example")
public class AppConfig {
}
```

### Interview Answer

> @Configuration defines bean configuration, while @ComponentScan tells Spring where to discover components automatically.

---

# 10. @Controller vs @RestController

## @Controller

Used for MVC applications returning views.

```java
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }
}
```

Returns:

```text
home.jsp
```

---

## @RestController

Used for REST APIs.

```java
@RestController
public class UserController {

    @GetMapping("/users")
    public List<User> getUsers() {
        return List.of();
    }
}
```

Returns:

```json
[
  {
    "id": 1
  }
]
```

### Interview Answer

> @RestController is equivalent to @Controller + @ResponseBody.

---

# 11. @Controller vs @Service vs @Repository

## @Controller

Handles HTTP requests.

```java
@Controller
```

---

## @Service

Contains business logic.

```java
@Service
```

---

## @Repository

Handles database access.

```java
@Repository
```

### Architecture

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

### Interview Answer

> These annotations help organize responsibilities according to layered architecture.

---

# 12. Spring Bean Scope

Spring supports multiple bean scopes.

Common scopes:

```text
singleton
prototype
request
session
```

---

# 13. Singleton vs Prototype

## Singleton

Default scope.

```java
@Scope("singleton")
```

Only one instance exists.

### Example

```java
UserService
EmailService
PaymentService
```

---

## Prototype

Creates a new object every request.

```java
@Scope("prototype")
```

### Example

```java
ReportBuilder
PdfGenerator
TemporaryCalculationObject
```

### Interview Answer

> Singleton is ideal for stateless services. Prototype is useful when each consumer requires its own independent state.

---

# 14. Bean Scope Use Cases

## Singleton Scope

### Use Case 1

UserService

### Use Case 2

PaymentService

### Use Case 3

EmailService

---

## Prototype Scope

### Use Case 1

ReportBuilder

### Use Case 2

PDFGenerator

### Use Case 3

WorkflowExecutor

---

## Request Scope

One bean per HTTP request.

### Use Case 1

RequestContext

### Use Case 2

CorrelationIdHolder

### Use Case 3

RequestValidationState

---

## Session Scope

One bean per user session.

### Use Case 1

ShoppingCart

### Use Case 2

LoggedInUserState

### Use Case 3

MultiStepCheckoutWizard

---

# 15. Session vs Cookie

## Cookie

Stored in browser.

Example:

```text
theme=dark
language=en
```

---

## Session

Stored on server.

Example:

```text
Shopping Cart
User Login State
Checkout Information
```

---

## Comparison

| Feature     | Cookie      | Session                |
| ----------- | ----------- | ---------------------- |
| Storage     | Client      | Server                 |
| Security    | Less Secure | More Secure            |
| Size        | Small       | Larger                 |
| Lifetime    | Can Persist | Usually Temporary      |
| Performance | Faster      | Requires Server Memory |

### Interview Answer

> Cookies are stored on the client side, while sessions are stored on the server side. In most Java web applications, the browser stores a session ID cookie, and the server uses that ID to retrieve session data.
