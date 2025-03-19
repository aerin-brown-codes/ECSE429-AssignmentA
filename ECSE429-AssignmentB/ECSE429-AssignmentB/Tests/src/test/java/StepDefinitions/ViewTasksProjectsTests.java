package StepDefinitions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;


public class ViewTasksProjectsTests {
    private static final String BASE_URL = "http://localhost:4567/projects/";
    private static Process appUnderTest;
    private static final String JAR_PATH = "/Users/laurenceperreault/Documents/Application_Being_Tested/runTodoManagerRestAPI-1.5.5.jar";
    private int status;
    private String result;

    @Given("the API is alive")
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

    @Given("the database contains the by default objects")
    public void defaultObjects() {
        result = "";
        status = -1;
    }

    @When("the user attempts to view the tasks of project with id {int} via JSON payload")
    public void viewTasksJSON(int projectId) throws MalformedURLException, IOException {
        URL url = new URL(BASE_URL + projectId + "/tasks");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Accept", "application/json");
        con.setRequestMethod("GET");

        status = con.getResponseCode();
        result = readResponse(con);
        con.disconnect();
    }

    @When("the user attempts to view the tasks of project with id {int} via XML payload")
    public void viewTasksXML(int projectId) throws MalformedURLException, IOException {
        URL url = new URL(BASE_URL + projectId + "/tasks");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Accept", "application/xml");
        con.setRequestMethod("GET");

        status = con.getResponseCode();
        result = readResponse(con);
        con.disconnect();
    }

    @Then("the status code 200 is seen")
    public void assertStatusCode() {
        assertEquals(200, status);
    }

    @Then("the tasks of project with id {int} are returned in JSON format")
    public void assertTasksJSON(int projectId) {
        assertEquals(true, result.contains("tasks"));
    }

    @Then("the tasks of project with id {int} are returned in XML format")
    public void assertTasksXML(int projectId) {
        assertEquals(false, result.contains("<tasks>"));
    }

    @Then("some tasks appear.")
    public void assertTasksAppear() {
        assertEquals(true, result.length() > 0);
    }

    private String readResponse(HttpURLConnection con) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder content = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        return content.toString();
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

