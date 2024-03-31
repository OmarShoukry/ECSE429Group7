package performancetest;

import io.restassured.response.Response;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class ToDoPerformanceTest {

    private static final String URL = "http://localhost:4567";

    private Response createToDo(String title, String description) {
        Map<String, String> testParams = new HashMap<>();
        testParams.put("title", title);
        testParams.put("description", description);

        return given()
                .baseUri(URL)
                .contentType("application/json")
                .body(testParams)
                .when()
                .post("/todos");
    }

    private Response updateToDo(String newTitle, String newDescription, int id) {
        Map<String, String> testParams = new HashMap<>();
        testParams.put("title", newTitle);
        testParams.put("description", newDescription);

        return given()
                .baseUri(URL)
                .pathParam("id", id)
                .contentType("application/json")
                .body(testParams)
                .when()
                .post("/todos/{id}");
    }

    private Response deleteToDo(int id){
        return given()
                .baseUri(URL)
                .pathParam("id", id)
                .when()
                .delete("/todos/{id}");
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

        try(FileWriter writer = new FileWriter("createAndUpdateUsageToDo.csv")) {
            writer.write("Test Number , Time to create object (ns) , Time To update object (ns) , CPU usage to create object (%) , CPU Usage to update time (%) , Memory usage to create object (bytes) , Memory usage to update time (bytes)\n");
            for (int i = 1; i<=n; i++){
                // Create ToDo object and record usage
                long createStart = System.nanoTime();
                Response createToDoResponse = createToDo("Title Nr. " + i, "Description Nr. " + i);
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

                // Update ToDo object and record usage
                long updateStart = System.nanoTime();
                updateToDo("New Title Nr. " + i, "New Description Nr. " + i, id);
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


        try(FileWriter writer = new FileWriter("DeleteUsageToDo.csv")) {
            writer.write("Test Number , Time to delete object (ns) , CPU usage to delete object (%) , Memory usage to delete object (bytes)\n");
            for (int z = 0; z < ids.size(); z++){
                // Delete ToDo object and record usage
                long deleteStart = System.nanoTime();
                deleteToDo(ids.get(z));
                long deleteEnd = System.nanoTime();

                Map<String,Object> deleteUsage = getUsage();
                double deleteCpu = (double) deleteUsage.get("CPU Usage");
                double deleteMemory = (double) deleteUsage.get("Memory Usage");

                // Write Usage to file
                recordDeleteUsage(writer, z, deleteEnd, deleteCpu, deleteMemory);
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
        ToDoPerformanceTest test = new ToDoPerformanceTest();
        int nrTests = 1000;
        long totalStart = System.nanoTime();
        test.runTest(nrTests);
        long totalEnd = System.nanoTime();

        double totalTime = (totalEnd - totalStart) / 1_000_000_000.0;
        System.out.println("Total time to create, update and delete " + nrTests + " objects is " + totalTime + " seconds.");
    }

}
