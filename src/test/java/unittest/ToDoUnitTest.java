package unittest;

import io.restassured.RestAssured; //1
import static io.restassured.RestAssured.given; // 3
import static org.junit.jupiter.api.Assertions.*;
import io.restassured.response.Response; // 2
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.After;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.HashMap;

// Run unit tests in a random order
@TestMethodOrder(Random.class)
public class ToDoUnitTest {
    private int testId;
    private int newId1;
    private int newId2;
    private int fakeId = -1;
    private final String taskTitle = "Test To Do";
    private final String taskDescription = "This is a description of the test To Do";
    private final String newTaskTitle = "New title";
    private final String newTaskDescription = "New description";
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
    public void createToDo() {
        Map<String, String> testParams = new HashMap<>();
        testParams.put("title", taskTitle);
        testParams.put("description", taskDescription);
        Response response = given()
                .contentType("application/json")
                .body(testParams)
                .when()
                .post("/todos");

        assertEquals(201, response.getStatusCode());

        testId = response.jsonPath().getInt("id"); // get the id of the newly created task
    }

    // Delete the todo after each test runs
    @AfterEach
    public void deleteToDo(){
        if(!deleted){
            Response response = given()
                    .pathParam("id", testId)
                    .when()
                    .delete("/todos/{id}");

            assertEquals(200, response.getStatusCode());
        }
        deleted = false;
    }

    // Test to get all todo headers
    @Test
    void testGetAllToDoHeaders(){
        Response response = given()
                .when()
                .head("/todos");
        assertEquals(200, response.getStatusCode());
    }

    // Test to get all todo options
    @Test
    void testGetAllToDoOptions(){
        Response response = given()
                .when()
                .options("/todos");
        assertEquals(200, response.getStatusCode());
    }

    // Test to get all the todo ids with json
    @Test
    void testGetAllToDosIdJson(){
        Response response = given()
                .contentType("application/json")
                .when()
                .get("/todos");
        assertEquals(200, response.getStatusCode());
    }

    // Test to get all the todo ids with xml
    @Test
    void testGetAllToDosIdXml(){
        Response response = given()
                .contentType("application/xml")
                .when()
                .get("/todos");
        assertEquals(200, response.getStatusCode());
    }

    // Test to get all todos with done status false
    @Test
    void testGetAllNotDoneToDo() {
        Response response = given()
                .when()
                .get("/todos?doneStatus=false");
        assertEquals(200, response.getStatusCode());
    }

    // Test to create a todo task with json
    @Test
    void testCreateToDoJson(){
        Map<String, String> testParams = new HashMap<>();
        testParams.put("title", taskTitle);
        testParams.put("description", taskDescription);
        Response response = given()
                .contentType("application/json")
                .body(testParams)
                .when()
                .post("/todos");

        assertEquals(201, response.getStatusCode());

        newId1 = response.jsonPath().getInt("id"); // get the id of this task which we will delete in another test
    }

    // Delete the new json todo created above
    @After
    void deleteToDoJson(){
        Response response = given()
                .pathParam("id", newId1)
                .when()
                .delete("/todos/{id}");

        assertEquals(200, response.getStatusCode());
    }

    // Test to create a todo task with xml
    @Test
    void testCreateToDoXml(){
        String xmlParams = "<todo>"
                + "<title> " + taskTitle + " </title>"
                + "<description> " + taskDescription + " </description>"
                + "</todo>";

        Response response = given()
                .contentType("application/xml")
                .body(xmlParams)
                .when()
                .post("/todos");

        assertEquals(201, response.getStatusCode());

        newId2 = response.jsonPath().getInt("id"); // get the id of this task which we will delete in another test
    }

    // Delete the new xml todo created above
    @After
    void deleteToDoXml(){
        Response response = given()
                .pathParam("id", newId2)
                .when()
                .delete("/todos/{id}");

        assertEquals(200, response.getStatusCode());
    }

    // Test to get a todo with its id in json
    @Test
    void testGetToDoIDJson(){
        Response response = given()
                .contentType("application/json")
                .pathParam("id", testId)
                .when()
                .get("/todos/{id}");

        assertEquals(200, response.getStatusCode());
    }

    // Test to get a todo with its id in xml
    @Test
    void testGetToDoIDXml(){
        Response response = given()
                .contentType("application/xml")
                .pathParam("id", testId)
                .when()
                .get("/todos/{id}");

        assertEquals(200, response.getStatusCode());
    }

    // Test to get the header of a todo with its id
    @Test
    void testGetToDoHeaderId(){
        Response response = given()
                .pathParam("id", testId)
                .when()
                .head("/todos/{id}");
        assertEquals(200, response.getStatusCode());
    }

    // Test to get the options of a todo with its id
    @Test
    void testGetOptionsId(){
        Response response = given()
                .pathParam("id", testId)
                .when()
                .options("/todos/{id}");
        assertEquals(200, response.getStatusCode());
    }

    // Test to update an existing todo with new title and new description using put
    @Test
    void testUpdateToDoPut() {
        Map<String, String> newParams = new HashMap<>();
        newParams.put("title", newTaskTitle);
        newParams.put("description", newTaskDescription);

        Response response = given()
                .pathParam("id", testId)
                .contentType("application/json")
                .body(newParams)
                .when()
                .put("/todos/{id}");

        assertEquals(200, response.getStatusCode());
    }

    // Test to update an existing todo with new title and new description using post
    @Test
    void testUpdateToDoPost() {
        Map<String, String> newParams = new HashMap<>();
        newParams.put("title", newTaskTitle);
        newParams.put("description", newTaskDescription);

        Response response = given()
                .pathParam("id", testId)
                .contentType("application/json")
                .body(newParams)
                .when()
                .post("/todos/{id}");

        assertEquals(200, response.getStatusCode());
    }

    // Test to delete an existing todo
    @Test
    void testDeleteToDo(){
        Response response = given()
                .pathParam("id", testId)
                .when()
                .delete("/todos/{id}");

        assertEquals(200, response.getStatusCode());
        deleted = true;
    }


    // Test failure to update an existing todo with no title and a new description using put
    @Test
    void testUpdateToDoPutNoTitle() {
        Map<String, String> newParams = new HashMap<>();
        newParams.put("description", newTaskDescription);

        Response response = given()
                .pathParam("id", testId)
                .contentType("application/json")
                .body(newParams)
                .when()
                .put("/todos/{id}");

        assertEquals(400, response.getStatusCode());
    }

    // Test failure to get a todo with a non-existing id
    @Test
    void testGetToDoFakeId(){
        Response response = given()
                .pathParam("id", fakeId)
                .when()
                .get("/todos/{id}");

        assertEquals(404, response.getStatusCode());
    }

    // Test failure to create a todo with string done status in json
    @Test
    void testCreateToDoMalformedPayloadJson() {
        Map<String, String> testParams = new HashMap<>();
        testParams.put("title", taskTitle);
        testParams.put("description", taskDescription);
        testParams.put("doneStatus", "true");

        Response response = given()
                .contentType("application/json")
                .body(testParams)
                .when()
                .post("/todos");

        assertEquals(400, response.getStatusCode());
    }

    // Test failure to create a todo with id in xml
    @Test
    void testCreateToDoMalformedPayloadXml(){
        String xmlParams = "<todo>"
                + "<title> " + taskTitle + " </title>"
                + "<description> " + taskDescription + " </description>"
                + "<id> " + "1" + " </id>"
                + "</todo>";

        Response response = given()
                .contentType("application/xml")
                .body(xmlParams)
                .when()
                .post("/todos");

        assertEquals(400, response.getStatusCode());
    }

    // Test failure to patch a todo with its id
    @Test
    void testPatchToDoID(){
        Map<String, String> newParams = new HashMap<>();
        newParams.put("title", newTaskTitle);
        newParams.put("description", newTaskDescription);

        Response response = given()
                .pathParam("id", testId)
                .contentType("application/json")
                .body(newParams)
                .when()
                .patch("/todos/{id}");

        assertEquals(405, response.getStatusCode());
    }

}
