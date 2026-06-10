## 1. What is AOP?

AOP (Aspect-Oriented Programming) is a programming paradigm used to separate **cross-cutting concerns** from business logic.

Common cross-cutting concerns include:

- Logging
- Transaction Management
- Security
- Auditing
- Performance Monitoring
- Exception Handling

Instead of duplicating these concerns across multiple classes, AOP allows them to be implemented in a centralized way and applied automatically.

Example:

```java
@Transactional
public void placeOrder() {
    // business logic
}
```

Transaction management is handled through Spring AOP.

---

## 2. What are JoinPoint and Aspect in AOP?

### JoinPoint

A JoinPoint is a point during program execution where an aspect can be applied.

In Spring AOP, a JoinPoint is typically a method execution.

### Aspect

An Aspect is a class that contains cross-cutting logic (advice).

Example:

```java
@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* com.example.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("Calling method: " +
                joinPoint.getSignature().getName());
    }
}
```

In this example:

- `LoggingAspect` is the Aspect.
- The execution of service methods are JoinPoints.

---

## 3. Explain Aspect, JoinPoint, Pointcut, Advice, and Target

### Aspect

A module that encapsulates cross-cutting concerns.

Example:

```java
@Aspect
public class LoggingAspect {
}
```

### JoinPoint

A specific point in execution where an aspect can be applied.

Example:

- Method execution
- Constructor execution

In Spring AOP, only method execution JoinPoints are supported.

### Pointcut

A Pointcut defines which JoinPoints should be intercepted.

Example:

```java
execution(* com.company.service.*.*(..))
```

This expression matches all methods in the service package.

### Advice

The action executed at a matched JoinPoint.

Types of Advice:

- `@Before`
- `@After`
- `@AfterReturning`
- `@AfterThrowing`
- `@Around`

Example:

```java
@Before("execution(* com.company.service.*.*(..))")
public void logBefore() {
    System.out.println("Before method execution");
}
```

### Target

The actual object being advised (proxied by Spring).

Example:

```java
@Service
public class OrderService {
}
```

`OrderService` is the Target object.

---

## 4. ApplicationContext vs BeanFactory

### BeanFactory

The basic Spring IoC container.

Responsibilities:

- Bean creation
- Dependency injection
- Lazy initialization by default

Example:

```java
BeanFactory factory =
    new XmlBeanFactory(new ClassPathResource("beans.xml"));
```

### ApplicationContext

An advanced container that extends BeanFactory.

Additional features:

- Event publishing
- Internationalization (i18n)
- AOP support
- Annotation-based configuration
- Automatic BeanPostProcessors
- Easier integration with Spring Boot

Example:

```java
ApplicationContext context =
    new AnnotationConfigApplicationContext(AppConfig.class);
```

### Key Differences

| Feature | BeanFactory | ApplicationContext |
|----------|------------|-------------------|
| Dependency Injection | ✅ | ✅ |
| Lazy Initialization | ✅ | ❌ (Singletons initialized eagerly) |
| Event Support | ❌ | ✅ |
| Internationalization | ❌ | ✅ |
| AOP Integration | Limited | Full |
| Enterprise Usage | Rare | Common |

**Interview Answer:**

> BeanFactory is the basic IoC container with lazy loading. ApplicationContext extends BeanFactory and provides enterprise-level features such as event handling, AOP integration, and annotation support. In modern Spring applications, ApplicationContext is almost always used.

---

## 5. How Does Spring MVC Work?

### Spring MVC Request Flow

1. Client sends HTTP request.
2. `DispatcherServlet` receives the request.
3. `HandlerMapping` identifies the appropriate controller.
4. `HandlerAdapter` invokes the controller method.
5. Controller executes business logic.
6. Controller returns:
    - View name (MVC application), or
    - Response body (REST API).
7. `ViewResolver` resolves the view (if applicable).
8. Response is sent back to the client.

### Example

```java
@RestController
@RequestMapping("/orders")
public class OrderController {

    @GetMapping("/{id}")
    public OrderDto getOrder(@PathVariable Long id) {
        return orderService.getOrder(id);
    }
}
```

For REST APIs:

- Controller returns Java objects.
- `HttpMessageConverter` converts objects to JSON.
- Response is returned to the client.

### Important Components

- DispatcherServlet
- HandlerMapping
- HandlerAdapter
- Controller
- ViewResolver
- HttpMessageConverter

---

## 6. How Does `@Autowired` Work?

`@Autowired` tells Spring to automatically inject a dependency from the Spring container.

Spring resolves dependencies primarily by:

1. Type
2. Qualifier (if specified)
3. Bean name
4. `@Primary`

### Constructor Injection Example

```java
@Service
public class OrderService {

    private final PaymentService paymentService;

    @Autowired
    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

### Why Constructor Injection?

- Mandatory dependencies
- Immutable fields
- Easier unit testing
- Better design

**Interview Answer:**

> `@Autowired` performs dependency injection by resolving beans from the Spring container, primarily by type. Constructor injection is preferred because it promotes immutability and testability.

---

## 7. `@Autowired` vs `@Qualifier`

### Problem: Multiple Implementations

```java
public interface PaymentService {
    void pay();
}
```

```java
@Service("stripePaymentService")
public class StripePaymentService implements PaymentService {
}
```

```java
@Service("paypalPaymentService")
public class PaypalPaymentService implements PaymentService {
}
```

Now Spring finds two matching beans:

```java
@Autowired
private PaymentService paymentService;
```

This causes:

```text
NoUniqueBeanDefinitionException
```

### Solution: `@Qualifier`

```java
@Service
public class OrderService {

    private final PaymentService paymentService;

    public OrderService(
        @Qualifier("stripePaymentService")
        PaymentService paymentService) {

        this.paymentService = paymentService;
    }
}
```

### Difference

| Annotation | Purpose |
|------------|---------|
| `@Autowired` | Inject bean by type |
| `@Qualifier` | Specify which bean to inject |

**Interview Answer:**

> `@Autowired` injects dependencies by type. When multiple beans of the same type exist, `@Qualifier` is used to explicitly specify which bean should be injected.

---

## 8. Two Use Cases for `@PostConstruct` and `@PreDestroy`

### `@PostConstruct`

Executed after dependency injection is completed and before the bean is available for use.

Example:

```java
@PostConstruct
public void init() {
    cache.loadInitialData();
}
```

### Common Use Cases

#### 1. Cache Initialization

```java
@PostConstruct
public void initializeCache() {
    cacheService.preload();
}
```

#### 2. Configuration Validation

```java
@PostConstruct
public void validateConfig() {
    if(apiKey == null) {
        throw new IllegalStateException("API Key missing");
    }
}
```

---

### `@PreDestroy`

Executed before the bean is removed from the Spring container.

Example:

```java
@PreDestroy
public void cleanup() {
    executorService.shutdown();
}
```

### Common Use Cases

#### 1. Release Resources

```java
@PreDestroy
public void closeResources() {
    databaseConnection.close();
}
```

#### 2. Stop Background Tasks

```java
@PreDestroy
public void stopScheduler() {
    scheduler.shutdown();
}
```