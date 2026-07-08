1. Point-to-Point vs Publish-Subscribe
   Point-to-Point Model

One message is consumed by only one consumer.

Example:

Producer → Queue → Consumer A
→ Consumer B
→ Consumer C

Only one consumer gets each message.

Pros
Good for task distribution
Easy load balancing
Prevents duplicate processing
Useful for background jobs
Cons
Not suitable when multiple systems need the same event
Message is usually removed after one consumer processes it
Harder to broadcast events
Example

Order service sends a payment task.

Only one payment worker should process it.

Publish-Subscribe Model

One message can be consumed by multiple subscribers.

Example:

Producer → Topic → Inventory Service
→ Email Service
→ Analytics Service

Each subscriber receives the event.

Pros
Great for event-driven systems
Loose coupling
Easy to add new consumers
Multiple services can react to the same event
Cons
More complex
Duplicate processing must be handled
Ordering and consistency can be harder
Requires idempotent consumers
Example

OrderCreated event is published.

Inventory, notification, and analytics services all consume it.

2. Why Use a Messaging System?

Interview answer:

We use messaging systems to decouple services, improve scalability, absorb traffic spikes, enable asynchronous processing, and improve system reliability.

Main benefits:

Asynchronous communication
Service decoupling
Load leveling
Retry support
Event-driven architecture
Better fault tolerance
Buffering during traffic spikes

Example:

Instead of checkout waiting for email and inventory updates, it publishes an event and returns quickly.

3. Kafka Architecture

Kafka has several core components.

Producer
↓
Topic
↓
Partition 0 → Broker 1
Partition 1 → Broker 2
Partition 2 → Broker 3
↓
Consumer Group
Main Components
Producer

Publishes messages to Kafka topics.

Topic

Logical category of messages.

Example:

order-created
payment-completed
user-registered
Partition

A topic is split into partitions.

Partition gives Kafka:

scalability
parallelism
ordering within a partition
Broker

Kafka server that stores partitions.

A Kafka cluster has multiple brokers.

Consumer

Reads messages from topics.

Consumer Group

Consumers work together.

Within the same group, each partition is consumed by only one consumer.

Different groups can consume the same topic independently.

Offset

Kafka tracks the position of each consumer in each partition.

Example:

Partition 0:
offset 0, offset 1, offset 2, offset 3
Replication

Partitions are replicated across brokers for fault tolerance.

One replica is leader, others are followers.

4. Messages Accumulate in Kafka, Consumer Cannot Keep Up — What Do You Do?

This is a very common senior-level question.

Interview answer:

First, I would identify whether the bottleneck is producer throughput, consumer processing, partition count, downstream dependency, or infrastructure capacity. Then I would scale consumers, optimize processing, increase partitions if needed, tune batch settings, and monitor consumer lag.

Steps
1. Check Consumer Lag

Use:

Kafka consumer group command
Prometheus/Grafana
Confluent Control Center

Consumer lag tells how far behind consumers are.

2. Scale Consumers

Add more consumers in the same consumer group.

But maximum parallelism is limited by partition count.

10 partitions → max 10 active consumers in one group
3. Increase Partition Count

If consumers are CPU-bound and partitions are too few, increase partitions.

But be careful:

ordering may be affected
repartitioning changes key distribution
too many partitions increase overhead
4. Optimize Consumer Logic

Common bottlenecks:

slow database writes
external API calls
synchronous processing
no batching

Solutions:

batch processing
async processing
connection pooling
bulk inserts
caching
reduce payload size
5. Tune Consumer Config

Useful configs:

max.poll.records=500
fetch.min.bytes=1048576
fetch.max.wait.ms=500
max.partition.fetch.bytes=1048576
6. Use Retry and DLQ

Poison messages can block processing.

Use:

retry topic
dead-letter topic
error handling
idempotency
7. Check Downstream Services

Sometimes Kafka is fine, but database or API is slow.

Scale:

database
cache layer
thread pool
external service limits
5. How Does Kafka Deal With Expired Data?

Kafka does not delete messages immediately after consumption.

Kafka deletes messages based on retention policy.

Retention by Time

Example:

log.retention.hours=168

This keeps data for 7 days.

Retention by Size

Example:

log.retention.bytes=1073741824

Delete old segments when topic exceeds size limit.

Log Compaction

Kafka can keep only the latest value for each key.

Example:

user-1 email=a@test.com
user-1 email=b@test.com

After compaction, Kafka keeps latest value:

user-1 email=b@test.com

Useful for:

user profile updates
configuration
changelog topics
Interview answer

Kafka stores messages based on configured retention policies, not based on whether they have been consumed. Expired data is deleted when the retention time or retention size limit is reached. Kafka also supports log compaction, where it retains only the latest value for each key.

6. Data Volume in Kafka

Kafka is designed for high-throughput data.

Things that affect data volume:

message size
messages per second
retention period
replication factor
compression
number of partitions

Formula:

Storage =
message size
× messages per second
× retention seconds
× replication factor

Example:

1 KB/message
× 10,000 messages/sec
× 7 days
× replication factor 3

≈ 18 TB

Compression can reduce this significantly.

Common compression:

snappy
gzip
lz4
zstd
7. How to Calculate Kafka Partition Number?

Partition count depends on throughput and parallelism.

Formula Based on Throughput
Partitions =
max(
target producer throughput / throughput per partition,
target consumer throughput / throughput per partition
)

Example:

Need 100 MB/s write throughput
Each partition supports 10 MB/s

100 / 10 = 10 partitions
Formula Based on Consumers
Partitions >= max number of consumers in one consumer group

Example:

Need 12 consumers processing in parallel
Use at least 12 partitions
Consider Ordering

Kafka only guarantees ordering within a partition.

If all events for one order must be ordered, use orderId as key.

producer.send(
new ProducerRecord<>("orders", orderId, event)
);

Same key goes to same partition.

Interview answer

I calculate partition count based on required throughput, expected consumer parallelism, ordering requirements, and future growth. The number of partitions should be at least the maximum number of consumers needed in a consumer group, but not excessively high because too many partitions increase broker overhead and rebalance cost.

8. AWS SQS vs RabbitMQ vs Kafka
   Feature	AWS SQS	RabbitMQ	Kafka
   Type	Managed queue	Message broker	Distributed event streaming
   Model	Queue	Queue/pub-sub	Log/pub-sub
   Retention	Limited retention	Usually until ack	Time/size based
   Ordering	FIFO queue supports ordering	Queue ordering	Per-partition ordering
   Throughput	High, managed	Medium/high	Very high
   Replay	Limited	Difficult after ack	Strong replay support
   Operations	Fully managed	Self-managed or managed	More operational complexity
   Best for	Async jobs	Routing/work queues	Event streaming/data pipelines
   AWS SQS

Best for:

simple queue
decoupling microservices
background jobs
fully managed AWS workloads
RabbitMQ

Best for:

complex routing
request/reply
work queues
traditional messaging

Supports:

exchanges
routing keys
acknowledgements
Kafka

Best for:

event streaming
high throughput
log-based architecture
replay events
analytics pipelines
Interview answer

I use SQS when I need a simple fully managed queue in AWS. I use RabbitMQ when I need advanced routing patterns and traditional broker features. I use Kafka when I need high-throughput event streaming, durable logs, consumer replay, and multiple independent consumer groups.

9. AWS SNS vs SQS
   SNS

Simple Notification Service.

Push-based pub-sub.

One message can be delivered to multiple subscribers.

Subscribers can be:

SQS
Lambda
HTTP endpoint
email
SMS
SQS

Simple Queue Service.

Pull-based queue.

Consumers poll messages from queue.

SNS + SQS Together

Common architecture:

Order Service
↓
SNS Topic
↓     ↓       ↓
SQS1   SQS2    SQS3
↓      ↓       ↓
Email Inventory Analytics

Each service gets its own queue.

This gives:

fanout
retry
buffering
failure isolation
SNS vs SQS Table
Feature	SNS	SQS
Pattern	Pub-sub	Queue
Delivery	Push	Pull
Consumers	Multiple subscribers	Usually worker consumers
Message storage	Short-lived	Stores messages until consumed/expired
Use case	Broadcast event	Buffer jobs
Retry	Per subscription	Visibility timeout + DLQ