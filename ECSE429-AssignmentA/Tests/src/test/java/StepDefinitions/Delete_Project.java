package StepDefinitions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.AfterEach;

public class Delete_Project {
    private static final String BASE_URL = "http://localhost:4567/projects/";
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

    @Given("the database contains only the default objects")
    public void defaultObjects() {
        result = "";
        status = -1;
    }

    @When("the user attempts to delete the project with id {int} with a JSON payload")
    public void deleteProjectJSON(int projectId) throws MalformedURLException, IOException {
        URL url = new URL(BASE_URL + projectId);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("DELETE");

        // Act
        status = con.getResponseCode();
        con.disconnect();
    }

    @When("the user attempts to delete the project with id {int} with a XML payload")
    public void deleteProjectXML(int projectId) throws MalformedURLException, IOException {
        URL url = new URL(BASE_URL + projectId);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/xml");
        con.setRequestMethod("DELETE");

        // Act
        status = con.getResponseCode();
        con.disconnect();
    }

    @Then("status code {int} is received")
    public void assertStatusCode(int expectedStatus) {
        assertEquals(expectedStatus, status);
    }

    @Then("no project with id {int} exists")
    public void assertProjectDoesNotExist(int projectId) throws MalformedURLException, IOException {
        URL url = new URL(BASE_URL + projectId);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        status = con.getResponseCode();
        con.disconnect();

        assertEquals(404, status);
    }

    @Then("the error message is \"{string}\"")
    public void assertErrorMessage(String expectedMessage) throws IOException {
        URL url = new URL(BASE_URL + "25");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder content = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();

        result = content.toString();
        assertEquals(expectedMessage, result);
    }

    @AfterEach
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
