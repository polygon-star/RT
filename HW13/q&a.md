## 1. SQL vs NoSQL

SQL databases are relational — data is stored in structured tables with a fixed schema, and relationships between tables are handled through joins. They provide strong ACID guarantees, which makes them a good fit for transactional systems where data consistency is critical. Common examples are PostgreSQL and MySQL.

NoSQL databases take a different approach — they're schema-flexible and designed to scale horizontally. Depending on the type, they can be document stores like MongoDB, key-value stores like Redis, wide-column stores like Cassandra, or graph databases like Neo4j. The tradeoff is that most NoSQL systems offer eventual consistency rather than strong consistency.

The way I think about it: if my data has clear relationships and I need complex queries with transactions, SQL is the right choice. If I need to handle massive write throughput or the data structure is highly variable, NoSQL fits better. In a microservices architecture, it's common to use both — SQL for financial or user data, NoSQL for caching or event logs.

---

## 2. Database Normalization

Normalization is the process of organizing a database schema to reduce data redundancy and prevent update anomalies. It's applied through a series of normal forms.

First normal form requires that every column holds atomic values — no repeating groups or arrays in a single cell.

Second normal form builds on that by requiring that every non-key column depends on the entire primary key, not just part of it. This mainly matters for tables with composite primary keys.

Third normal form goes further — it removes transitive dependencies, meaning non-key columns should depend only on the primary key, not on other non-key columns.

BCNF is a stricter version of 3NF where every determinant must be a candidate key.

In practice, I normalize to 3NF for transactional tables to keep data consistent and avoid anomalies on insert, update, or delete. But for read-heavy reporting or analytics, I'll intentionally denormalize — flatten data into fewer tables with some redundancy — to avoid expensive joins at query time. That's really the OLTP vs OLAP tradeoff.

---

## 3. Vertical Scaling vs Horizontal Scaling

Vertical scaling means adding more resources to a single machine — more CPU, more RAM, faster disk. It's simpler to implement because you don't change the architecture, but it has a hard ceiling based on hardware limits, and it's still a single point of failure.

Horizontal scaling means adding more machines and distributing the load across them. It can scale almost indefinitely, but it introduces complexity — you need to handle things like load balancing, data sharding, and distributed state.

For the application tier, horizontal scaling is usually preferred — you run multiple stateless instances behind a load balancer. The key is keeping the service stateless, so any instance can handle any request.

For the database tier, it depends on the workload. Vertical scaling is simpler and works well up to a point. For read-heavy systems, you add read replicas. For write-heavy or very large datasets, you look at sharding or switching to a database that was designed for horizontal scale, like Cassandra.

---

## 4. ACID

ACID describes the four properties that guarantee reliable transaction processing in a database.

**Atomicity** means a transaction is all-or-nothing. If any step fails, the entire transaction is rolled back. You never end up with partial writes.

**Consistency** means every transaction brings the database from one valid state to another. All constraints, foreign keys, and rules must be satisfied before and after.

**Isolation** means concurrent transactions don't interfere with each other. The level of isolation is configurable — from Read Uncommitted at the loosest, up to Serializable at the strictest. Each level makes a different tradeoff between correctness and performance.

**Durability** means once a transaction is committed, it survives crashes. The data is written to disk, typically through a write-ahead log.

One important thing to mention is that ACID guarantees apply within a single database. In a distributed system with multiple services and databases, you lose native ACID across service boundaries. In that case, you typically use a Saga pattern with compensating transactions to handle failures and maintain eventual consistency.

---

## 5. CAP Theorem

CAP theorem says that in a distributed system, you can only guarantee two out of three properties at the same time: Consistency, Availability, and Partition Tolerance.

**Consistency** means every read returns the most recent write, or an error. All nodes see the same data at the same time.

**Availability** means every request gets a response — it might not be the latest data, but the system always responds.

**Partition tolerance** means the system continues to function even when there's a network partition — some nodes can't communicate with others.

In practice, network partitions do happen, so partition tolerance is essentially required. That means the real choice is between consistency and availability when a partition occurs.

Systems that choose CP — like HBase or Zookeeper — will return an error or wait rather than serve stale data. Systems that choose AP — like Cassandra or DynamoDB — will stay available and serve potentially stale data, with eventual consistency.

The right choice depends on the use case. For financial transactions, I'd pick CP — you can't serve stale balance data. For something like a product recommendation feed, AP is fine — it's acceptable if the data is slightly out of date.

One extension worth knowing is PACELC, which says that even without a partition, there's still a tradeoff between latency and consistency. That's a more complete model of how distributed databases actually behave.