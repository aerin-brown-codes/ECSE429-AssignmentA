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

import org.junit.jupiter.api.AfterEach;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class View_Todos {
    private static final String BASE_URL = "http://localhost:4567/todos";
    private static Process appUnderTest;
    private static final String JAR_PATH = "/Users/laurenceperreault/Documents/Application_Being_Tested/runTodoManagerRestAPI-1.5.5.jar";
    private String result;
    private int status;

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
        return;
    }
    
    @When("the user attempts to view the todo with id 1 via JSON payload")
    public void viewJSONTodos() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "/1");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        // act
        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        String result = content.toString();
        con.disconnect();
    }

    @Then("status code 200 is received")
    public void assert200() {
        assertEquals(200, status);
    }

    @Then("the todo with id 1 is returned in JSON format")
    public void assertJsonSuccess() {
        assertEquals("{\"todos\":[{\"id\":\"1\",\"title\":\"scan paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"tasksof\":[{\"id\":\"1\"}],\"categories\":[{\"id\":\"1\"}]}]}", result);
    }

    @When("the user attempts to view the todo with id 1 via XML payload")
    public void viewXMLTodos() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "/1");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/xml");
        con.setRequestProperty("Accept", "application/xml");
        con.setRequestMethod("GET");

        // act
        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        String result = content.toString();
        con.disconnect();
    }    

    @Then("the todo with id 1 is returned in XML format")
    public void assertXMLSuccess() {
        assertEquals("<todos><todo><doneStatus>false</doneStatus><description/><tasksof><id>1</id></tasksof><id>1</id><categories><id>1</id></categories><title>scan paperwork</title></todo></todos>", result);
    }

    @When("the user attempts to view the todo with id 25")
    public void viewNonexistentTodo() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "/25");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        // act
        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getErrorStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        String result = content.toString();
        con.disconnect();
    }

    @Then("status code 404 is received")
    public void assert404() {
        assertEquals(404, status);
    }
    
    @Then("the error message is Could not find any instances with todos/25")
    public void assertError() {
        assertEquals("{\"errorMessages\":[\"Could not find an instance with todos/25\"]}", result);
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
