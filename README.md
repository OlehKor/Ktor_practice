# Getting Started

In this task you'll build a simple RESTful API using Ktor, an asynchronous framework tailored for servers and clients in Kotlin.

### Objectives:

- Understand the basics of Ktor server & HTTP client and its key components.
- Learn how to perform common HTTP requests (GET, POST, PUT, DELETE) using the Ktor HTTP client.
- Implement error handling for robust HTTP client operations.
- Introduce integration tests for newly created endpoints.

The service will use [Json Placeholder Public API](https://jsonplaceholder.typicode.com/) to perform various HTTP requests to it using Ktor HTTP client.

Optional: You can use Gradle instead of Maven to manage dependencies and build your project. Choice is up to you.

### The Task

0. **Add the Ktor HTTP client dependencies to your project.** <br>

1. **Add new HTTP endpoints**

- An endpoint that returns list of all posts retrieved from [Json Placeholder Public API](https://jsonplaceholder.typicode.com/posts)
- An endpoint that returns list of all comments for provided user, use [Json Placeholder Public API](https://jsonplaceholder.typicode.com/comments)
- An endpoint that creates a new post, use  [Json Placeholder Public API](https://jsonplaceholder.typicode.com/post)
- An endpoint that updates the post, use  [Json Placeholder Public API](https://jsonplaceholder.typicode.com/post/{id})
- An endpoint that deletes the post, use  [Json Placeholder Public API](https://jsonplaceholder.typicode.com/post/{id})

Refer to [Json Placeholder Public API Guide](https://jsonplaceholder.typicode.com/guide/) for more examples.

2. **Implement error handling mechanisms to gracefully handle HTTP errors (e.g., 4xx, 5xx status codes).**

3. **Add integration tests for the endpoints**

4. **Create a pull request and assign your mentor as a reviewer**

### Reference Documentation

For further reference, please consider the following sections:
* [ktor.io](https://ktor.io)
* [Creating HTTP APIs with Ktor](https://ktor.io/docs/server-create-restful-apis.html)
* [Ktor HTTP Client](https://ktor.io/docs/client-create-new-application.html#create-client)
* [Testing with Ktor](https://ktor.io/docs/server-testing.html#test-app)



