package StepDefinitions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.description;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.After;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

public class CreateProjectTests {
    private static final String BASE_URL = "http://localhost:4567/projects";
    private static Process appUnderTest;
    private static final String JAR_PATH = "/Users/laurenceperreault/Documents/Application_Being_Tested/runTodoManagerRestAPI-1.5.5.jar";
    private int status;
    private String result;

    @Given("the API is responsive")
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

    @Given("the database contains the default objects")
    public void defaultObjects() {
        result = "";
        status = -1;
    }

    @When("the user creates a new project with title {string} and description {string} in {string} format.")
    public void the_user_creates_a_new_project_with_title_and_description_in_format(String title, String description, String format) throws IOException {
        if (format.equalsIgnoreCase("JSON")) {
            createProjectJSON(title, description);
        } else if (format.equalsIgnoreCase("XML")) {
            createProjectXML(title, description);
        } else {
            throw new IllegalArgumentException("Unsupported format: " + format);
        }
    }

    private void createProjectJSON(String title, String description) throws IOException {
        String jsonPayload = String.format("{\"title\":\"%s\",\"description\":\"%s\"}", title, description);
        sendRequest(jsonPayload, "application/json");
    }

    private void createProjectXML(String title, String description) throws IOException {
        String xmlPayload = String.format("<project><title>%s</title><description>%s</description></project>", title, description);
        sendRequest(xmlPayload, "application/xml");
    }

    private void sendRequest(String payload, String contentType) throws IOException {
        URL url = new URL(BASE_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", contentType);
        con.setDoOutput(true);

        System.out.println("\nSending Request:");
        System.out.println("Payload: " + payload);
        System.out.println("Content-Type: " + contentType);

        try (OutputStream os = con.getOutputStream()) {
            os.write(payload.getBytes());
            os.flush();
        }

        status = con.getResponseCode();

        // Capture response body for debugging
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            // Handle error response
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
        }

        result = response.toString();
        System.out.println("Raw Response: " + result);
        con.disconnect();
    }

    @Then("status code 201 is visible")
    public void assertStatusCode() {
        assertEquals(201, status, "Expected status 201, but got " + status);
    }

    @Then("a new project is received with title {string}, description {string}, id {int}, completed {string} and active {string} in {string} format.")
    public void assertProjectDetails(String title, String description, int id, String completed, String active, String format) throws IOException {
        URL url = new URL(BASE_URL + "/" + id);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        status = con.getResponseCode();
        assertEquals(200, status, "Expected status 200 when retrieving project, but got " + status);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder content = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();

        System.out.println("\nProject Response:");
        System.out.println(content.toString());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode projectNode = mapper.readTree(content.toString());

        // Print parsed JSON for debugging
        System.out.println("Parsed JSON Response: " + projectNode.toPrettyString());

        // Extract first project from the "projects" array
        JsonNode firstProject = projectNode.get("projects").get(0);  

        // Assertions
        assertEquals(title, firstProject.has("title") ? firstProject.get("title").asText() : null, "Title mismatch");
        assertEquals(description, firstProject.has("description") ? firstProject.get("description").asText() : null, "Description mismatch");
        assertEquals(id, firstProject.has("id") ? firstProject.get("id").asInt() : -1, "ID mismatch");
        assertEquals(completed, firstProject.has("completed") ? firstProject.get("completed").asText() : "false", "Completed field mismatch");
        assertEquals(active, firstProject.has("active") ? firstProject.get("active").asText() : "false", "Active field mismatch");
    }

    @Then("status code 400 is put on screen")
    public void assertStatusCodeReceived() {
        assertEquals(201, status, "Expected status 400, but got " + status);
    }

    @Then("the error message is \"\"java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 1 path $\"\"")
    public void assertErrorMessage() {
        System.out.println("Actual error message: " + result);
        assertEquals("", result, "Error message mismatch");
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
