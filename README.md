Task is in task.zip password is "task" (to avoid indexing by search engines)

Install:
mvn install

Startup:
cd target
java - jar test-task-0.0.1-SNAPSHOT.jar

Usage:
POST http://127.0.0.1:8080/validateWithSpring
{
    "customer" : "012345678",
    "seller" : "012345679",
    "products": [
        {"name":"product1","code":"0123456789012"},
        {"name":"product2","code":"3656352437590"}
    ]
}

POST http://127.0.0.1:8080/validateWithArgCache
Validation responses are cached and app will log once new values are computed

POST http://127.0.0.1:8080/validateWithFunctionAndArgCache
Validation responses are cached (using response calculation as a key) and app will log once new values are computed