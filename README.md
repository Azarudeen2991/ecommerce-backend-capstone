# E-commerce Backend Capstone

A Java backend portfolio repository containing Spring Boot assignments completed as part of the Scaler e-commerce capstone. The work covers catalog operations, order flows, authentication, messaging, and payment gateway integrations.

## Project structure

This repository is a **Maven multi-module collection of independent assignment applications**. The root Maven project groups the modules so they can be built from one place; the modules are not yet wired into one deployed end-to-end application and may have their own data stores, ports, and configuration.

| Area | Modules | Focus |
| --- | --- | --- |
| Catalog | `modules/catalog/*` | Product models, search and deletion, category operations |
| Orders | `modules/orders/*` | Place and cancel orders, process order events |
| Authentication | `modules/auth/*` | Signup, login, validation, account activation, JWT issuance and validation |
| Payments | `modules/payments/razorpay/*`, `modules/payments/stripe/*` | Payment links, subscriptions, refunds, payouts, checkout and webhooks |
| Messaging | `modules/messaging/*` | Kafka based password reset email consumer |

Each module keeps its own `pom.xml`, source tree, configuration, and application entry point. This preserves the original assignment boundaries while giving the repository one root build and a consistent layout.

## Technology

- Java 17
- Spring Boot and Spring MVC
- Maven
- Spring Data JPA and H2 in selected modules
- JWT (JJWT)
- Apache Kafka in messaging and order event modules
- Razorpay and Stripe integrations in payment modules
- JUnit and Spring Boot Test dependencies in selected modules

## Requirements

- JDK 17
- Maven 3.8 or later
- External services and credentials only for modules that call payment gateways, Kafka, or email providers

## Build

Build all modules from the repository root:

```bash
mvn clean package
```

Build one application by changing to its module directory and running Maven there. For example:

```bash
cd modules/catalog/product-search
mvn spring-boot:run
```

Use `mvn test` from the root to run the module test suites. Some integration tests may need service credentials or external services; inspect the selected module's test configuration before running them.

## Configuration and secrets

Do not commit real credentials. Payment and JWT configuration uses environment variable placeholders where credentials are required. `.env.example` lists the supported variable names; Spring Boot does not load that file automatically, so export the values in your shell before starting the corresponding application:

```text
RAZORPAY_KEY_ID
RAZORPAY_KEY_SECRET
STRIPE_SECRET_KEY
JWT_SECRET_KEY
```

For PowerShell, for example:

```powershell
$env:RAZORPAY_KEY_ID = "your-test-key-id"
$env:RAZORPAY_KEY_SECRET = "your-test-key-secret"
$env:STRIPE_SECRET_KEY = "your-test-secret-key"
$env:JWT_SECRET_KEY = "your-long-random-signing-key"
```

Use provider test-mode credentials for local integration work. The JWT secret must be a sufficiently long random key for the configured signing algorithm. Never put credentials in source files, test fixtures, screenshots, or commit history. If a credential was previously pushed, revoke or rotate it at the provider; editing the current file does not remove it from Git history.

## Portfolio notes

This repository demonstrates backend coursework and implementation exercises across common e-commerce domains. Before describing a module as production-ready, verify its implementation, tests, service configuration, and end-to-end behavior. The current modules are independent apps; a future iteration can connect them through documented APIs and shared local infrastructure.
