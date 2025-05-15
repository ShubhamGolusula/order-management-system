# 🛒 Order Management System

This is a backend Java application for managing customer orders, built with **Spring Boot**, **Spring Data JPA**, **Hibernate**, and **MySQL**. It demonstrates a clean REST API design with layered architecture, DTO usage, and advanced querying including custom native SQL and JPA specifications.

---

## 🚀 Features

* 📦 Create, update, delete orders and products
* 👤 Manage customers and their order histories
* 📊 Track real-time order status (PENDING, CONFIRMED, SHIPPED, CANCELLED)
* 🔍 Custom queries to fetch latest status per order
* 📄 OpenAPI documentation using Swagger
* 🛠️ Docker support (optional)

---

## 🛠️ Tech Stack

| Layer      | Technology        |
| ---------- | ----------------- |
| Language   | Java 21           |
| Framework  | Spring Boot       |
| ORM        | Hibernate / JPA   |
| Database   | MySQL             |
| Build Tool | Maven             |
| REST Docs  | Swagger / OpenAPI |
| Container  | Docker (optional) |

---

## 🧱 Architecture

* `Controller` — Exposes REST APIs
* `Service` — Contains business logic
* `Repository` — JPA/Native queries
* `DTO` — Decouples entity exposure
* `Entity` — JPA-mapped domain models

---

## 📂 Project Structure

```
order-management-system/
├── src/
│   ├── main/
│   │   ├── java/com/shubham/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── model/
│   │   │   ├── dto/
│   │   │   └── enums/
│   │   └── resources/
│   │       ├── application.yml
├── .mvn/
├── pom.xml
└── README.md
```

---

## 🧪 Running Locally

1. **Clone the project:**

   ```bash
   git clone https://github.com/ShubhamGolusula/order-management-system.git
   cd order-management-system
   ```

2. **Set up the database:**
   Create a MySQL schema named `orders_db` and configure credentials in `application.yml`.

3. **Run the app:**

   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access APIs:**

   ```
   http://localhost:8080/api/v1/customers
   http://localhost:8080/api/v1/orders
   http://localhost:8080/api/v1/products
   ```

5. **Swagger UI:**

   ```
   http://localhost:8080/swagger-ui/index.html
   ```

---

## 🧠 Notable Implementations

* Use of `@ManyToMany` for Order-Item relations
* Enum persistence in database with `@Enumerated(EnumType.STRING)`
* Complex native queries using window functions (`RANK`, `MIN OVER PARTITION`)
* Swagger integration using `springdoc-openapi`

---

## ✅ To-Do

* Add JWT-based security
* Implement order analytics
* Add Dockerfile and `docker-compose.yml` for MySQL setup

---

## 👨‍💻 Author

**Shubham Golusula**
