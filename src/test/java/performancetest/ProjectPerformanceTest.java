package performancetest;

import com.sun.management.OperatingSystemMXBean;
import io.restassured.response.Response;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class ProjectPerformanceTest {

    private static final String URL = "http://localhost:4567";

    private Response createProject(String title, String description, boolean completed) {
        Map<String, Object> testParams = new HashMap<>();
        testParams.put("title", title);
        testParams.put("description", description);
        testParams.put("completed", completed);

        return given()
                .baseUri(URL)
                .contentType("application/json")
                .body(testParams)
                .when()
                .post("/projects");
    }

    private Response updateProject(String newTitle, String newDescription, boolean completed, int id) {
        Map<String, Object> testParams = new HashMap<>();
        testParams.put("title", newTitle);
        testParams.put("description", newDescription);
        testParams.put("completed", completed);

        return given()
                .baseUri(URL)
                .pathParam("id", id)
                .contentType("application/json")
                .body(testParams)
                .when()
                .post("/projects/{id}");
    }

    private Response deleteProject(int id){
        return given()
                .baseUri(URL)
                .pathParam("id", id)
                .when()
                .delete("/projects/{id}");
    }

    private Map<String, Object> getUsage() {
        Map<String, Object> usage = new HashMap<>();

        // Get CPU Usage in percentage
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double cpuUsage = osBean.getProcessCpuLoad() * 100;
        usage.put("CPU Usage", cpuUsage);

        // Get Memory Usage in bytes
        Runtime runtime = Runtime.getRuntime();
        double totalMemory = runtime.totalMemory();
        double freeMemory = runtime.freeMemory();
        double usedMemory = totalMemory - freeMemory;
        usage.put("Memory Usage", usedMemory);

        return usage;
    }

    private void recordCreateAndUpdateUsage(FileWriter writer, int testNr, long createTime, long updateTime, double createCpu, double updateCpu, double createMemory, double updateMemory ) {
        try {
            writer.write( testNr + " , " + createTime + " , " + updateTime + " , " + createCpu + " , " + updateCpu + " , " + createMemory + " , " + updateMemory + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void recordDeleteUsage(FileWriter writer, int testNr, long deleteTime, double deleteCpu,double deleteMemory) {
        try {
            writer.write(testNr + " , " + deleteTime + " , " + deleteCpu + " , " + deleteMemory + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runTest(int n){
        List<Integer> ids = new ArrayList<>();

        try(FileWriter writer = new FileWriter("createAndUpdateUsageProject.csv")) {
            writer.write("Test Number , Time to create object (ns) , Time To update object (ns) , CPU usage to create object (%) , CPU Usage to update time (%) , Memory usage to create object (bytes) , Memory usage to update time (bytes) \n");
            for (int i = 1; i<=n; i++){
                // Create Project object and record usage
                long createStart = System.nanoTime();
                Response createToDoResponse = createProject("Title Nr. " + i, "Description Nr. " + i, false);
                long createEnd = System.nanoTime() - createStart;

                int id = createToDoResponse.jsonPath().getInt("id");
                ids.add(id);
                //sleep
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Map<String,Object> createUsage = getUsage();
                double createCpu = (double) createUsage.get("CPU Usage");
                double createMemory = (double) createUsage.get("Memory Usage");

                // Update Project object and record usage
                long updateStart = System.nanoTime();
                updateProject("New Title Nr. " + i, "New Description Nr. " + i, true, id);
                long updateEnd = System.nanoTime() - updateStart;

                Map<String,Object> updateUsage = getUsage();
                double updateCpu = (double) updateUsage.get("CPU Usage");
                double updateMemory = (double) updateUsage.get("Memory Usage");

                // Write Usage to file
                recordCreateAndUpdateUsage(writer, i, createEnd, updateEnd, createCpu, updateCpu, createMemory, updateMemory);
                //sleep
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        try(FileWriter writer = new FileWriter("DeleteUsageProject.csv")) {
            writer.write("Test Number , Time to delete object (ns) , CPU usage to delete object (%) , Memory usage to delete object (bytes)\n");
            for (int z = 0; z < ids.size(); z++){
                // Delete Project object and record usage
                long deleteStart = System.nanoTime();
                deleteProject(ids.get(z));
                long deleteEnd = System.nanoTime();

                Map<String,Object> deleteUsage = getUsage();
                double deleteCpu = (double) deleteUsage.get("CPU Usage");
                double deleteMemory = (double) deleteUsage.get("Memory Usage");

                // Write Usage to file
                recordDeleteUsage(writer, z + 1 , deleteEnd, deleteCpu, deleteMemory);
                //sleep
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        ProjectPerformanceTest test = new ProjectPerformanceTest();
        int nrTests = 100000;
        test.runTest(nrTests);
    }

}
