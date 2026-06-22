## Design Pattern Use Cases by Industry

### Singleton Pattern

| Industry | Use Case |
|---|---|
| **Banking** | A single database connection pool manager shared across all transaction services — ensures consistent connection reuse and prevents resource exhaustion |
| **E-Commerce** | A single shopping cart session manager per user session — ensures cart state is consistent across all pages without duplicating session objects |
| **Health Insurance** | A single configuration manager that loads policy rules and premium tables once at startup — all claim-processing services read from the same in-memory config |

### Factory Pattern

| Industry | Use Case |
|---|---|
| **Banking** | A loan factory that creates different loan objects (MortgageLoan, AutoLoan, PersonalLoan) based on the loan type requested — the caller just says "give me a loan" without knowing the construction details |
| **E-Commerce** | A payment processor factory that returns the correct payment handler (CreditCardProcessor, PayPalProcessor, CryptoProcessor) based on the user's selected payment method |
| **Health Insurance** | A claim factory that creates different claim objects (DentalClaim, VisionClaim, MedicalClaim) based on claim type — each has different validation rules but shares a common interface |

### Builder Pattern

| Industry | Use Case |
|---|---|
| **Banking** | Building a complex loan application object step by step — applicant info, credit score, collateral, co-signer, loan terms — without a constructor with 15 parameters |
| **E-Commerce** | Building an order object incrementally — add items, apply coupon, set shipping address, choose delivery speed, add gift message — before finalizing and submitting |
| **Health Insurance** | Building an insurance policy object with optional riders, coverage limits, deductibles, and beneficiary details — each field is optional and the builder prevents invalid combinations |

### Proxy Pattern

| Industry | Use Case |
|---|---|
| **Banking** | A security proxy that sits in front of account services — intercepts every request, verifies the JWT token, checks authorization level, then forwards to the real service if permitted |
| **E-Commerce** | A caching proxy for product catalog service — checks Redis cache first before calling the real product database, reducing load during high-traffic sales events |
| **Health Insurance** | A logging proxy that wraps all claim submission services — transparently records every claim request and response for audit compliance without modifying the core business logic |

---

## 1. What is Microservice Architecture

Microservice architecture is a software design approach where an application is built as a collection of **small, independently deployable services**, each responsible for a specific business capability.

Each service:
- Runs in its own process
- Has its own database (loose coupling)
- Communicates over a network via REST, gRPC, or messaging queues
- Can be deployed, scaled, and updated independently

Example: An e-commerce app broken into `OrderService`, `PaymentService`, `InventoryService`, `NotificationService` — each a separate deployable unit.

---

## 2. Why Microservice Architecture

| Problem with Monolith | How Microservices Solves It |
|---|---|
| One bug can crash the whole app | Failure is isolated to one service |
| Must redeploy everything for a small change | Deploy only the changed service |
| Hard to scale one bottleneck feature | Scale only the service that needs it |
| One tech stack for everything | Each service can use the best tool for its job |
| Large team steps on each other's code | Teams own and work on separate services independently |

**When NOT to use microservices:** small teams, early-stage products, or simple domains — the overhead isn't worth it.

---

## 3. Microservice Main Components

```
Client
  │
  ▼
API Gateway          ← single entry point, routing, auth, rate limiting
  │
  ├──► Service A ◄──── Service Discovery (Eureka)
  ├──► Service B         registers & finds services by name
  └──► Service C
         │
         ▼
    Message Queue (Kafka/RabbitMQ)   ← async communication
         │
         ▼
    Service D (consumer)

Config Server        ← centralized configuration for all services
Distributed Tracing  ← track requests across services (Zipkin)
Centralized Logging  ← aggregate logs from all services (ELK)
Circuit Breaker      ← prevent cascading failures (Resilience4j)
```

**Core components summary:**
- **API Gateway** — single entry point for all clients
- **Service Discovery** — services register themselves and find each other dynamically
- **Config Server** — one place to manage all service configurations
- **Message Queue** — async, decoupled communication between services
- **Circuit Breaker** — stops calling a failing service to prevent cascade
- **Distributed Tracing** — follow a request as it travels through multiple services
- **Centralized Logging** — aggregate and search logs across all services

---

## 4. Key Terms Deep Dive

---

### 4.1 Config Server

**What it is:** A centralized place to store and serve configuration (application.yml / .properties) for all microservices. Instead of each service having its own config file baked in, they all pull config from one server at startup.

**Why:** Change a config value in one place and all services pick it up — no redeployment needed.

**Spring Cloud Config Server setup:**

```java
// Config Server main class
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
```

```yaml
# Config Server application.yml
server:
  port: 8888

spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/your-org/config-repo  # configs stored here
```

```yaml
# Client microservice bootstrap.yml
spring:
  application:
    name: order-service
  config:
    import: optional:configserver:http://localhost:8888
```

The config repo on Git would have a file `order-service.yml` with that service's config. The service fetches it on startup.

---

### 4.2 Service Discovery — Eureka

**What it is:** A registry where all microservices register themselves when they start up. Other services look up the registry to find the address of a service instead of hardcoding IPs.

**Why:** In a dynamic environment (Docker, cloud), IPs change constantly. Service discovery handles this automatically.

```java
// Eureka Server
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

```yaml
# Eureka Server application.yml
server:
  port: 8761

eureka:
  client:
    register-with-eureka: false   # server doesn't register itself
    fetch-registry: false
```

```java
// Microservice client
@SpringBootApplication
@EnableDiscoveryClient
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
```

```yaml
# Client application.yml
spring:
  application:
    name: order-service   # this is how other services find it

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

Now `PaymentService` can call `order-service` by name — Eureka resolves the actual address.

**Consul and Zookeeper** serve the same purpose but are more general-purpose tools — Consul also handles health checking and key-value config; Zookeeper is used more in Kafka ecosystems.

---

### 4.3 Zipkin and Sleuth — Distributed Tracing

**What it is:**
- **Sleuth** — automatically adds a unique `traceId` and `spanId` to every request and propagates them across service calls
- **Zipkin** — collects and visualizes those traces so you can see the full journey of a request across services

**Why:** Without tracing, debugging a failure across 5 services means hunting through 5 separate log files manually.

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-sleuth-zipkin</artifactId>
</dependency>
```

```yaml
# application.yml
spring:
  zipkin:
    base-url: http://localhost:9411   # Zipkin server address
  sleuth:
    sampler:
      probability: 1.0   # trace 100% of requests (use 0.1 in prod)
```

Sleuth automatically injects trace info into logs:
```
INFO [order-service,traceId=abc123,spanId=def456] Order created
INFO [payment-service,traceId=abc123,spanId=ghi789] Payment processed
```

Same `traceId` across both services — you can search `abc123` in Zipkin UI and see the full call chain.

---

### 4.4 Ribbon — Client-Side Load Balancer

**What it is:** Instead of a central load balancer routing requests, Ribbon runs **inside the client service** and decides which instance to send a request to.

**Why:** When `OrderService` has 3 instances running, Ribbon (inside `PaymentService`) round-robins across them automatically.

> **Note:** Ribbon is now in maintenance mode. Spring Cloud replaced it with **Spring Cloud LoadBalancer**. But Ribbon still appears in older codebases and interviews.

```yaml
# application.yml (Ribbon config)
order-service:
  ribbon:
    listOfServers: localhost:8081,localhost:8082,localhost:8083
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RoundRobinRule
```

With Eureka, you don't even need to list servers manually — Ribbon fetches the instance list from the registry automatically:

```java
// With @LoadBalanced, RestTemplate uses Ribbon/LoadBalancer automatically
@Bean
@LoadBalanced
public RestTemplate restTemplate() {
    return new RestTemplate();
}

// Then call by service name, not IP
restTemplate.getForObject("http://order-service/orders/1", Order.class);
```

---

### 4.5 ELK Stack — Centralized Logging

**What it is:** Three tools working together:
- **Elasticsearch** — stores and indexes log data, makes it searchable
- **Logstash** — collects, parses, and transforms logs from all services and ships them to Elasticsearch
- **Kibana** — a web UI to search, filter, and visualize logs stored in Elasticsearch

**Why:** With 10 microservices each writing their own logs, you need one place to search across all of them.

**Flow:**
```
Microservice logs
      │
      ▼
  Logstash (collect & parse)
      │
      ▼
  Elasticsearch (store & index)
      │
      ▼
  Kibana (search & visualize)
```

**Spring Boot setup — send logs to Logstash:**

```xml
<!-- pom.xml -->
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>7.3</version>
</dependency>
```

```xml
<!-- logback-spring.xml -->
<appender name="LOGSTASH" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
    <destination>localhost:5044</destination>
    <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
</appender>
```

Now all logs are shipped to Logstash in JSON format, indexed in Elasticsearch, and searchable in Kibana. You can filter by `traceId` (from Sleuth) to find all logs for one request across all services.

---

### 4.6 Circuit Breaker — Hystrix → Resilience4j

**What it is:** A pattern that monitors calls to a downstream service. If failures exceed a threshold, the circuit "opens" and stops sending requests to the failing service — returning a fallback response instead.

**Three states:**
- **Closed** — normal operation, requests pass through
- **Open** — too many failures, requests are blocked, fallback returned immediately
- **Half-Open** — after a wait period, lets a few test requests through to check if service recovered

**Why Resilience4j instead of Hystrix:** Netflix put Hystrix in maintenance mode in 2018. Resilience4j is the modern replacement.

```xml
<!-- pom.xml -->
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot2</artifactId>
</dependency>
```

```yaml
# application.yml
resilience4j:
  circuitbreaker:
    instances:
      paymentService:
        failure-rate-threshold: 50          # open circuit if 50% of calls fail
        wait-duration-in-open-state: 10s    # wait 10s before trying again
        sliding-window-size: 10             # evaluate last 10 calls
```

```java
@Service
public class OrderService {

    @CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
    public String processPayment(Order order) {
        // calls PaymentService
        return restTemplate.postForObject("http://payment-service/pay", order, String.class);
    }

    // called automatically when circuit is open
    public String paymentFallback(Order order, Exception e) {
        return "Payment service is temporarily unavailable. Your order is queued.";
    }
}
```

**Resilience4j also includes:**
- `@Retry` — retry a failed call with backoff
- `@RateLimiter` — limit how many calls per second
- `@Bulkhead` — limit concurrent calls to prevent one service from consuming all threads
- `@TimeLimiter` — timeout if a call takes too long