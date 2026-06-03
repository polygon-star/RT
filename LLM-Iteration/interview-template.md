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