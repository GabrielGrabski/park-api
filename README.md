# 🏞️ Welcome to Park API!

## ❓ What is this project?

The **Pawnee Parks & Recreation Department** is building a new internal tool to help **Leslie Knope** and her team
manage relationships with vendors, community members, and local businesses.

You’ve been asked to create the foundation for a **Customer Service API** to help the department track the people they
work with and eventually provide helpful, automated insights.

---

## 📝 Design and Implementation Notes

### 1️⃣ The fields chosen for the Customer model and why

- **id (UUID)**: Globally unique identifier for each customer, ensures no collisions.
- **name (String)**: Required for identification and display.
- **email (String)**: Required and unique, used for login or contact.
- **type**: Differentiates customer categories.
- **status (Enum: ACTIVE / INACTIVE)**: Enables soft deletion and historical tracking.
- **createdAt / updatedAt (LocalDateTime)**: For auditing, sorting, and tracking changes.

---

### 2️⃣ Approach to Testing, Error Handling, and Extensibility

**Testing:**

- Used **MockMvc** for controller tests and **Mockito** for service mocking.
- Covered edge cases like invalid input, duplicate emails, not-found resources, and empty pages.
- Tested both **happy paths** and **failure scenarios** for robust coverage.

**Error Handling:**

- Centralized exceptions using **custom exception classes** (`CustomerNotFoundException`, `CustomerAlreadyExistsException`).
- Standardized error responses with `code` and `message`.

**Extensibility:**

- Used **DTOs** for input/output to decouple API from database model.
- `CustomerStatus` and `CustomerType` enums allow easy addition of new statuses or types.
- Soft deletion (`INACTIVE` status) preserves historical data and allows future recovery.

---

### 3️⃣ Shortcuts or Assumptions Made Due to 2-Hour Timebox

- Limited business logic; focus was on CRUD operations and validation.
- Used **in-memory database (H2) or simplified DB setup** for testing.
- Logging, metrics, and additional error details were minimized to meet the time constraint.


## 📌 Endpoints

### 1️⃣ Create Customer

**POST** `/api/v1/customers`

### 2️⃣ Get All Customers

**GET** `/api/v1/customers`

### 3️⃣ Get Customer By ID

**GET** `/api/v1/customers/{id}`

### 4️⃣ Delete Customer

**DELETE** `/api/v1/customers/{id}`

---

## 📖 API Documentation (Swagger)

This API uses **Springdoc OpenAPI** to generate interactive documentation. Swagger UI allows you to explore and test all API endpoints directly from your browser.

---

### 🔹 Access URLs

- **Swagger UI (interactive web interface):**  
  [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

- **OpenAPI JSON (API definition in JSON format):**  
  [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 🚀 How to run?

### 1️⃣ Build Docker image

```bash
docker build -t park-api .
```

2️⃣ Run the container

```bash
docker run -p 8080:8080 park-api
```

Now your API should be up and running! ✅

## Trade-offs

### Swagger:

* For better separation of responsibilities, we can leave Swagger in a separate project where we write the YAML. The
  downside would be more work to document our APIs, as it will have to be written manually and there's potential for
  human error.

### Security

* To increase security, we could add JWT login to ensure our API is stateless. We could also add roles to make our API
  even more secure, ensuring that each user has the appropriate access and cannot access inappropriate resources (such
  as a guest user not being able to deactivate another user).

### Architecture

* Because it's a very small project, I preferred to use a layered architecture, dividing resources into controllers,
  services, and repositories. However, there's still room for refactoring to add other architectural types; this would
  depend largely on the project's scope.

### Caching

* As the amount of data in our database grows, we can add a caching layer, where our data would be retrieved more
  quickly.

### Database

* I'm using an in-memory database (H2). However, the correct approach for a real application would be to use a
  relational database (such as PostgreSQL) or a non-relational database, depending on the need.

### Insights feature

* To be honest, I tried to implement the insights feature using Hugging Face AI, but I realized that it would take to
  long, and then I decided to do not implement the functionality

## 🤖 ChatGPT AI Usage

### Prompt

```text
I have a Java project called park-api. I need a performatic and clean Dockerfile that is responsible by execute the clean package and build my application. Please, use multi stage and workspaces and Java 21
```

### Response

```dockerfile
# Stage 1: Build
FROM maven:3.9.4-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy only pom.xml and download dependencies first (cache)
COPY pom.xml . 
COPY .mvn .mvn
COPY mvnw . 
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/park-api-*.jar app.jar

# Expose port (adjust if needed)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java","-jar","app.jar"]
```