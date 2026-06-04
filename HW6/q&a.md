# Web development

---

### s3 link
https://qa-walkthrough-01.s3.us-east-2.amazonaws.com/2026-06-04%2016-47-15.mp4?response-content-disposition=inline&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Security-Token=IQoJb3JpZ2luX2VjEJX%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaCXVzLWVhc3QtMiJHMEUCIHiu1m86w8CizzesEHMeLpUJxVQh2WlvP5fv3MVNP44tAiEAxpeAnNo%2BSUuZ8c7AQB7W1DBuL%2FWb6%2BG96SpcxO1sz7oqtQMIXhAAGgwwMzAxNzkzMTA1ODgiDIHFd8JrV3RK8BhlTSqSA6M7NBpbObUbeHhRWGpYfIc7vAOoqTF3bF9hTL1Y0oFpemM7kWvL4OxxE7%2FUgUrdIiunelUsQOO4g%2BucwCDsGX1dc6cTZCu8bGwYc%2BYvYfIVxjTL4xC%2B1O1ObHpKyAkfNqPAaq5iNmCv0xwO7S63jaXJGpgSylDYS7oYJnnDGToHPNC%2FqirVTj8tyoQNLv3h%2F%2F5exK5gQ3CW1VgWjoMpOkGQQ%2FTuXV%2FwpMgLpKGCSvE2XIw4e7zFqcFHWpQd3jxK0trzdTxAjjmMfEzcwRS1yL2iBlBhCMKtRfVptdz0vFENig0eEOSoxNKt0eYqJ1M16DonXifx65NscEVryrxzwefa8%2FSoqSfTI%2BAvkBiCnHOMfjTCxFqN%2BVY0DSVPBgF1I9lw27EqATwD%2BOUuGZHjRrBm0iKyd%2BBtFW46fTX5be5v0vis1P8PvOJsbbuIsLWZLbJLBxWs9WCzY1YoquFdAB92S9oL87i6gWx51NAHTfm62ODpPwld1CdyQKoPcygHYjez9c5w8azVHVQSb2ehJ9zkpjCt0ofRBjreAojerw1S4C6vQfcIuVmXbrg01v6TdJqPcFCDhw2qhTtENDV1XofxUKZqjfCCIEcF1E6Yp08PMezLv2EbcGpzOWikGyLjsTi57S3w6bXV%2ByqM5%2F91sVdKWjibR%2F4o1eoX0R%2FBnY%2FMUAT5gAm8s3m89bJz4eeAgo9XcmjFsNtiS7HolZqXVlWncMfT44JW5js8VSijqPZxfna3ZlqHze0%2ByHmfV7YRzCal9wObL%2FG3Rp1D3rAVxX8vQKY%2B1pjCef5Eu%2FM%2FS0bRepQVeY7BOfCnRA9Rhz8V3LWS0YEymbQCBsuToD2Q66wEW3xmc2jOlwViOEpg0RXjia6p8cBdquigpZ9q8fJoFMkBdMQH%2B4KgnYuXcRPyuu8MXMBaGi%2B8w%2Fx69UVC%2B7tDg6iNqSNZhjryJyYACPSXyvJGSUCQ%2FmHC4xfVvxGkjkBykNACbuS0h5uSJEUXzxAVUrv%2FbXlgcWig&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=ASIAQOBWTXP6O5V5XAGG%2F20260604%2Fus-east-2%2Fs3%2Faws4_request&X-Amz-Date=20260604T210951Z&X-Amz-Expires=43200&X-Amz-SignedHeaders=host&X-Amz-Signature=9b85445a129a4521c1bc66ca6b64466119722a5da08752da35dd37dcf8fa5f5d

## Client–server model

The client–server model is a communication pattern where one party (the client) initiates requests and the other party (the server) fulfills them. The client is typically a browser, mobile app, or any consumer of data; the server is a process sitting somewhere on the network, listening for those requests and sending back responses.

What makes this model powerful is the separation of concerns — the client handles presentation and user interaction, while the server handles business logic, data persistence, and security enforcement. Neither side needs to know how the other is implemented internally.

> This is a synchronous pull model by default — the client has to ask. When you need the server to push updates (live feeds, notifications), you reach for WebSockets, SSE, or long-polling on top of this foundation.

---

## Application service

An application service is the layer that owns your business logic — the middle ground between your API endpoints (which just route requests) and your data layer (which just stores things). It's where decisions happen: validate this input, orchestrate these domain operations, apply these rules, return this result.

The reason you isolate it as a distinct layer is testability and replaceability. You can unit-test your business logic without spinning up a database or HTTP server. You can swap out your delivery mechanism — REST today, gRPC tomorrow — without touching the core logic at all.

> In a microservice architecture, each service is essentially a bounded context with its own application service layer. Keeping that layer thin and focused on one domain is what prevents the "distributed monolith" anti-pattern where services are technically separate but logically entangled.

---

## HTTP request / response

HTTP is a stateless, text-based protocol built on top of TCP. A client sends a request with a method (`GET`, `POST`, `PUT`, `DELETE`, etc.), a URL, headers, and optionally a body. The server processes it and returns a response with a status code, headers, and a body.

Headers carry metadata — content type, authorization tokens, caching directives, CORS policies. Status codes communicate intent: 2xx means success, 3xx is redirection, 4xx is a client error (bad input, unauthorized), 5xx is a server error.

HTTP/1.1 allows one request per TCP connection at a time. HTTP/2 introduced multiplexing — multiple requests in flight over a single connection. HTTP/3 moves to QUIC (UDP-based) to eliminate head-of-line blocking entirely.

> Understanding idempotency matters here. `GET` and `PUT` are idempotent — calling them multiple times has the same effect as calling once. `POST` is not. This affects retry logic, distributed systems design, and API contract design.

---

## Horizontal scaling vs vertical scaling

Vertical scaling means giving your existing machine more resources — a bigger CPU, more RAM, faster disks. It's simple to do and requires zero changes to your application. The hard limit is that there's a ceiling to how big one machine can get, and it's a single point of failure.

Horizontal scaling means adding more machines and distributing the load across them. It's theoretically unlimited and inherently redundant — if one node dies, the others keep serving traffic. The trade-off is complexity: your application has to be stateless (or state has to live externally), and you need a load balancer in front.

> In practice you do both. You vertical-scale until it becomes cost-inefficient or you hit a ceiling, then you horizontal-scale. Stateless services (like REST APIs) scale horizontally with ease. Stateful things (like databases) are harder — that's where sharding, read replicas, and distributed databases come in.

---

## Load balancer

A load balancer sits in front of a pool of servers and distributes incoming traffic across them. Its job is twofold: spread the work so no single server gets overwhelmed, and detect unhealthy servers and route around them automatically via health checks.

Common distribution strategies are round-robin (take turns), least connections (send to whoever has the fewest active requests), and IP hash (same client always hits the same server — useful for sticky sessions). There are two layers:

- **Layer 4** load balancers route based on IP/TCP — fast and simple.
- **Layer 7** load balancers route based on HTTP content (URL path, headers, cookies) — which lets you do things like route `/api/*` to one cluster and `/static/*` to another.

> The load balancer itself can become a single point of failure, so production setups run them in active-active or active-passive pairs. In cloud environments (AWS ALB, GCP Load Balancer), this redundancy is managed for you. The session-stickiness vs. statelessness trade-off is worth discussing here.

---

## Microservices & microfrontends

A microservice is an independently deployable unit of software that owns a single, well-defined business capability — user management, payments, notifications. Each service has its own codebase, its own database, and its own deployment pipeline. Teams can develop, test, and ship them independently, which is the real organizational win.

A microfrontend applies that same idea to the UI layer. Instead of one monolithic frontend app, different teams own different sections of the UI — a checkout team owns the cart widget, a catalog team owns the product listing — each deployed independently and composed at runtime via iframes, module federation, or server-side includes.

> Microservices trade operational simplicity for team autonomy and fault isolation. The hidden costs are network latency between services, distributed tracing complexity, and data consistency challenges (no shared transactions across services — you deal with eventual consistency and sagas). I'd only recommend them when your team has outgrown a monolith's deployment bottleneck.

---

## Relational (SQL) vs non-relational (NoSQL) databases

A relational database stores data in tables with a fixed schema, enforces relationships via foreign keys, and gives you ACID transactions — atomicity, consistency, isolation, durability. If you need to join data across entities, run complex queries, or guarantee that a payment and an inventory update either both happen or neither does, SQL is the right tool. PostgreSQL and MySQL are the go-tos.

NoSQL databases sacrifice some of that rigidity for flexibility and scale. Document stores (MongoDB) let each record have its own shape. Key-value stores (Redis) give you nanosecond lookups. Wide-column stores (Cassandra) are tuned for massive write throughput. Graph databases (Neo4j) model complex relationships natively. They typically favor availability and partition tolerance over strict consistency (CAP theorem).

> The choice isn't religious — it's workload-driven. I'd use Postgres as a default and reach for NoSQL when I have a specific, justified need: high write throughput, schema flexibility for user-generated content, or sub-millisecond caching. Using the wrong tool because it's trendy creates technical debt that's painful to undo.

---

## API gateway

An API gateway is the single front door to your backend. Every external request hits the gateway first, and the gateway figures out where to route it. Beyond routing, it centralizes cross-cutting concerns that would otherwise be duplicated across every service: authentication, rate limiting, request logging, SSL termination, response caching, and request/response transformation.

Without a gateway, every microservice has to implement its own auth middleware, its own rate limiter, its own logging — which is expensive and inconsistent. The gateway owns that once, and your services can focus purely on their domain logic.

> The gateway can become a bottleneck and a single point of failure — it needs to be highly available and performant. It also introduces a choke point in your deployment pipeline if every service change requires gateway reconfiguration. The BFF (Backend for Frontend) pattern is a useful evolution where you create purpose-built gateways for each client type (mobile, web) rather than one generic one.

---

## Message queue

A message queue is a buffer that sits between two services, letting them communicate asynchronously. The producer drops a message onto the queue and moves on — it doesn't wait for the consumer to finish. The consumer picks messages off at its own pace and processes them.

This decoupling is enormously valuable. A spike in traffic doesn't crash your downstream service — it just grows the queue. If the consumer goes down, messages pile up and get processed when it recovers. You can also scale producers and consumers independently, and add multiple consumers to process in parallel (competing consumers pattern).

Common tools: Kafka for high-throughput event streaming, RabbitMQ for traditional task queues, AWS SQS for managed simplicity.

> The hard problems are at-least-once vs. exactly-once delivery, message ordering guarantees, and dead-letter queues for poison messages that repeatedly fail. Kafka gives you durable, ordered, replayable event logs — which is fundamentally different from a traditional queue and enables event sourcing and stream processing patterns.

---

## Logging & monitoring

Logging is the practice of recording discrete events as they happen — a user logged in, a payment failed, a null pointer was thrown. Good logs are structured (JSON, not raw strings), include a request correlation ID so you can trace a request across services, and have appropriate severity levels (DEBUG, INFO, WARN, ERROR).

Monitoring is about measuring your system continuously and alerting when things go wrong. The four golden signals are:

- **Latency** — how long requests take
- **Traffic** — how many requests
- **Error rate** — what fraction fail
- **Saturation** — how close you are to capacity

Tools like Prometheus + Grafana, Datadog, or CloudWatch let you build dashboards and set alert thresholds.

> Logs, metrics, and traces are the three pillars of observability. Traces (via OpenTelemetry, Jaeger) are what logs miss — they let you follow a single request as it hops across microservices and see exactly where latency is being introduced. Without all three, debugging production incidents is guesswork.

---

## Deployment with AWS / Azure / GCP

Cloud deployment means running your application on managed infrastructure instead of physical servers you own. The core primitives are compute (VMs like EC2, or containers via ECS/EKS/GKE), storage (S3, Blob Storage, GCS), and networking (VPCs, subnets, security groups). On top of that, you get managed databases, managed queues, CDNs, and dozens of other services that would otherwise take months to build and operate.

Modern deployments are typically containerized with Docker, orchestrated with Kubernetes, and automated with CI/CD pipelines (GitHub Actions, CodePipeline, Cloud Build). Infrastructure is defined as code (Terraform, CDK, Pulumi) so environments are reproducible and version-controlled. Blue-green or canary deployments let you ship with zero downtime and roll back instantly if something goes wrong.

> Cloud gives you speed and elasticity, but the cost model is tricky — it's easy to accidentally spend 10× more than necessary. Right-sizing instances, using spot/preemptible instances for non-critical workloads, and setting up cost alerts are things I consider from day one. Also, being too cloud-provider-specific creates lock-in; I prefer leaning on open standards (Kubernetes, OpenTelemetry) as the abstraction layer.

---

## Security: authentication vs authorization

Authentication is the question "who are you?" — verifying identity. A user provides credentials (password, biometric, OAuth token) and the system confirms they are who they claim to be. The output is typically a signed token (JWT, session cookie) that proves identity for subsequent requests.

Authorization is the question "what are you allowed to do?" — it comes after authentication and checks permissions. Just because you're authenticated as a valid user doesn't mean you can access every resource. Authorization enforces access control:

- **RBAC** (role-based access control) assigns permissions by role.
- **ABAC** (attribute-based access control) is more fine-grained, checking attributes of the user, resource, and environment.

> Common pitfalls include storing passwords in plaintext (use bcrypt/argon2 instead), using symmetric JWTs where any service can forge tokens (prefer asymmetric RS256), and conflating authentication with authorization in code. Defense in depth means enforcing authorization at multiple layers — API gateway, service layer, and database row-level security — not relying on just one.

---

## Why testing?

Testing is how you make changes confidently. Without it, every deployment is a leap of faith — you're not sure what you broke until a user tells you. With a good test suite, you get a fast feedback loop: you change code, run tests, and know immediately whether you've introduced a regression.

The testing pyramid gives you the right mix:

- **Unit tests** (fast, isolated, test individual functions) form the base.
- **Integration tests** (test how components work together — your service talking to a real database) sit in the middle.
- **End-to-end tests** (simulate real user flows through the full stack) sit at the top and are fewest in number because they're slow and brittle.

Beyond correctness, tests serve as living documentation of intended behavior, and they constrain design — code that's hard to test is usually also hard to maintain and extend.

> I've shipped code without tests and I've shipped code with them — the difference in confidence and maintenance burden is night and day. That said, 100% code coverage is a vanity metric. I focus on covering critical paths, edge cases, and failure modes. Contract testing (Pact) is underrated in microservice environments — it lets you verify service interfaces without full integration test environments.