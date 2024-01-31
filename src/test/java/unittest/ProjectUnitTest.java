package unittest;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

// Run unit tests in a random order
@TestMethodOrder(Random.class)
public class ProjectUnitTest {
    private int testToDoId;
    private int testProjectId;
    private int testProjectId2;
    private int newProjectId1;
    private int newProjectId2;
    private int fakeProjectId = -1;
    private final String toDoTitle = "Test To Do";
    private final String toDoDescription = "This is a description of the test To Do";
    private final String projectTitle = "Test Project";
    private final String projectDescription = "This is a description of the test project";
    private final String newProjectTitle = "New Project Title";
    private final String newProjectDescription = "New Project Description";
    private boolean toDoDeleted = false;
    private boolean projectDeleted = false;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:4567";
    }

    // Test that the server is up
    @Test
    void testServer(){
        toDoDeleted = false;
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

    // Create a todo, a project and an instance of a relationship between the two before each test runs
    @BeforeEach
    public void createToDoProjectRelationship() {
        Map<String, String> testTaskParams = new HashMap<>();
        testTaskParams.put("title", toDoTitle);
        testTaskParams.put("description", toDoDescription);
        Response response1 = given()
                .contentType("application/json")
                .body(testTaskParams)
                .when()
                .post("/todos");

        assertEquals(201, response1.getStatusCode());

        testToDoId = response1.jsonPath().getInt("id"); // get the id of the newly created task


        Map<String, String> testProjectParams = new HashMap<>();
        testProjectParams.put("title", projectTitle);
        testProjectParams.put("description", projectDescription);
        Response response2 = given()
                .contentType("application/json")
                .body(testProjectParams)
                .when()
                .post("/projects");

        assertEquals(201, response2.getStatusCode());

        testProjectId = response2.jsonPath().getInt("id"); // get the id of the newly created task


        Map<String, String> testRelationshipParams = new HashMap<>();
        testRelationshipParams.put("id", String.valueOf(testToDoId));
        Response response3 = given()
                .contentType("application/json")
                .body(testRelationshipParams)
                .pathParam("id", testProjectId)
                .when()
                .post("/projects/{id}/tasks");

        assertEquals(201, response3.getStatusCode());

        Response response4 = given()
                .contentType("application/json")
                .body(testProjectParams)
                .when()
                .post("/projects");

        assertEquals(201, response4.getStatusCode());

        testProjectId2 = response4.jsonPath().getInt("id"); // get the id of the newly created task
    }


    // Delete the todo after each test runs
    @AfterEach
    public void deleteToDo(){
        if(!toDoDeleted){
            Response response = given()
                    .pathParam("id", testToDoId)
                    .when()
                    .delete("/todos/{id}");

            assertEquals(200, response.getStatusCode());
        }
        toDoDeleted = false;
    }

    // Delete the projects after each test runs
    @AfterEach
    public void deleteProject(){
        if(!projectDeleted) {
            Response response = given()
                    .pathParam("id", testProjectId)
                    .when()
                    .delete("/projects/{id}");

            assertEquals(200, response.getStatusCode());
        }
        projectDeleted = false;

        Response response2 = given()
                .pathParam("id", testProjectId2)
                .when()
                .delete("/projects/{id}");

        assertEquals(200, response2.getStatusCode());


    }

    // Test to get all projects headers
    @Test
    void testGetAllProjectHeaders(){
        Response response = given()
                .when()
                .head("/projects");
        assertEquals(200, response.getStatusCode());
    }

    // Test to get all projects options
    @Test
    void testGetAllProjectOptions(){
        Response response = given()
                .when()
                .options("/projects");
        assertEquals(200, response.getStatusCode());
    }

    // Test to get all the project ids with json
    @Test
    void testGetAllProjectIdsJson(){
        Response response = given()
                .contentType("application/json")
                .when()
                .get("/projects");
        assertEquals(200, response.getStatusCode());
    }

    // Test to get all the project ids with xml
    @Test
    void testGetAllProjectIdsXml(){
        Response response = given()
                .contentType("application/xml")
                .when()
                .get("/projects");
        assertEquals(200, response.getStatusCode());
    }

    // Test to get all projects with completed false
    @Test
    void testGetAllNonCompletedProjects() {
        Response response = given()
                .when()
                .get("/todos?completed=false");
        assertEquals(200, response.getStatusCode());
    }

    // Test to create a project  with json
    @Test
    void testCreateProjectJson(){
        Map<String, String> testParams = new HashMap<>();
        testParams.put("title", projectTitle);
        testParams.put("description", projectDescription);
        Response response = given()
                .contentType("application/json")
                .body(testParams)
                .when()
                .post("/projects");

        assertEquals(201, response.getStatusCode());

        newProjectId1 = response.jsonPath().getInt("id"); // get the id of this project which we will delete in another test
    }

    // Delete the new json todo created above
    @After
    void deleteProjectJson(){
        Response response = given()
                .pathParam("id", newProjectId1)
                .when()
                .delete("/projects/{id}");

        assertEquals(200, response.getStatusCode());
    }

    // Test to create a project task with xml
    @Test
    void testCreateProjectXml(){
        String xmlParams = "<todo>"
                + "<title> " + projectTitle + " </title>"
                + "<description> " + projectDescription + " </description>"
                + "</todo>";

        Response response = given()
                .contentType("application/xml")
                .body(xmlParams)
                .when()
                .post("/projects");

        assertEquals(201, response.getStatusCode());

        newProjectId2 = response.jsonPath().getInt("id"); // get the id of this project which we will delete in another test
    }

    // Delete the new xml project created above
    @After
    void deleteToDoXml(){
        Response response = given()
                .pathParam("id", newProjectId2)
                .when()
                .delete("/projects/{id}");

        assertEquals(200, response.getStatusCode());
    }

    // Test to get a project with its id in json
    @Test
    void testGetProjectIDJson(){
        Response response = given()
                .contentType("application/json")
                .pathParam("id", testProjectId)
                .when()
                .get("/projects/{id}");

        assertEquals(200, response.getStatusCode());
    }

    // Test to get a project with its id in xml
    @Test
    void testGetProjectIDXml(){
        Response response = given()
                .contentType("application/xml")
                .pathParam("id", testProjectId)
                .when()
                .get("/projects/{id}");

        assertEquals(200, response.getStatusCode());
    }

    // Test to get the header of a project with its id
    @Test
    void testGetProjectHeaderId(){
        Response response = given()
                .pathParam("id", testProjectId)
                .when()
                .head("/projects/{id}");
        assertEquals(200, response.getStatusCode());
    }

    // Test to get the options of a project with its id
    @Test
    void testGetOptionsId(){
        Response response = given()
                .pathParam("id", testProjectId)
                .when()
                .options("/projects/{id}");
        assertEquals(200, response.getStatusCode());
    }

    // Test to update an existing project with new title and new description using put
    @Test
    void testUpdateProjectPut() {
        Map<String, String> newParams = new HashMap<>();
        newParams.put("title", newProjectTitle);
        newParams.put("description", newProjectDescription);

        Response response = given()
                .pathParam("id", testProjectId)
                .contentType("application/json")
                .body(newParams)
                .when()
                .put("/projects/{id}");

        assertEquals(200, response.getStatusCode());
    }

    // Test to update an existing project with new title and new description using post
    @Test
    void testUpdateProjectPost() {
        Map<String, String> newParams = new HashMap<>();
        newParams.put("title", newProjectTitle);
        newParams.put("description", newProjectDescription);

        Response response = given()
                .pathParam("id", testProjectId)
                .contentType("application/json")
                .body(newParams)
                .when()
                .post("/projects/{id}");

        assertEquals(200, response.getStatusCode());
    }

    // Test to delete an existing project
    @Test
    void testDeleteProject(){
        Response response = given()
                .pathParam("id", testProjectId)
                .when()
                .delete("/projects/{id}");

        assertEquals(200, response.getStatusCode());
        projectDeleted = true;
    }

    // Test failure to get a project with a non-existing id
    @Test
    void testGetProjectFakeId(){
        Response response = given()
                .pathParam("id", fakeProjectId)
                .when()
                .get("/projects/{id}");

        assertEquals(404, response.getStatusCode());
    }

    // Test failure to create a project with string completed in json
    @Test
    void testCreateProjectMalformedPayloadJson() {
        Map<String, String> testParams = new HashMap<>();
        testParams.put("title", projectTitle);
        testParams.put("description", projectDescription);
        testParams.put("completed", "true");

        Response response = given()
                .contentType("application/json")
                .body(testParams)
                .when()
                .post("/projects");

        assertEquals(400, response.getStatusCode());
    }

    // Test failure to create a project with id in xml
    @Test
    void testCreateProjectMalformedPayloadXml(){
        String xmlParams = "<todo>"
                + "<title> " + projectTitle + " </title>"
                + "<description> " + projectDescription + " </description>"
                + "<id> " + "1" + " </id>"
                + "</todo>";

        Response response = given()
                .contentType("application/xml")
                .body(xmlParams)
                .when()
                .post("/projects");

        assertEquals(400, response.getStatusCode());
    }

    // Test failure to patch a project with its id
    @Test
    void testPatchProjectID(){
        Map<String, String> newParams = new HashMap<>();
        newParams.put("title", projectTitle);
        newParams.put("description", projectDescription);

        Response response = given()
                .pathParam("id", testProjectId)
                .contentType("application/json")
                .body(newParams)
                .when()
                .patch("/projects/{id}");

        assertEquals(405, response.getStatusCode());
    }

    // Test to get all the todos related with a project
    @Test
    void testGetAllTodosOfProject(){
        Response response = given()
                .pathParam("id", testProjectId)
                .when()
                .get("/projects/{id}/tasks");
        assertEquals(200, response.getStatusCode());
    }

    // Test to create a relationship between a todo and a project
    @Test
    void testCreateRelationship(){
        Map<String, String> testRelationshipParams = new HashMap<>();
        testRelationshipParams.put("id", String.valueOf(testToDoId));
        Response response = given()
                .contentType("application/json")
                .body(testRelationshipParams)
                .pathParam("id", testProjectId2)
                .when()
                .post("/projects/{id}/tasks");

        assertEquals(201, response.getStatusCode());
    }

    // Test that when updating a project using put its relationships get deleted
    @Test
    void testUpdateProjectPutDeletesRelationships() {
        Map<String, String> newParams = new HashMap<>();
        newParams.put("title", newProjectTitle);
        newParams.put("description", newProjectDescription);

        Response response = given()
                .pathParam("id", testProjectId)
                .contentType("application/json")
                .body(newParams)
                .when()
                .put("/projects/{id}");

        assertEquals(200, response.getStatusCode());

        Response response2 = given()
                .pathParam("id", testProjectId)
                .when()
                .get("/projects/{id}/tasks");

        assertEquals(200, response2.getStatusCode());
        List<?> relationships = response2.jsonPath().getList("projects." + testProjectId + ".tasks");
        assertTrue(relationships == null);
    }

    // Test to create a duplicate relationship between a todo and a project
    @Test
    void testCreateDuplicateRelationship(){
        Map<String, String> testRelationshipParams = new HashMap<>();
        testRelationshipParams.put("id", String.valueOf(testToDoId));
        Response response = given()
                .contentType("application/json")
                .body(testRelationshipParams)
                .pathParam("id", testProjectId)
                .when()
                .post("/projects/{id}/tasks");

        assertEquals(201, response.getStatusCode());
    }

    // Test to get a relationship header
    @Test
    void testGetAllRelationshipHeader(){
        Response response = given()
                .pathParam("id", testProjectId)
                .when()
                .head("/projects/{id}/tasks");
        assertEquals(200, response.getStatusCode());
    }

    // Test to get a relationship options
    @Test
    void testGetAllRelationshipOptions(){
        Response response = given()
                .pathParam("id", testProjectId)
                .when()
                .options("/projects/{id}/tasks");
        assertEquals(200, response.getStatusCode());
    }

}
