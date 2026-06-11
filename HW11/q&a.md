# Why Spring Boot? Pros and Cons

Spring Boot simplifies Spring application development by providing auto-configuration, embedded servers, starter dependencies, and production-ready features.

## Pros

- Reduces boilerplate configuration
- Embedded servers (Tomcat, Jetty, Undertow)
- Starter dependencies simplify dependency management
- Rapid application development
- Production-ready monitoring with Actuator
- Strong ecosystem and community support
- Ideal for microservices and REST APIs

Example starter dependencies:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

## Cons

- Can hide underlying Spring configurations
- Larger memory footprint than lightweight frameworks
- Auto-configuration may be difficult to debug
- Startup time can increase with many dependencies

---

# How to Start a Spring Boot Project from Scratch

## Step 1: Generate Project

Use Spring Initializr and select:

- Java
- Maven or Gradle
- Spring Boot version
- Dependencies:
    - Spring Web
    - Spring Data JPA
    - Database Driver
    - Validation
    - Actuator
    - Lombok (optional)

## Step 2: Main Class

```java
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

## Step 3: Configure Database

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/demo
spring.datasource.username=root
spring.datasource.password=password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## Step 4: Create Layers

```text
controller
service
repository
entity
dto
exception
config
```

---

# @Controller vs @RestController

## @Controller

Used for MVC applications that return views.

```java
@Controller
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "home";
    }
}
```

## @RestController

Used for REST APIs.

```java
@RestController
public class UserController {

    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.getUsers();
    }
}
```

Equivalent to:

```java
@Controller
@ResponseBody
```

---

# @PathVariable vs @RequestParam

## @PathVariable

Reads values from URL path.

```java
@GetMapping("/users/{id}")
public User getUser(@PathVariable Long id) {
    return userService.getUser(id);
}
```

Request:

```text
GET /users/10
```

## @RequestParam

Reads values from query parameters.

```java
@GetMapping("/users")
public List<User> searchUsers(
        @RequestParam String name) {

    return userService.search(name);
}
```

Request:

```text
GET /users?name=Tom
```

### Common Use Cases

```text
@PathVariable -> Resource identification
@RequestParam -> Filtering, Sorting, Pagination
```

---

# @RequestBody vs @ResponseBody

## @RequestBody

Converts JSON request body into Java objects.

```java
@PostMapping("/users")
public User createUser(
        @RequestBody CreateUserRequest request) {

    return userService.createUser(request);
}
```

Request:

```json
{
  "name": "Tom",
  "email": "tom@example.com"
}
```

## @ResponseBody

Converts Java objects into JSON response.

```java
@ResponseBody
@GetMapping("/users/{id}")
public User getUser(@PathVariable Long id) {
    return userService.getUser(id);
}
```

`@RestController` automatically applies `@ResponseBody`.

---

# GetMapping, PostMapping, PutMapping, DeleteMapping, RequestMapping

## @RequestMapping

General-purpose mapping.

```java
@RequestMapping("/api/users")
```

Usually used at class level.

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
}
```

## @GetMapping

Retrieve data.

```java
@GetMapping("/{id}")
public User getUser(@PathVariable Long id) {
    return userService.getUser(id);
}
```

## @PostMapping

Create data.

```java
@PostMapping
public User createUser(
        @RequestBody CreateUserRequest request) {

    return userService.createUser(request);
}
```

## @PutMapping

Update data.

```java
@PutMapping("/{id}")
public User updateUser(
        @PathVariable Long id,
        @RequestBody UpdateUserRequest request) {

    return userService.updateUser(id, request);
}
```

## @DeleteMapping

Delete data.

```java
@DeleteMapping("/{id}")
public void deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
}
```

---

# What is Spring Actuator?

Spring Boot Actuator provides monitoring and management endpoints.

Dependency:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Common Endpoints:

```text
/actuator/health
/actuator/info
/actuator/metrics
/actuator/env
/actuator/beans
/actuator/loggers
```

Configuration:

```properties
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
```

Benefits:

- Health monitoring
- Metrics collection
- Production troubleshooting
- Kubernetes readiness/liveness checks

---

# How to Achieve Async in Spring Boot

Enable async support:

```java
@Configuration
@EnableAsync
public class AsyncConfig {
}
```

Use `@Async`:

```java
@Service
public class EmailService {

    @Async
    public void sendEmail(String email) {
        System.out.println("Sending email...");
    }
}
```

Return `CompletableFuture`:

```java
@Async
public CompletableFuture<String> process() {
    return CompletableFuture.completedFuture("Done");
}
```

Custom thread pool:

```java
@Bean
public Executor taskExecutor() {

    ThreadPoolTaskExecutor executor =
            new ThreadPoolTaskExecutor();

    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(100);

    executor.initialize();

    return executor;
}
```

---

# How Does Spring Handle Exceptions?

Global exception handling:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(
            UserNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }
}
```

Custom exception:

```java
public class UserNotFoundException
        extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
```

Benefits:

- Centralized error handling
- Consistent API responses
- Cleaner controllers

---

# How Does Spring Validate Data?

DTO:

```java
public class CreateUserRequest {

    @NotBlank
    private String name;

    @Email
    private String email;

    @Min(18)
    private int age;
}
```

Controller:

```java
@PostMapping("/users")
public User createUser(
        @Valid @RequestBody CreateUserRequest request) {

    return userService.createUser(request);
}
```

Common Validation Annotations:

```text
@NotNull
@NotBlank
@NotEmpty
@Email
@Min
@Max
@Size
@Pattern
```

Handle validation errors:

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<?> handleValidation(
        MethodArgumentNotValidException ex) {

    return ResponseEntity.badRequest().build();
}
```

---

# How Does Spring Do Logging?

Spring Boot uses:

```text
SLF4J + Logback
```

Example:

```java
private static final Logger log =
        LoggerFactory.getLogger(UserService.class);

log.info("User created");

log.warn("User not found");

log.error("Database error", ex);
```

Log Levels:

```text
TRACE
DEBUG
INFO
WARN
ERROR
```

Configuration:

```properties
logging.level.root=INFO
logging.level.com.company=DEBUG
logging.file.name=application.log
```

---

# Cache Hit vs Cache Miss

## Cache Hit

Data exists in cache.

```text
Request
  ↓
Redis
  ↓
Return Data
```

Fast response.

## Cache Miss

Data not found in cache.

```text
Request
  ↓
Redis
  ↓
Not Found
  ↓
Database
  ↓
Save to Cache
  ↓
Return Data
```

Slower response.

Benefits of caching:

- Lower latency
- Reduced database load
- Improved scalability

---

# Redis Basics

Redis is an in-memory key-value datastore.

Common Uses:

- Caching
- Session Storage
- Distributed Locking
- Rate Limiting
- Pub/Sub Messaging
- Queues

Common Data Structures:

```text
String
Hash
List
Set
Sorted Set
Stream
Bitmap
```

Example:

```java
redisTemplate.opsForValue()
             .set("user:1", user);
```

Retrieve:

```java
User user =
    redisTemplate.opsForValue()
                 .get("user:1");
```

Typical Cache Flow:

```text
Request
  ↓
Redis
  ↓
Hit? → Return Data
  ↓
Miss
  ↓
Database
  ↓
Store in Redis
  ↓
Return Data
```

Common Redis Challenges:

- Cache Penetration
- Cache Breakdown
- Cache Avalanche
- Cache Invalidation

---

# @RestControllerAdvice vs @ControllerAdvice

## @ControllerAdvice

Used for global exception handling across controllers.

```java
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
}
```

## @RestControllerAdvice

Equivalent to:

```java
@ControllerAdvice
@ResponseBody
```

Example:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(
            UserNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        "USER_NOT_FOUND",
                        ex.getMessage()));
    }
}
```

Error DTO:

```java
public record ErrorResponse(
        String code,
        String message
) {}
```

### When to Use

```text
@ControllerAdvice
    → MVC applications returning views

@RestControllerAdvice
    → REST APIs returning JSON
```