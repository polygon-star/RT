### 1. JDBC vs Hibernate

| Topic | JDBC | Hibernate |
|---|---|---|
| Type | Low-level Java database API | ORM framework |
| Query style | SQL | HQL/JPQL or Criteria API |
| Boilerplate | High | Low |
| Object mapping | Manual | Automatic |
| Portability | Database-dependent SQL | More database-independent |
| Control | More direct control | More abstraction |

**JDBC** means you write SQL and manually handle connections, statements, result sets, and object mapping.

**Hibernate** maps Java objects to database tables and reduces boilerplate code.

---

### 2. Statement vs PreparedStatement vs CallableStatement

| Type | Use case | SQL Injection Safe? |
|---|---|---|
| `Statement` | Static SQL | No |
| `PreparedStatement` | Parameterized SQL | Yes |
| `CallableStatement` | Stored procedures | Yes, if parameters are used |

### Statement

```java
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery("SELECT * FROM users");
```
PreparedStatement
```
PreparedStatement ps = conn.prepareStatement(
    "SELECT * FROM users WHERE email = ?"
);
ps.setString(1, email);
ResultSet rs = ps.executeQuery();
```
CallableStatement
```
CallableStatement cs = conn.prepareCall("{call get_user(?)}");
cs.setInt(1, userId);
ResultSet rs = cs.executeQuery();
```
### 3. How to Prevent SQL Injection

Use parameterized queries.

PreparedStatement ps = conn.prepareStatement(
    "SELECT * FROM users WHERE username = ? AND password = ?"
);
ps.setString(1, username);
ps.setString(2, password);

Avoid string concatenation:

// Bad
String sql = "SELECT * FROM users WHERE username = '" + username + "'";

Other best practices:

Use PreparedStatement
Validate user input
Use ORM safely
Avoid dynamic SQL when possible
Use least-privilege database accounts

### 4. What is ORM?

ORM stands for Object-Relational Mapping.

It maps Java classes to database tables.

Example:

@Entity
public class User {
    @Id
    private Long id;

    private String name;
}

This maps to a table like:

users(id, name)

ORM helps developers work with objects instead of writing raw SQL manually.

### 5. JPA vs Hibernate
Topic	JPA	Hibernate
Type	Specification	Implementation
Package	jakarta.persistence	org.hibernate
Defines rules?	Yes	Follows and extends JPA
Can run alone?	No	Yes

JPA is an interface/specification.

Hibernate is a framework that implements JPA.

Example:

@Entity
@Table(name = "users")
public class User {
    @Id
    private Long id;
}
### 6. Persistent States in Entity Lifecycle

Hibernate/JPA entity states:

1. Transient

Object is created but not associated with persistence context.

User user = new User();
2. Persistent / Managed

Object is associated with persistence context.

entityManager.persist(user);
3. Detached

Object was persistent but is no longer managed.

entityManager.detach(user);
4. Removed

Object is marked for deletion.

entityManager.remove(user);

### 7. Mapping Relationships
One-to-One
@OneToOne
private Profile profile;
One-to-Many
@OneToMany(mappedBy = "user")
private List<Order> orders;
Many-to-One
@ManyToOne
private User user;
Many-to-Many
@ManyToMany
private List<Course> courses;

### 8. What is Cascade Type?

Cascade means an operation on one entity is automatically applied to related entities.

Example:

@OneToMany(cascade = CascadeType.ALL)
private List<Order> orders;

Common cascade types:

Cascade Type	Meaning
PERSIST	Save child when parent is saved
MERGE	Update child when parent is updated
REMOVE	Delete child when parent is deleted
REFRESH	Refresh child when parent is refreshed
DETACH	Detach child when parent is detached
ALL	Applies all cascade operations

Be careful with:

CascadeType.REMOVE

because it may delete related records unexpectedly.

### 9. What is Fetch Type?

Fetch type controls when related data is loaded.

Fetch Type	Meaning
EAGER	Load immediately
LAZY	Load only when accessed

Example:

@OneToMany(fetch = FetchType.LAZY)
private List<Order> orders;

Usually prefer LAZY for collections to avoid loading too much data.

### 10. First-Level Cache vs Second-Level Cache
First-Level Cache
Enabled by default
Exists inside one Hibernate session / persistence context
Cannot be disabled
Same entity loaded twice in same session comes from cache
User u1 = entityManager.find(User.class, 1L);
User u2 = entityManager.find(User.class, 1L);

u1 and u2 refer to the same managed object.

Second-Level Cache
Optional
Shared across sessions
Requires configuration
Useful for frequently read data
Cache	Scope	Default
First-level cache	One session	Enabled
Second-level cache	Across sessions	Disabled by default

### 11.SQL Joins

Assume:

users
id	name
1	Alice
2	Bob
orders
id	user_id	item
101	1	Book
102	3	Pen
INNER JOIN

Returns only matching rows from both tables.

SELECT *
FROM users u
INNER JOIN orders o
ON u.id = o.user_id;

Result: Alice with Book.

LEFT JOIN

Returns all rows from the left table and matched rows from the right table.

SELECT *
FROM users u
LEFT JOIN orders o
ON u.id = o.user_id;

Result:

Alice with Book
Bob with NULL
RIGHT JOIN

Returns all rows from the right table and matched rows from the left table.

SELECT *
FROM users u
RIGHT JOIN orders o
ON u.id = o.user_id;

Result:

Alice with Book
NULL user with Pen
FULL OUTER JOIN

Returns all rows from both tables.

SELECT *
FROM users u
FULL OUTER JOIN orders o
ON u.id = o.user_id;

Result:

Alice with Book
Bob with NULL
NULL user with Pen
CROSS JOIN

Returns Cartesian product.

SELECT *
FROM users
CROSS JOIN orders;

If users has 2 rows and orders has 2 rows, result has:

2 * 2 = 4 rows

### 12. UNION vs UNION ALL
UNION

Combines results and removes duplicates.

SELECT name FROM students
UNION
SELECT name FROM teachers;
UNION ALL

Combines results and keeps duplicates.

SELECT name FROM students
UNION ALL
SELECT name FROM teachers;
Topic	UNION	UNION ALL
Removes duplicates	Yes	No
Performance	Slower	Faster
Keeps all rows	No	Yes

Use UNION ALL when duplicates are allowed or performance matters.