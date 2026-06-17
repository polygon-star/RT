![HW14](./screenshots/01.png)

![HW14](./screenshots/02.png)


---

## 1. What is an Index?

An **index** is a data structure that allows the database engine to find rows quickly **without scanning the entire table**.

Think of it like a book's table of contents — instead of reading every page, you jump directly to the right page.

```sql
-- Create an index on the age column
CREATE INDEX idx_employee_age ON EMPLOYEE(age);
```

**Trade-offs:**
- ✅ Speeds up `SELECT` / `WHERE` / `JOIN` queries
- ❌ Slows down `INSERT` / `UPDATE` / `DELETE` (index must be maintained)
- ❌ Consumes extra disk space

---

## 2. Clustered Index vs Non-Clustered Index

| | Clustered Index | Non-Clustered Index |
|---|---|---|
| **Storage** | Data rows are physically sorted and stored in index order | A separate structure; stores pointers to actual data rows |
| **Count per table** | Only **1** (data can only be sorted one way) | Multiple allowed |
| **Speed** | Faster for range queries | Slightly slower (requires a lookup to the actual row) |
| **Default** | PRIMARY KEY in most databases | Created manually with `CREATE INDEX` |

**Visual analogy:**
- Clustered = a dictionary where words are physically ordered A→Z
- Non-Clustered = a book's index at the back that points to page numbers

```sql
-- In PostgreSQL, the PRIMARY KEY automatically creates a clustered-like index
-- Manually cluster a table by an index:
CLUSTER EMPLOYEE USING idx_employee_age;
```

---

## 3. Data Structure for Index

Most databases (PostgreSQL, MySQL, SQL Server) use a **B-Tree (Balanced Tree)** by default.

```
                [30]
               /    \
           [25]      [35]
          /    \    /    \
        [10] [28] [32] [40]
```

**Why B-Tree?**
- Supports `=`, `<`, `>`, `BETWEEN`, `ORDER BY` efficiently
- O(log n) search, insert, delete
- Self-balancing — stays efficient as data grows

**Other index types:**

| Type | Use Case |
|------|----------|
| **B-Tree** | Default; range queries, equality |
| **Hash** | Equality only (`=`), faster than B-Tree for exact match |
| **GIN** | Full-text search, arrays, JSONB |
| **GiST** | Geometric data, fuzzy search |
| **Bitmap** | Low-cardinality columns (e.g. gender, boolean) |

```sql
-- Hash index in PostgreSQL
CREATE INDEX idx_hash_dept ON EMPLOYEE USING HASH(dept);
```

---

## 4. View vs Stored Procedure

| | View | Stored Procedure |
|---|---|---|
| **What it is** | A saved SQL query (virtual table) | A saved block of SQL logic (executable program) |
| **Returns** | Always a result set (table-like) | Can return result sets, scalar values, or nothing |
| **Parameters** | ❌ Cannot accept parameters | ✅ Can accept input/output parameters |
| **Contains logic** | Simple `SELECT` only | Can have `IF`, loops, transactions, error handling |
| **Use case** | Simplify complex queries, restrict column access | Business logic, batch operations, multi-step transactions |

```sql
-- View example
CREATE VIEW sales_employees AS
SELECT name, salary FROM EMPLOYEE WHERE dept = 'Sales';

-- Usage
SELECT * FROM sales_employees;

-- Stored Procedure example (PostgreSQL)
CREATE OR REPLACE PROCEDURE give_raise(dept_name TEXT, raise_amount DECIMAL)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE EMPLOYEE SET salary = salary + raise_amount WHERE dept = dept_name;
END;
$$;

-- Usage
CALL give_raise('Sales', 10000);
```

---

## 5. View vs Materialized View

| | View | Materialized View |
|---|---|---|
| **Storage** | No data stored; query runs every time | Result is **physically stored** on disk |
| **Freshness** | Always up-to-date (real-time) | Can be stale; requires manual `REFRESH` |
| **Performance** | Slower for complex queries | Much faster for complex/expensive queries |
| **Use case** | Real-time data needed | Reports, dashboards, heavy aggregations |
| **Refresh** | N/A | `REFRESH MATERIALIZED VIEW` |

```sql
-- Materialized View
CREATE MATERIALIZED VIEW dept_avg_salary AS
SELECT dept, AVG(salary) AS avg_salary
FROM EMPLOYEE
GROUP BY dept;

-- Refresh when data changes
REFRESH MATERIALIZED VIEW dept_avg_salary;
```

**Rule of thumb:** Use a regular view for real-time accuracy; use a materialized view when query performance matters more than instant freshness.

---

## 6. How to Tune a SQL Query

### Step 1 — Use EXPLAIN ANALYZE
```sql
EXPLAIN ANALYZE
SELECT dept, AVG(salary) FROM EMPLOYEE
WHERE age > 30 GROUP BY dept;
```
Look for: **Seq Scan** (bad on large tables), **Hash Join** vs **Nested Loop**, high `actual time`.

### Step 2 — Add Indexes on Filtered/Joined Columns
```sql
-- If WHERE age > 30 is slow
CREATE INDEX idx_age ON EMPLOYEE(age);

-- Composite index for multi-column filters
CREATE INDEX idx_dept_age ON EMPLOYEE(dept, age);
```

### Step 3 — Avoid SELECT *
```sql
-- Bad
SELECT * FROM EMPLOYEE;

-- Good
SELECT name, salary FROM EMPLOYEE;
```

### Step 4 — Avoid Functions on Indexed Columns in WHERE
```sql
-- Bad (index on age is NOT used)
WHERE UPPER(dept) = 'SALES'

-- Good
WHERE dept = 'Sales'
```

### Step 5 — Use Joins Instead of Subqueries
```sql
-- Subquery (slower)
SELECT * FROM EMPLOYEE WHERE empId IN (SELECT empId FROM ORDERS);

-- Join (faster)
SELECT e.* FROM EMPLOYEE e JOIN ORDERS o ON e.empId = o.empId;
```

### Step 6 — Partition Large Tables
For tables with millions of rows, partition by range (e.g., date) or list (e.g., region) so queries scan only relevant partitions.

### Step 7 — Use Connection Pooling
In Java Spring Boot, configure HikariCP to avoid overhead of creating new DB connections on every request.

---

## 7. Saga vs 2PC (Two-Phase Commit)

Both solve the **distributed transaction** problem: how to keep data consistent across multiple services/databases.

### 2PC (Two-Phase Commit)

A **synchronous** protocol with a central coordinator:

```
Phase 1 — Prepare:
  Coordinator → "Can you commit?" → Service A, Service B
  Services lock resources and reply "Yes/No"

Phase 2 — Commit/Rollback:
  If all say Yes → Coordinator → "Commit!"
  If any say No  → Coordinator → "Rollback!"
```

| | |
|---|---|
| ✅ Strong consistency (ACID) | |
| ❌ **Blocking** — services hold locks during both phases | |
| ❌ Single point of failure (coordinator crash = deadlock) | |
| ❌ Poor performance at scale | |

### Saga Pattern

A **sequence of local transactions**, each publishing an event or message to trigger the next step. If a step fails, **compensating transactions** undo previous steps.

```
Order Service  →  Payment Service  →  Inventory Service
     ✅                 ✅                    ❌ (out of stock)
                                        ↓ compensate
                   Refund Payment ←  Cancel Order
```

Two styles:
- **Choreography**: each service listens for events and reacts (no central coordinator)
- **Orchestration**: a central Saga orchestrator directs each step (easier to trace)

| | |
|---|---|
| ✅ Non-blocking, high availability | |
| ✅ Works well with microservices and message queues (Kafka) | |
| ❌ **Eventual consistency** only (not immediate) | |
| ❌ Compensating logic is complex to implement | |

### When to Use Which?

| Scenario | Choose |
|----------|--------|
| Single database, strong consistency needed | **2PC** |
| Microservices, high availability, can tolerate eventual consistency | **Saga** |
| Spring Boot microservices with Kafka | **Saga (Orchestration)** |

s3 link

https://qa-walkthrough-01.s3.us-east-2.amazonaws.com/2026-06-17%2017-43-23.mp4?response-content-disposition=inline&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Security-Token=IQoJb3JpZ2luX2VjEM7%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaCXVzLWVhc3QtMiJIMEYCIQDNAVLYpQLZCUQVvMCYt7IFdckYW%2FFglYIP9mHauRtsfAIhAO%2F%2FXbhMAr%2BzjsE2Fu91Jw1sTFPIbR9iYbK9kE0A%2Bq3oKr4DCJf%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEQABoMMDMwMTc5MzEwNTg4IgzBAoDIN09BDY5nhrQqkgM%2FIrQEIMSOvvFH3nZ7%2FoCyeAwCTkftUMnWSTpPiNrm6egiXVrr28ieJpBKF1ni70qXDVnpm7FKQRyls%2Be4L8OW27US1cJ2MVeLyTdzTqa3AC24Az9R4QQCJICyEc57Ux0awYVzmf2%2BFAy8yzkEDsn6ZBS1fqB%2BTjEsC8VlQ%2FTXgRE8b18%2BbYy6IfjBWxz0S2e9Rn5JuOkfDqmRJRNVCi9kBaNMy4Nw1zgb%2FitWCl3OS3c5MSR0frVfet%2Fk2g9QYqIMSr1iYFbKfXQUeWq68zHBj7mMT0zQS5HIyXinr6tnV9%2FyVzztBNirkc2xiq2naLoW5AwW8%2Bo4xGbAqGtJ22lbPvpBIoj1r7wvrnpIZg6KekHUuD9VUicG4w8AbIMMNdT1NFp6X8F2klSgHsCq2P5%2Bhkigyqtfq4xJ5KtwaG1N2fgPg%2BwAl4%2B%2F6v%2FRy9R9tHG6kF99GRHHLxRLTvmVRCwuIFhQAhDuVnzzhFo24knY71C5dERvHjyPoaeFXKX%2F4N0PDaHMiX9i490e%2BVyJ%2BepU%2F0MwtK%2FM0QY63QLfONUlMPCPrJDjDennRfdEyWrmJkoGwBFOr3kHHLUPn2RGAQohflbo1Qk42n7q5RQLMMJewsdoIdLDDVAY1LDDCPEShJDnpWO0oBeg%2B11tfWJpTvmSYz1j0UhcDFncPmHSLp1DbPJdc9LqxfDrGoEp09aewDnu%2BovAuXQz51b%2FqOG5bgpZqfuQdh%2Bj4Wk1cnFqLw1hmyaaUWxm0ocUCwlEkluhVRpEDQmJO%2BNt60NGpZCwiuWukbcoC9UMZI1ArNDw9XQLqgAPOHoZds3TtCDBYjMb0lRS053r4cXbi7oTJG5RiXPPrepZs9zB8ci5G6oc3NPezDmwhG75TvyCKTy5pIOI%2BQ7gxyUjpGmiuCctRRiGq8K7EaXZAixLRxjCRVKXlgK4S3JccOpVfsFXHloyq2y4Hu0Kty%2B7umQ%2BdvFLZI8p04R0WaUpEun1jwYuwI3sNum9aParDTBQlqtG&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=ASIAQOBWTXP6KRBW4XAL%2F20260617%2Fus-east-2%2Fs3%2Faws4_request&X-Amz-Date=20260617T215620Z&X-Amz-Expires=43200&X-Amz-SignedHeaders=host&X-Amz-Signature=12e1ec7bfa5f848486c67998bc62c3d1a00c21f2872829ee56fdf32a07829bc0

