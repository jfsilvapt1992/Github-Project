# Github-Project

This is a project about getting information from github api.

## Build

In all cases, you need to be in this repository folder and run the following commands.

1. With gradle

    ```bash
        ./gradlew clean build && bootRun
    ```

2. With docker
    When we are starting the application with docker, please remember that this can be a little bit slower and you can verify when the application started by looking at containers logs.

    ```bash
        docker-compose run -d app
    ```

3. IntelliJ 
    ```bash
        Go to GithubApplication.kt and click run application
    ```

## Testing
In this project we will find `unit tests` and `integration tests`. 
At the moment, it doesn't have 100% coverage of all use cases but, at least, shows how to implement
both kind of testing and how to run it.

1. Unit testing
    1. Gradle
        ```
            ./gradlew test
        ```
    2. Docker
        ```
            docker-compose run unit-tests
        ```

1. Integration Testing
    1. Gradle
        ```
            ./gradlew integrationTest
        ```
    2. Docker
        ```
            docker-compose run integration-tests
        ```
## Application

After we start up the application, it should be available in `http:localhost:8080/`.
More information about this REST API can be found at `http://localhost:8080/v1/swagger-ui.html`. This is a workin in progress, and some information is missing, such as an example of the endpoint response and what kind of errors are thrown. This could and should be an improvement in the future.

#### Requests

In this REST API we have only have one endpoints:

```bash
    HtppMethod - GET
    headers (mandatory ones) - github-token
    path - /users/{userName}/repositories"
    path parameters - userName (String)
    prodcues - application/jsom
 ```

 In this case, its mandatory to generate an personal github token to use this REST API, there is more information how to do it [here](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token#personal-access-tokens-classic). Please, pay attetion at the moment of select the permissions before generate the token, you can select all permissions, just to make sure you have the ones mandatory to communicate with github (will provide a better section about permissions needed in future).

 Example of request using curl:
 
 ```bash
    curl -v --header "Accept: application/json" --header "github-token: github_generated_token"  --request GET 'http://localhost:8080/users/jfsilvapt1992/repositories'
 ```
