package unittest;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import io.restassured.response.Response;
import jdk.nashorn.internal.runtime.ECMAException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.After;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

// Run unit tests in a random order
@TestMethodOrder(Random.class)
public class ToDoUnitTest {
    private int id1;
    private int id2;
    private final String taskTitle = "Test task";
    private final String taskDescription = "This is a description of the test task";
    private boolean deleted = false;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:4567";
    }

    // Test that the server is up
    @Test
    void testServer(){
        deleted = false;
        try{
            URL serverUrl = new URL("http://localhost:4567");
            HttpURLConnection serverConnection = (HttpURLConnection) serverUrl.openConnection();

            serverConnection.setRequestMethod("GET");
            int serverResponse = serverConnection.getResponseCode();

            assertEquals(200, serverResponse);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // Create a todo before each test runs
    @BeforeEach
    public void createTest() {
        Map<String, String> testParams = new HashMap<>();
        testParams.put("title", taskTitle);
        testParams.put("description", taskDescription);
        Response response = given()
                .contentType("application/json")
                .body(testParams)
                .when()
                .post("/todos");

        assertEquals(201, response.getStatusCode());

        id1 = response.jsonPath().getInt("id"); // get the id of the newly created task
    }

    // Delete the todo after each test runs
    @AfterEach
    public void deleteTest(){
        if(!deleted){
            Response response = given()
                    .pathParam("id", id1)
                    .when()
                    .delete("/todos/{id}");

            assertEquals(200, response.getStatusCode());
        }
        deleted = false;
    }


}
