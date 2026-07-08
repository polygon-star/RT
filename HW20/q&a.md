1. TDD vs BDD vs DDD
   TDD (Test-Driven Development)

TDD is a development methodology where we write tests before writing the implementation.

Process:

Write a failing test.
Write minimal code to pass the test.
Refactor the code.

Benefits:

Better code quality
High test coverage
Easier refactoring

Common tools:

JUnit
Mockito
BDD (Behavior-Driven Development)

BDD focuses on application behavior from the user's perspective.

Instead of technical tests, requirements are written like:

Given a customer has enough balance
When they transfer money
Then the transfer should succeed

Tools:

Cucumber
Gherkin
JUnit

BDD improves communication between developers, QA, and business teams.

DDD (Domain-Driven Design)

DDD is an architectural approach that models software around business domains.

Key concepts:

Entity
Value Object
Aggregate
Repository
Domain Service
Bounded Context

Example:

Banking System

Customer
Account
Transaction

Instead of putting all business logic into services, DDD keeps business rules inside domain models.

2. What is JUnit?

JUnit is the standard unit testing framework for Java.

It is used to:

write unit tests
automate testing
verify business logic

JUnit 5 features:

@Test
@BeforeEach
@AfterEach
Assertions
Parameterized tests

Example:

@Test
void testAdd() {
assertEquals(5, calculator.add(2,3));
}
3. What is Mockito?

Mockito is a Java mocking framework used for unit testing.

It creates fake objects so we don't depend on databases or external services.

Example:

@Mock
UserRepository repository;

@InjectMocks
UserService service;

when(repository.findById(1L))
.thenReturn(user);

Benefits:

isolate business logic
faster tests
no real database needed
4. How do you test your application?

A good senior answer:

I follow the testing pyramid.

Unit Tests
JUnit
Mockito

Test business logic only.

Integration Tests
Spring Boot Test
Testcontainers
Embedded database

Verify:

REST APIs
Database
Kafka
Redis
API Testing
Postman
RestAssured

Verify endpoints.

End-to-End Testing
Selenium
Cypress

Test the complete user flow.

Performance Testing
JMeter
Gatling
CI/CD

Every pull request runs

unit tests
integration tests
code coverage
static analysis

before deployment.

5. What tools do you use for code quality analysis?

Typical answer:

SonarQube
SonarLint
Checkstyle
PMD
SpotBugs
JaCoCo

SonarQube checks:

code smells
bugs
vulnerabilities
duplicated code
maintainability
coverage

JaCoCo measures test coverage.

6. Authentication vs Authorization
   Authentication

Who are you?

Examples:

username/password
JWT
OAuth login
Authorization

What are you allowed to do?

Example:

Admin -> delete user

Normal user -> view profile

Authentication happens first.

Authorization happens after authentication.

7. Encryption vs Hashing vs Encoding
   Encoding

Purpose:

Data representation.

Examples:

Base64
URL Encoding

Reversible.

Encryption

Purpose:

Protect confidential information.

Needs a key.

Examples:

AES
RSA

Reversible using the key.

Hashing

Purpose:

Verify integrity.

Examples:

SHA-256
BCrypt
Argon2

One-way.

Passwords should always be hashed, never encrypted.

8. How do you secure your application?

Typical senior answer:

We implement security in multiple layers.

Spring Security
JWT authentication
OAuth2 login
HTTPS/TLS
Password hashing using BCrypt
Role-based access control (RBAC)
Input validation
SQL Injection prevention using prepared statements/JPA
XSS protection
CSRF protection (when using sessions)
Rate limiting
Secure HTTP headers
Secrets stored in Vault or Kubernetes Secrets
API Gateway authentication
Logging and auditing
9. What is JWT?

JWT stands for JSON Web Token.

It is a compact token used for stateless authentication.

Structure:

Header.Payload.Signature

Example flow:

User Login

↓

Server verifies credentials

↓

Creates JWT

↓

Client stores JWT

↓

Client sends JWT

Authorization:
Bearer eyJhbGc...

↓

Server validates signature

↓

Request is authorized.

Advantages:

Stateless
Scalable
Fast
Works well with microservices
10. What is OAuth2?

OAuth2 is an authorization framework.

It allows applications to access resources on behalf of a user without sharing the user's password.

Example:

"Login with Google"

Flow:

User

↓

Google Login

↓

Google returns Access Token

↓

Application validates token

↓

User logged in

Common OAuth2 roles:

Resource Owner (User)
Client (Application)
Authorization Server
Resource Server

Grant types commonly used today:

Authorization Code + PKCE (recommended for web/mobile apps)
Client Credentials (service-to-service communication)
