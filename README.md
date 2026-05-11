# Budgeting App — OOP Project

A full-stack budgeting application built with:
- **Frontend**: HTML, CSS, JavaScript
- **Backend**: Java (Spring Boot)
- **Database**: MySQL

---

## 📁 Project Structure

```
budgeting-app/
├── bin/                          ← Compiled Java classes (auto-generated)
├── database/
│   └── schema.sql                ← Run this in MySQL Workbench first!
├── src/
│   └── main/
│       ├── java/com/budget/app/
│       │   ├── BudgetingApplication.java   ← App entry point
│       │   ├── controller/                 ← Handles HTTP requests
│       │   │   ├── UserController.java
│       │   │   ├── BudgetController.java
│       │   │   └── TransactionController.java
│       │   ├── model/                      ← OOP classes (entities)
│       │   │   ├── User.java
│       │   │   ├── Budget.java
│       │   │   └── Transaction.java
│       │   ├── repository/                 ← Talks to the database
│       │   │   ├── UserRepository.java
│       │   │   ├── BudgetRepository.java
│       │   │   └── TransactionRepository.java
│       │   └── service/                    ← Business logic
│       │       ├── UserService.java
│       │       ├── BudgetService.java
│       │       └── TransactionService.java
│       └── resources/
│           ├── application.properties      ← DB config (edit this!)
│           └── static/                     ← Your website files
│               ├── index.html
│               ├── css/style.css
│               └── js/app.js
└── pom.xml                       ← Maven dependencies
```

---

## ⚙️ Setup Instructions

### Step 1 — Install Requirements
- [Java JDK 17+](https://www.oracle.com/java/technologies/downloads/)
- [Maven](https://maven.apache.org/download.cgi) (or use the wrapper)
- [MySQL Server + MySQL Workbench](https://dev.mysql.com/downloads/)
- [VS Code](https://code.visualstudio.com/) with Java Extension Pack

---

### Step 2 — Set Up the Database
1. Open **MySQL Workbench**
2. Connect to your local MySQL server
3. Open the file `database/schema.sql`
4. Click the **⚡ Execute** button to run it
5. You should see `budgeting_db` appear in the schema list

---

### Step 3 — Configure Database Connection
Open `src/main/resources/application.properties` and update:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/budgeting_db
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD_HERE
```

---

### Step 4 — Run the App

**Option A — VS Code terminal:**
```bash
./mvnw spring-boot:run
```

**Option B — Command prompt:**
```bash
mvn spring-boot:run
```

**Option C — VS Code Spring Boot Extension:**
- Open `BudgetingApplication.java`
- Click the ▶️ Run button

---

### Step 5 — Open the Website
Visit: **http://localhost:8080**

---

## 🌐 API Endpoints

### Users
| Method | URL | Description |
|--------|-----|-------------|
| POST | /api/users/register | Register new user |
| POST | /api/users/login | Login |
| GET | /api/users | Get all users |
| DELETE | /api/users/{id} | Delete user |

### Budgets
| Method | URL | Description |
|--------|-----|-------------|
| GET | /api/budgets/user/{userId} | Get user's budgets |
| POST | /api/budgets/user/{userId} | Create budget |
| PUT | /api/budgets/{id} | Update budget |
| DELETE | /api/budgets/{id} | Delete budget |

### Transactions
| Method | URL | Description |
|--------|-----|-------------|
| GET | /api/transactions/budget/{budgetId} | Get transactions |
| POST | /api/transactions/budget/{budgetId} | Add transaction |
| DELETE | /api/transactions/{id} | Delete transaction |

---

## 🎓 OOP Concepts Used

| Concept | Where |
|---------|-------|
| **Classes & Objects** | User, Budget, Transaction |
| **Encapsulation** | Private fields + getters/setters |
| **Inheritance** | Repositories extend JpaRepository |
| **Abstraction** | Service layer hides DB logic |
| **Enum** | Transaction.Type (INCOME/EXPENSE) |
| **Annotations** | @Entity, @Service, @Controller |
