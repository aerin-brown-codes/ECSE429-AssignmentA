package StepDefinitions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.After;

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

        if (!waitForApi("http://localhost:4567/projects", 60)) {
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

    @When("the user creates a new project with title {string} and description {string} in JSON format.")
    public void createProjectJSON(String title, String description) throws IOException {
        String jsonPayload = String.format("{\"title\":\"%s\",\"description\":\"%s\"}", title, description);
        sendRequest(jsonPayload, "application/json");
    }

    @When("the user creates a new project with title {string} and description {string} in XML format.")
    public void createProjectXML(String title, String description) throws IOException {
        String xmlPayload = String.format("<project><title>%s</title><description>%s</description></project>", title, description);
        sendRequest(xmlPayload, "application/xml");
    }

    private void sendRequest(String payload, String contentType) throws IOException {
        URL url = new URL(BASE_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", contentType);
        con.setDoOutput(true);

        try (OutputStream os = con.getOutputStream()) {
            os.write(payload.getBytes());
            os.flush();
        }

        status = con.getResponseCode();
        con.disconnect();
    }

    @Then("status code {int} is received")
    public void assertStatusCode(int expectedStatus) {
        assertEquals(expectedStatus, status);
    }

    @Then("a new project is received with title {string}, description {string}, id {int}, completed {string} and active {string} in {string} format.")
    public void assertProjectDetails(String title, String description, int id, String completed, String active, String format) {
        URL url = new URL(BASE_URL + "/" + id); // Assuming you can get the project by ID
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        status = con.getResponseCode();
        assertEquals(200, status);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode projectNode = mapper.readTree(content.toString());

        assertEquals(title, projectNode.get("title").asText());
        assertEquals(description, projectNode.get("description").asText());
        assertEquals(id, projectNode.get("id").asInt());
        assertEquals(Boolean.parseBoolean(completed), projectNode.get("completed").asText());
        assertEquals(Boolean.parseBoolean(active), projectNode.get("active").asText());
    }

    @Then("the error message is {string}")
    public void assertErrorMessage(String expectedMessage) {
        assertEquals(expectedMessage, result);
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
