# Transporter Assignment on Lanes

## Overview

The Transporter Assignment application is a Spring Boot REST API that assigns transporters to lanes based on their quoted prices.

The application aims to:
- Cover every lane with an available transporter.
- Minimize the total transportation cost.
- Maximize the number of distinct transporters used without exceeding the specified limit.
- Validate input data and return meaningful error messages when an assignment cannot be completed.

## Technologies Used

- Java 27
- Spring Boot
- Spring Web MVC
- Jakarta Validation dependency
- Maven
- JUnit 5
- IntelliJ IDEA
- Postman

## Project Structure

```text
src
├── main
│   └── java
│       └── com.example.transpoter
│           ├── TranspoterAssignmentApplication.java
│           ├── controller
│           │   └── TransporterController.java
│           ├── service
│           │   └── AssignmentService.java
│           ├── dto
│           │   └── ApiModels.java
│           └── exception
│               └── GlobalExceptionHandler.java
└── test
    └── java
        └── com.example.transpoter
            └── service
                └── AssignmentServiceTest.java
```

## How to Run the Application

### Prerequisites

- Java 17 or later
- IntelliJ IDEA or another Java IDE
- Maven Wrapper included in the project

### Steps

1. Open the project in IntelliJ IDEA.
2. Allow Maven to load the project dependencies.
3. Open `TranspoterAssignmentApplication.java`.
4. Run the main application class.
5. Confirm that the application starts on port `8080`.

Base URL:

```text
http://localhost:8080
```

## API Endpoints

### 1. Submit Lane and Transporter Data

**Endpoint**

```http
POST /api/v1/transporters/input
```

This endpoint accepts lane information, transporter details, and transporter quotes for individual lanes.

**Sample request**

```json
{
  "lanes": [
    {
      "id": 1,
      "origin": "Delhi",
      "destination": "Mumbai"
    },
    {
      "id": 2,
      "origin": "Mumbai",
      "destination": "Pune"
    }
  ],
  "transporters": [
    {
      "id": 101,
      "name": "Transporter A",
      "laneQuotes": [
        {
          "laneId": 1,
          "quote": 5000
        },
        {
          "laneId": 2,
          "quote": 3000
        }
      ]
    },
    {
      "id": 102,
      "name": "Transporter B",
      "laneQuotes": [
        {
          "laneId": 1,
          "quote": 4500
        }
      ]
    }
  ]
}
```

**Sample success response**

```json
{
  "status": "success",
  "message": "Input data saved successfully."
}
```

### 2. Calculate Transporter Assignment

**Endpoint**

```http
POST /api/v1/transporters/assignment
```

This endpoint calculates the assignment using the previously submitted input data.

**Sample request**

```json
{
  "maxTransporters": 2
}
```

**Sample response**

```json
{
  "status": "success",
  "totalCost": 7500,
  "assignments": [
    {
      "laneId": 1,
      "transporterId": 102
    },
    {
      "laneId": 2,
      "transporterId": 101
    }
  ],
  "selectedTransporters": [
    101,
    102
  ]
}
```

## Assignment Algorithm

The application uses dynamic programming with a bitmask to track the transporters selected for an assignment.

For each lane, the algorithm evaluates the available transporter quotes and retains the lowest-cost assignment for each exact set of selected transporters.

The final result prioritizes:

1. Covering every lane.
2. Using as many distinct transporters as possible, without exceeding `maxTransporters`.
3. Minimizing the total cost among assignments using the maximum feasible number of transporters.

If a lane cannot be covered, the application returns an error response instead of an incomplete assignment.

## Input Validation and Error Handling

The application checks for:
- Missing lane or transporter data.
- Empty lane lists.
- Duplicate or non-positive lane and transporter IDs.
- Missing lane origins, destinations, or transporter names.
- Quotes referring to unknown lanes.
- Duplicate quotes for the same lane and transporter.
- Negative quotes.
- Invalid `maxTransporters` values.
- Lanes that cannot be covered with the available quotes.

Errors are returned as JSON responses with an appropriate HTTP status code.

## Automated Testing

The project includes `AssignmentServiceTest.java` with tests covering:

- Finding a valid low-cost assignment.
- Maximizing transporter usage within the limit.
- Handling lanes that cannot be covered.
- Rejecting a zero transporter limit.

**Current test status:** All 4 automated tests passed.

## Sample Dataset Test

The application was tested using the supplied CSV dataset containing 9 lanes and 7 transporters.

With `maxTransporters` set to `3`, the application returned:

| Result | Value |
|---|---:|
| Total cost | 134876 |
| Selected transporters | 1, 4, 6 |
| Lanes covered | 9 |

## Notes

- The input data is stored in application memory.
- Submit the input data before requesting an assignment.
- Restarting the application clears the stored input.
- The algorithm is designed for a modest number of transporters; its bitmask-based state space grows exponentially as the number of transporters increases.
