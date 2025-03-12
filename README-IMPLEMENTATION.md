# Ktor RESTful API Implementation

This is an implementation of a simple RESTful API using Ktor 2.3.13 that interacts with the JsonPlaceholder API.

## Features

- **GET /api/posts**: Retrieves a list of all posts from JsonPlaceholder
- **GET /api/comments?userId={id}**: Retrieves all comments for a specific user
- **POST /api/posts**: Creates a new post
- **PUT /api/posts/{id}**: Updates an existing post
- **DELETE /api/posts/{id}**: Deletes a post

## Implementation Details

The application is structured as follows:

1. **Models**: Data classes for Post and Comment entities
2. **Services**: A JsonPlaceholderService that handles API calls to the JsonPlaceholder API
3. **Application**: The main application file that sets up the server and defines the endpoints using Ktor's module approach

Error handling is implemented to gracefully handle various error scenarios:
- HTTP errors (4xx, 5xx) from the JsonPlaceholder API
- Network errors
- Invalid input parameters
- JSON parsing errors

### Ktor 2.3.13 Specific Implementation Notes

This implementation is compatible with Ktor 2.3.13, which has some API differences compared to Ktor 3.x:

- Uses the module approach with `Application.module()` function
- Uses `ContentNegotiation` with Jackson for serialization/deserialization
- Uses `ResponseException` for HTTP error handling
- Uses `parameter()` function for URL parameters

### Ktor Module System

The Ktor module system requires proper integration between the application.conf file and the Application.kt file:

- In application.conf, we specify the module function to load: `modules = [ com.epam.mentoring.ApplicationKt.main ]`
- In Application.kt, we define the `Application.main()` extension function that gets called when the application starts
- The `main()` extension function delegates to the `module()` function which contains the actual application setup

This approach allows for better separation of concerns and more flexible configuration of the application.


## Running the Application

### Prerequisites

- JDK 21 or newer
- Maven

### Steps

1. Clone the repository
2. Navigate to the project root directory
3. Run the following command:

```bash
mvn compile exec:java
```

The server will start on port 8080.

## API Usage Examples

### Get all posts

```bash
curl -X GET http://localhost:8080/api/posts
```

### Get comments by user ID

```bash
curl -X GET http://localhost:8080/api/comments?userId=1
```

### Create a new post

```bash
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "title": "New Post", "body": "This is a new post."}'
```

### Update an existing post

```bash
curl -X PUT http://localhost:8080/api/posts/1 \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "title": "Updated Post", "body": "This post has been updated."}'
```

### Delete a post

```bash
curl -X DELETE http://localhost:8080/api/posts/1
```

## Testing

The application includes integration tests for all endpoints. To run the tests, use:

```bash
mvn test
``` 