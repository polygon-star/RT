### 6/1

System design interview answers

Client–server model
The client–server model is a communication pattern where one party (the client) initiates requests and the other party (the server) fulfills them. The client is typically a browser, mobile app, or any consumer of data; the server is a process sitting somewhere on the network, listening for those requests and sending back responses.
What makes this model powerful is the separation of concerns — the client handles presentation and user interaction, while the server handles business logic, data persistence, and security enforcement. Neither side needs to know how the other is implemented internally.

This is a synchronous pull model by default — the client has to ask. When you need the server to push updates (live feeds, notifications), you reach for WebSockets, SSE, or long-polling on top of this foundation.

### 6/2
Singleton — the right way in modern Java
Senior answer: mention thread safety, serialization, and reflection pitfalls before being asked.

Best approach — enum singleton
public enum AppConfig {
INSTANCE;
private final String dbUrl = "jdbc:...";
public String getDbUrl() { return dbUrl; }
}

### 6/3
if asked this question "what is application service?" in the interview, how to respond properly as a senior swe?

3:09 PM
A senior SWE answer should show depth + context awareness, not just a textbook definition. The key is to show you understand the why, not just the what.

Strong Answer Structure
1. Lead with the core idea (1-2 sentences)

"Application Service is a layer in a layered architecture that orchestrates business workflows — it coordinates the sequence of operations to fulfill a use case, but doesn't contain business rules itself."

2. Clarify what it does vs. what it doesn't do

"It's responsible for what steps to execute and in what order, delegating the actual business rules down to the domain layer and technical concerns like payment APIs or email to the infrastructure layer."

3. Give a concrete example

"For example, in an order service, the application service would coordinate: validate the request, call the domain to apply pricing rules, call the payment infrastructure to charge, then persist the order. It's the conductor — it knows the sequence but doesn't play any instrument itself."

4. Show awareness of trade-offs (this is what separates senior from junior)

"In practice, the line can get blurry — teams sometimes let business logic leak into the application service, which makes it harder to test and maintain. Keeping it thin is a discipline."