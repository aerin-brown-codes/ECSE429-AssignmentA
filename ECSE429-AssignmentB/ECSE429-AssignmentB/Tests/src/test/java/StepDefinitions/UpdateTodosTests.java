package StepDefinitions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.io.*;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.After;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

public class UpdateTodosTests {
    private static final String BASE_URL = "http://localhost:4567/todos";
    private static Process appUnderTest;
    private static final String JAR_PATH = "/Users/laurenceperreault/Documents/Application_Being_Tested/runTodoManagerRestAPI-1.5.5.jar";
    private String result;
    private int status;

    @Given("the API is ok")
    public void setup() throws IOException, InterruptedException {
        appUnderTest = new ProcessBuilder("java", "-jar", JAR_PATH)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start();

        if (!waitForApi(BASE_URL, 60)) {
            throw new IllegalStateException("API did not start within timeout.");
        }
    }
    
    private static boolean waitForApi(String healthUrl, int timeoutSeconds) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeoutSeconds * 1000L) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(healthUrl).openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(2000);
                connection.setReadTimeout(2000);
                if (connection.getResponseCode() == 200) {
                    return true;
                }
            } catch (Exception ignored) {
                // API not ready yet
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return false;
    }

    @Given("the database has only the default objects")
    public void defaultObjects() {
        result = "";
        status = -1;
    }
    
    @When("the user updates the todo with id 1 to have title wash dishes, description run dishwasher, and done status false via POST")
    public void updatePostTodos() throws MalformedURLException, IOException {
        URL url = new URL(BASE_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        String jsonInputString = "{\"title\":\"wash dishes\",\"doneStatus\":false,\"description\":\"run dishwasher\"}";
        
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        status = con.getResponseCode();
        StringBuilder content = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
        }
        result = content.toString();
        con.disconnect();
    }

    @When("the user sends a PUT request for the todo with id {int} with no body")
    public void the_user_sends_a_put_request_for_the_todo_with_id_with_no_body(Integer id) throws IOException {
        URL url = new URL(BASE_URL + "/" + id);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("PUT");
        con.setDoOutput(true);

        // Send a JSON object with null values instead of empty strings
        String jsonInputString = "{\"title\":\"\"}"; // Still a valid JSON, but an empty title

        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        status = con.getResponseCode();
        StringBuilder content = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
        }
        result = content.toString();
        con.disconnect();
    }

    @Then("status code 201 is readable")
    public void assert201() {
        assertEquals(201, status);
    }

    @Then("status code 200 is readable")
    public void assert200() {
        assertEquals(200, status);
    }

    @Then("a todo object is received with id {int} title wash dishes, description run dishwasher, and done status false")
    public void assertJsonSuccess(Integer id) {
        assertEquals("{\"id\":\"" + id + "\",\"title\":\"wash dishes\",\"doneStatus\":\"false\",\"description\":\"run dishwasher\"}", result);
    }

    @Then("a todo object is received with id {int}, title wash dishes, description run dishwasher, and done status false")
    public void a_todo_object_is_received_with_id_title_wash_dishes_description_run_dishwasher_and_done_status_false(Integer id) {
        String expectedJson = "{\"id\":\"" + id + "\",\"title\":\"wash dishes\",\"doneStatus\":\"false\",\"description\":\"run dishwasher\"}";
        assertEquals(expectedJson, result);
    }

    @When("the user updates the todo with id 1 to have title wash dishes, description run dishwasher, and done status false via PUT")
    public void updatePutTodos() throws MalformedURLException, IOException {
        URL url = new URL(BASE_URL + "/1");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("PUT");
        con.setDoOutput(true);
        String jsonInputString = "{\"title\":\"wash dishes\",\"doneStatus\":false,\"description\":\"run dishwasher\"}";
        
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        status = con.getResponseCode();
        StringBuilder content = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
        }
        result = content.toString();
        con.disconnect();
    }

    @Then("status code 400 is returned for updating todo")
    public void assert400() {
        System.out.println("Status Code: " + status);
        assertEquals(400, status);
    }
    
    @Then("the message is title: field is mandatory")
    public void assertError() {
        System.out.println("Response: " + result);
        assertEquals("{\"errorMessages\":[\"title:field is mandatory\"]}", result);
    }

    @After
    public void teardown() {
        if (appUnderTest != null) {
            appUnderTest.destroy();
            try {
                appUnderTest.waitFor(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
