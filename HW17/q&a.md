### What is Docker?

Docker is a containerization platform that packages an application with its dependencies, runtime, libraries, and configuration into a portable container.

It helps the application run consistently across development, testing, and production environments.

---

### What is a Dockerfile?

A Dockerfile is a script that defines how to build a Docker image.

Example:

```dockerfile
FROM eclipse-temurin:17-jdk

COPY target/app.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
```
### What is a Docker Image?

A Docker image is an immutable template used to create containers.

Dockerfile
    ↓
docker build
    ↓
Docker Image

Example:

springboot-app:v1

### What is a Docker Container?

A Docker container is a running instance of a Docker image.

Docker Image
    ↓
docker run
    ↓
Docker Container

One image can create multiple containers.

### Docker vs VM
Docker Container	Virtual Machine
Shares host OS kernel	Has its own guest OS
Lightweight	Heavyweight
Starts quickly	Slower startup
Uses fewer resources	Uses more resources
Good for microservices	Stronger isolation

Docker containers are lighter because they share the host OS kernel. Virtual machines include a full guest operating system, so they consume more memory and CPU.

### How to Use Docker in a Real Project

In a Spring Boot project, I can containerize the application using Docker.

Flow:

Spring Boot App
    ↓
Dockerfile
    ↓
Docker Image
    ↓
Amazon ECR
    ↓
Amazon ECS

Typical steps:

mvn clean package
docker build -t springboot-app .
docker run -p 8080:8080 springboot-app

In production, the image can be pushed to Amazon ECR, and ECS can pull the image and deploy it as containers.

### AWS Services
### EC2

EC2 provides virtual servers in AWS.

It can be used to host applications, run backend services, install databases manually, or run Docker containers directly.

Example use case:

Spring Boot App running on EC2
### ECS

ECS, Elastic Container Service, is used to run and manage Docker containers.

It is commonly used to deploy containerized Spring Boot applications.

Docker Image
    ↓
ECR
    ↓
ECS Cluster
    ↓
Running Containers
### ECR

ECR, Elastic Container Registry, is used to store Docker images.

Typical flow:

docker build
    ↓
docker tag
    ↓
docker push
    ↓
Amazon ECR

ECS can then pull images from ECR.

### RDS

RDS is a managed relational database service.

It supports databases like:

PostgreSQL
MySQL
SQL Server
MariaDB
Oracle

Common use case:

Spring Boot App
    ↓
Amazon RDS PostgreSQL

RDS is good for transactional business data.

### DocumentDB

DocumentDB is AWS's MongoDB-compatible document database.

It stores JSON-like documents and is useful when the data structure is flexible.

Example use cases:

User profiles
Product catalogs
Semi-structured data
### DynamoDB

DynamoDB is a fully managed NoSQL key-value and document database.

It is designed for high scalability and low-latency access.

Example use cases:

User sessions
Shopping carts
Cache-like data
High-throughput event data
### Lambda Function

Lambda is a serverless compute service.

It runs code without managing servers.

Example:

File uploaded to S3
    ↓
Lambda triggered
    ↓
Process file

Common use cases:

Background processing
Scheduled jobs
Event-driven workflows
Lightweight APIs
### API Gateway

API Gateway exposes REST or HTTP APIs.

It can route requests to:

Lambda
ECS services
EC2 services
Other HTTP backends

Example:

Client
    ↓
API Gateway
    ↓
Lambda or ECS

It can also handle authentication, throttling, rate limiting, and logging.

### AWS Kinesis

Kinesis is used for real-time data streaming.

Example use cases:

Clickstream events
Application logs
IoT data
Real-time analytics

Flow:

Producer
    ↓
Kinesis Stream
    ↓
Consumer
### IAM

IAM, Identity and Access Management, controls access to AWS resources.

It manages:

Users
Groups
Roles
Policies
Permissions

Example:

ECS Task Role
    ↓
Allows app to access S3 or DynamoDB

IAM roles are preferred over hardcoded credentials.

### SNS

SNS, Simple Notification Service, is a publish-subscribe messaging service.

One message can be sent to many subscribers.

Example:

Order Created Event
    ↓
SNS Topic
    ↓
Email
    ↓
Lambda
    ↓
### SQS

SNS is good for broadcasting events.

SQS

SQS, Simple Queue Service, is a message queue service.

It decouples services and supports asynchronous processing.

Example:

Order Service
    ↓
SQS Queue
    ↓
Payment Service

SQS is good when one service produces work and another service processes it later.

### SNS vs SQS
SNS	SQS
Pub/Sub	Queue
One message to many subscribers	One message processed by consumers
Push-based	Poll-based
Good for notifications/events	Good for async task processing
### Example Project Architecture
Frontend
    ↓
API Gateway
    ↓
ECS Spring Boot Service
    ↓
RDS / DynamoDB

Spring Boot Service
    ↓
SNS
    ↓
SQS
    ↓
Worker Service

Docker Image
    ↓
ECR
    ↓
ECS Deployment

A Spring Boot application can be packaged as a Docker image, pushed to ECR, and deployed on ECS. The application can connect to RDS for transactional data, use DynamoDB for high-scale NoSQL data, use SQS for asynchronous processing, and use SNS for event notifications. IAM roles provide secure access between AWS services.