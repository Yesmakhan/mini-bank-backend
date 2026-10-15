# Mini-Bank Backend Service

A thread-safe RESTful banking service built with **Java**, **Spring Boot**, and **PostgreSQL**. Designed to handle user accounts and concurrent money transfers with strict ACID guarantees and deadlock prevention.

## Tech Stack
* **Language:** Java 17+
* **Framework:** Spring Boot (Spring Web, Spring Data JDBC / Transactions)
* **Database:** PostgreSQL, JDBC
* **Concurrency:** Java Multithreading, Lock Ordering, Pessimistic/Transactional Locking
* **Build & Testing:** Maven, JUnit 5

## Key Features & Architecture
* **Account Management:** Create and manage multiple bank accounts per user.
* **Concurrent Money Transfers:** Thread-safe balance updates preventing race conditions and lost updates under parallel load.
* **Deadlock Prevention:** Deterministic lock ordering by account ID during bilateral transfers.
* **Audit Logging:** Complete transactional history of all credit/debit operations stored in PostgreSQL.

## Database Schema
* `users` — Client profiles and metadata.
* `accounts` — Account balances and ownership references.
* `transactions` — Immutable audit log of completed and failed transfers.