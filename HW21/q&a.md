1. Encryption vs Hashing vs Encoding
   Encryption	Hashing	Encoding
   Protect confidential data	Verify integrity	Convert data format
   Reversible with a key	One-way	Reversible
   Uses encryption key	No key	No key
   AES, RSA	BCrypt, Argon2, SHA-256	Base64, URL Encoding
   Interview answer

Encryption protects sensitive data and can be decrypted with a key. For example, HTTPS uses TLS encryption to protect data in transit.

Hashing is a one-way operation mainly used for password storage and integrity checks. Passwords should be hashed using algorithms like BCrypt or Argon2 rather than encrypted.

Encoding is not a security mechanism; it simply converts data into another format for transmission or storage, such as Base64 encoding.

Real example

HTTPS → Encryption
User password → BCrypt Hash
JWT parts → Base64URL Encoding
2. Authentication vs Authorization
   Authentication

Who are you?

Examples

Username/password
Google Login
JWT validation
Authorization

What can you do?

Examples

Admin
↓
Can delete users

User
↓
Can only view profile
Interview answer

Authentication verifies the user's identity, while authorization determines what resources the authenticated user is allowed to access. Authentication happens first, followed by authorization.

3. HTTPS, Certificates, Key Rotation
   HTTPS

HTTPS is HTTP over TLS.

It provides:

Encryption
Authentication
Integrity

Without HTTPS

Browser
Password
──────────────►

Anyone can read it

With HTTPS

Encrypted

Browser
TLS
──────────────► Server
Certificates

A certificate proves the server's identity.

Contains

Public key
Domain
Issuer (CA)
Expiration date

Common Certificate Authorities

DigiCert
Let's Encrypt
GlobalSign
TLS Handshake

Very common interview question.

Client connects.
Server sends certificate.
Client verifies certificate.
Client generates session key.
Session key encrypted using server public key.
Both use symmetric encryption (AES).
Key Rotation

Interview answer:

Key rotation means periodically replacing encryption keys or signing keys to reduce the impact of key compromise.

Common examples:

Rotate JWT signing keys
Rotate AWS KMS keys
Rotate database encryption keys
Rotate Kubernetes Secrets

Many systems use a Key ID (kid) in the JWT header so multiple signing keys can coexist during rotation.

4. JWT Token Structure

JWT has three parts

Header.Payload.Signature

Example

eyJhbGc...
.
eyJzdWI...
.
VjA34...
Header

Contains

{
"alg":"HS256",
"typ":"JWT"
}
Payload

Contains claims

{
"sub":"12345",
"username":"john",
"role":"ADMIN",
"exp":1711111111
}

Common claims

sub
iss
aud
exp
iat
roles
Signature

Generated using

Base64(Header)
+
Base64(Payload)
+
Secret/private key

This prevents token tampering.

Interview answer

A JWT consists of a header, payload, and signature. The header specifies the signing algorithm, the payload contains user claims such as roles and expiration time, and the signature ensures the token has not been modified. The payload is only Base64URL encoded, not encrypted, so sensitive information should never be stored in it.

5. OAuth 2.0 vs OIDC

Many people confuse these.

OAuth2

OAuth2 is an authorization framework.

Purpose:

Grant access to resources.

Example:

Login using Google.

Application receives an Access Token.

OIDC (OpenID Connect)

OIDC is built on top of OAuth2.

Purpose:

Authenticate users.

Provides

ID Token (JWT)
User profile
Login information
OAuth2 Tokens
Access Token
Refresh Token
OIDC adds
ID Token
Interview answer

OAuth2 is designed for authorization, allowing an application to access resources on behalf of a user. OpenID Connect extends OAuth2 by adding an ID Token and standardized identity information, making it suitable for user authentication. In practice, when we implement "Login with Google" or "Login with Azure AD," we are usually using OIDC on top of OAuth2.

6. LDAP

LDAP stands for

Lightweight Directory Access Protocol

Used for centralized authentication.

Common examples

Microsoft Active Directory
OpenLDAP

Example

Company has

10000 employees

Instead of storing passwords inside every application

Application →

LDAP →

Authenticate employee

Spring Security supports LDAP directly.

Interview answer

LDAP is a protocol for accessing and managing directory services, commonly used for enterprise authentication. Instead of maintaining user accounts within each application, authentication requests are delegated to a centralized LDAP server such as Active Directory.

7. SQL Injection & Prepared Statements

SQL Injection

Bad example

String sql =
"SELECT * FROM users WHERE username='"
+ username
+ "'";

If user inputs

' OR '1'='1

SQL becomes

SELECT *
FROM users
WHERE username=''
OR '1'='1'

Returns every user.

Prepared Statement

Good example

PreparedStatement ps =
connection.prepareStatement(
"SELECT * FROM users WHERE username=?");

ps.setString(1, username);

The database treats the input strictly as data, not executable SQL.

How to prevent SQL Injection
Use Prepared Statements
Use JPA/Hibernate parameter binding
Never concatenate SQL strings
Validate user input
Apply the principle of least privilege for database accounts
Use ORM frameworks whenever possible