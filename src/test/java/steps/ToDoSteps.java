package steps;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import io.restassured.response.Response;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.*;

public class ToDoSteps {
    private Process serviceProcess;

    @Given("the service is running")
    public void serviceRunning() {
        RestAssured.baseURI = "http://localhost:4567";
        if (isServiceRunning()) {
            try {
                given().when().get("/shutdown");
            } catch (Exception e) {
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startService();
        } else {
            startService();
        }
    }

    private boolean isServiceRunning() {
        try {
            URL serviceUrl = new URL("http://localhost:4567");
            HttpURLConnection serviceConnection = (HttpURLConnection) serviceUrl.openConnection();
            serviceConnection.setRequestMethod("GET");
            serviceConnection.connect();
            int serviceResponse = serviceConnection.getResponseCode();

            return serviceResponse == 200;
        } catch (IOException e) {
            return false;
        }
    }

    private void startService() {
        try {
            serviceProcess = new ProcessBuilder("java", "-jar", "runTodoManagerRestAPI-1.5.5.jar").start();
            Thread.sleep(500);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String endpoint;
    private Response response;

    @When("I send a POST request to {string} using title: {string} and description: {string}")
    public void postToDo(String endpoint, String title, String description) {
        this.endpoint = endpoint;
        String JsonParams = "{\"title\": \"" + title + "\", \"description\": \"" + description + "\"}";
        response = given()
                .contentType("application/json")
                .body(JsonParams)
                .when()
                .post(this.endpoint);
    }

    @Then("I should receive a response status code of {int}")
    public void verifyStatusCode(int statusCode) {
        response.then()
                .statusCode(statusCode);
    }

    @And("the response should have a todo task with title: {string} and description: {string}")
    public void verifyToDoResponseParams(String title, String description) {
        response.then()
                .body("title", equalTo(title))
                .body("description", equalTo(description));
    }

    @When("I send a POST request to {string} with title {string} and description {string}")
    public void postProject(String endpoint, String title, String description) {
        this.endpoint = endpoint;
        String projectJsonParams = "{\"title\": \"" + title + "\", \"description\": \"" + description + "\"}";
        response = given()
                .contentType("application/json")
                .body(projectJsonParams)
                .when()
                .post(this.endpoint);
    }

    @And("the response should contain a project with name {string} and description {string}")
    public void verifyProjectResponseParams(String title, String description) {
        response.then()
                .body("title", equalTo(title))
                .body("description", equalTo(description));
    }
}
