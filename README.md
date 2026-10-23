# Transporter Assignment on Lanes

A Spring Boot backend application that assigns transporters to trade lanes based on transporter quotes and a maximum transporter limit. The application exposes REST APIs for submitting quote data and calculating lane assignments.

## 🔗 Project Links

- **GitHub Repository:** https://github.com/Prakhar-70/transpoter-assignment111
- **Live Backend API:** https://transpoter-assignment.onrender.com
- **Swagger API Documentation:** https://transpoter-assignment.onrender.com/swagger-ui/index.html
- **Loom Demo Video:** https://www.loom.com/share/48a72fe965514916bb38afc8c361e933

## 📌 Project Overview

The Transporter Assignment application accepts trade lane information and transporter quotes, then calculates assignments using the available quote data and the specified maximum number of transporters.

The application provides endpoints to submit input data and retrieve the calculated assignment, including lane-wise transporter allocations, selected transporter IDs, and total cost.

## ✨ Features

- RESTful API development using Spring Boot
- Input data submission and processing
- Transporter assignment across trade lanes
- Assignment calculation based on a maximum transporter limit
- Total assignment cost calculation
- Input validation and exception handling
- Swagger UI for API documentation and testing
- Docker configuration for deployment

## 🛠️ Tech Stack

- **Language:** Java
- **Backend Framework:** Spring Boot
- **Build Tool:** Maven
- **API Documentation:** Swagger / OpenAPI
- **Deployment:** Render
- **Containerization:** Docker

## 🏗️ Project Architecture

The application follows a layered architecture:

- **Controller Layer:** Exposes REST endpoints and handles incoming requests.
- **Service Layer:** Implements business logic and assignment calculations.
- **Model / DTO Layer:** Represents input data, requests, and responses.
- **Exception Handling:** Handles invalid inputs and assignment errors.

## 🚀 API Endpoints

### 1. Submit Transporter Input Data

**Endpoint:** `POST /api/v1/transporters/input`

Accepts trade lane details and transporter quotes.

**Example response:**

```json
{
  "status": "success",
  "message": "Input data saved successfully."
}
```

### 2. Calculate Transporter Assignment

**Endpoint:** `POST /api/v1/transporters/assignment`

Calculates transporter assignments using the provided maximum transporter limit.

**Example request:**

```json
{
  "maxTransporters": 3
}
```

**Example response structure:**

```json
{
  "status": "success",
  "totalCost": 134876,
  "assignments": [
    {
      "laneId": 1,
      "transporterId": 4
    },
    {
      "laneId": 2,
      "transporterId": 1
    }
  ],
  "selectedTransporters": [1, 4, 6]
}
```

*Note: The response above illustrates the response structure. The actual cost, lane assignments, and selected transporters depend on the input dataset.*

## 🧪 Testing the APIs

You can test the endpoints through the deployed Swagger interface:

**Swagger UI:** https://transpoter-assignment.onrender.com/swagger-ui/index.html

1. Open the Swagger UI.
2. Execute `POST /api/v1/transporters/input` with the transporter quote data in the expected JSON format.
3. Confirm that the input data is accepted successfully.
4. Execute `POST /api/v1/transporters/assignment` with a request such as:

   ```json
   {
     "maxTransporters": 3
   }
   ```

5. Review the returned total cost, lane assignments, and selected transporter IDs.

## 📊 Sample Dataset

The project was tested using transporter quote data converted from CSV into JSON.

The dataset used for testing contains:
- 9 trade lanes
- 7 transporters
- 63 transporter quotes

The CSV dataset provides the quote values for each transporter across the available lanes.

## ⚙️ Run Locally

### Prerequisites

- Java Development Kit (JDK)
- Maven
- Git

### Steps

1. Clone the repository:

   ```bash
   git clone https://github.com/Prakhar-70/transpoter-assignment111.git
   ```

2. Navigate to the project directory:

   ```bash
   cd transpoter-assignment111/transpoter
   ```

3. Build the project:

   ```bash
   mvn clean package
   ```

4. Run the application:

   ```bash
   mvn spring-boot:run
   ```

5. Open the Swagger UI locally, if enabled:

   http://localhost:8080/swagger-ui/index.html

## ☁️ Deployment

The backend is deployed on Render.

- **Application URL:** https://transpoter-assignment.onrender.com
- **API Documentation:** https://transpoter-assignment.onrender.com/swagger-ui/index.html

Docker configuration is included in the repository for containerized deployment.

## 🎥 Demo Video

Watch the project walkthrough to see the application structure, deployed APIs, and API testing:

**Loom Demo:** https://www.loom.com/share/48a72fe965514916bb38afc8c361e933

## 👨‍💻 Author

**Prakhar Pandey**

- GitHub: https://github.com/Prakhar-70

---

*This project demonstrates backend API development, layered architecture, transporter assignment logic, input validation, exception handling, and deployment using Spring Boot.*
