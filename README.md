# Budgeting App — OOP Project

A full-stack budgeting application built with:
- **Frontend**: HTML, CSS, JavaScript
- **Backend**: Java (Spring Boot)
- **Database**: MySQL

---

## Project Structure

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

## Database Setup

### Prerequisites
- MySQL 8.0 installed and running

### Steps

1. Clone this repository

2. Run the SQL script to create the database:

   **Windows:**
```bash
   "C:/Program Files/MySQL/MySQL Server 8.0/bin/mysql" -u root -p < database/schema_mysql.sql
```
   **Mac/Linux:**
```bash
   mysql -u root -p < database/schema_mysql.sql
```
   > If `mysql` is not recognized on Windows, add MySQL to your PATH or use MySQL Workbench instead (see below)

3. Configure your database credentials in `src/main/resources/application.properties`:
```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/budgeting_db?serverTimezone=UTC&useSSL=false
   spring.datasource.username=root
   spring.datasource.passwordYOUR_PASSWORD_HERE
```

4. Run the Spring Boot application — the database is ready.

---

### Alternative: Using MySQL Workbench
1. Open MySQL Workbench and connect to your local server
2. Go to **File → Open SQL Script** and select `database/schema_mysql.sql`
3. Click the ⚡ (Execute) button

---

### Troubleshooting
- **"mysql is not recognized"** → Add MySQL to your system PATH, or use the full path to `mysql.exe`
- **"Access denied"** → Double check your username and password
- **"Can't connect to MySQL server"** → Make sure MySQL service is running