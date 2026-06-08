## What is TCP Three-Way Handshake?

TCP uses a three-way handshake to establish a reliable connection before data transmission begins.

### Steps

1. **SYN**

    * Client sends a SYN packet to the server.
    * Indicates a request to establish a connection.

2. **SYN-ACK**

    * Server responds with a SYN-ACK packet.
    * Acknowledges the client's request and sends its own sequence number.

3. **ACK**

    * Client sends an ACK packet.
    * Connection is established.

```text
Client                     Server

  SYN  ------------------>
       <------------------  SYN-ACK
  ACK  ------------------>

Connection Established
```

### Senior-Level Answer

TCP establishes a reliable connection through a three-way handshake, ensuring both client and server are synchronized before data transmission. This mechanism supports reliability, ordered delivery, flow control, and congestion control.

---

# TCP vs UDP

| Feature         | TCP                 | UDP            |
| --------------- | ------------------- | -------------- |
| Connection      | Connection-oriented | Connectionless |
| Reliability     | Reliable            | Best Effort    |
| Packet Ordering | Guaranteed          | Not Guaranteed |
| Error Recovery  | Yes                 | No             |
| Speed           | Slower              | Faster         |
| Overhead        | Higher              | Lower          |

## TCP

Used when reliability is important.

Examples:

* HTTP/HTTPS
* Database connections
* Email
* File transfer

## UDP

Used when speed and low latency are more important.

Examples:

* Video streaming
* Online gaming
* VoIP
* DNS

---

# What is Tomcat?

Apache Tomcat is an open-source Java web server and servlet container.

It implements:

* Servlet Specification
* JSP (JavaServer Pages)
* WebSocket

Tomcat executes Java web applications and handles HTTP requests.

---

# Basic Components of Tomcat

## Architecture

```text
Server
 └── Service
      ├── Connector
      └── Engine
           └── Host
                └── Context
```

## Components

### Server

Top-level container managing the Tomcat instance.

### Service

Groups Connectors and an Engine.

### Connector

Receives incoming requests.

Examples:

* HTTP
* HTTPS
* AJP

### Engine

Processes requests and routes them to the correct Host.

### Host

Represents a virtual host/domain.

Examples:

```text
www.company.com
api.company.com
```

### Context

Represents a deployed web application.

Example:

```text
http://localhost:8080/shop
```

Here, `/shop` is the Context.

---

# What is a Web Server?

A web server receives HTTP requests and returns content to clients.

Responsibilities:

* Serve static files
* Handle HTTP communication
* SSL/TLS termination
* Reverse proxy
* Load balancing

Examples:

* Nginx
* Apache HTTP Server

## Example

```text
Browser
   ↓
Nginx
   ↓
Spring Boot + Tomcat
   ↓
MySQL
```

---

# What is Three-Tier Architecture?

Three-tier architecture separates an application into three layers.

## 1. Presentation Layer

Handles user interaction.

Examples:

* React
* Angular
* Mobile applications

Responsibilities:

* Display data
* Collect user input

## 2. Business Layer

Contains business logic.

Examples:

* Spring Boot Services
* Java Application Layer

Responsibilities:

* Validation
* Business rules
* Processing requests

## 3. Data Layer

Responsible for persistence.

Examples:

* MySQL
* PostgreSQL
* MongoDB

Responsibilities:

* Store data
* Retrieve data
* Maintain consistency

## Diagram

```text
+----------------------+
| Presentation Layer   |
| React / Angular UI   |
+----------------------+
           |
+----------------------+
| Business Layer       |
| Spring Boot Services |
+----------------------+
           |
+----------------------+
| Data Layer           |
| MySQL / MongoDB      |
+----------------------+
```

### Benefits

* Separation of concerns
* Easier maintenance
* Better scalability
* Improved testability

---

# OSI Model

The OSI (Open Systems Interconnection) Model defines seven layers of network communication.

## Layers

| Layer | Name         |
| ----- | ------------ |
| 7     | Application  |
| 6     | Presentation |
| 5     | Session      |
| 4     | Transport    |
| 3     | Network      |
| 2     | Data Link    |
| 1     | Physical     |

### Mnemonic

```text
Please Do Not Throw Sausage Pizza Away
```

---

## Layer 7 - Application

Provides network services to applications.

Protocols:

* HTTP
* HTTPS
* FTP
* SMTP
* DNS

Example:

* Browser sending an HTTP request

---

## Layer 6 - Presentation

Responsible for formatting data.

Functions:

* Encryption
* Decryption
* Compression
* Encoding

Examples:

* TLS/SSL
* JSON
* XML

---

## Layer 5 - Session

Manages communication sessions.

Functions:

* Session establishment
* Session maintenance
* Session termination

Example:

* User login session

---

## Layer 4 - Transport

Provides end-to-end communication.

Protocols:

* TCP
* UDP

Functions:

* Reliability
* Error recovery
* Flow control

Example:

* TCP Three-Way Handshake

---

## Layer 3 - Network

Handles routing between networks.

Protocol:

* IP

Device:

* Router

Functions:

* Logical addressing
* Route selection

---

## Layer 2 - Data Link

Handles communication within a local network.

Functions:

* MAC addressing
* Error detection

Device:

* Switch

Protocol:

* Ethernet

---

## Layer 1 - Physical

Responsible for transmitting raw bits.

Examples:

* Ethernet cables
* Fiber optics
* Wi-Fi signals

Devices:

* Hub
* Repeater

---

## Java Web Application Through OSI Layers

```text
Browser
   ↓
Application Layer (HTTP)

TLS Encryption
   ↓
Presentation Layer

Session Management
   ↓
Session Layer

TCP
   ↓
Transport Layer

IP
   ↓
Network Layer

Ethernet
   ↓
Data Link Layer

Cable / WiFi
   ↓
Physical Layer
```
