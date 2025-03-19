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

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.After;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;


public class ViewMultipleTodosTests {
    private static final String BASE_URL = "http://localhost:4567/todos";
    private static Process appUnderTest;
    private static final String JAR_PATH = "/Users/laurenceperreault/Documents/Application_Being_Tested/runTodoManagerRestAPI-1.5.5.jar";
    private String result;
    private int status;

    @Given("the API is ready to go")
    public void setupMultipleTodos() throws IOException, InterruptedException {
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

    @Given("the database contains the initial objects")
    public void defaultObjects() {
        result = "";
        status = -1;
        return;
    }
    
    @When("the user attempts to view all todos")
    public void viewAllTodos() throws MalformedURLException, IOException {
        URL url = new URL(BASE_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        this.status = con.getResponseCode(); // Store status in class field

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder content = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        this.result = content.toString(); // Store result in class field
        con.disconnect();
    }

    @Then("status code 200 is demonstrated")
    public void assert200() {
        assertEquals(200, status);
    }

    @Then("the todos with ids 1 and 2 are returned")
    public void assertJsonSuccess() throws JSONException{
        JSONAssert.assertEquals("{\n" + //
                        "  \"todos\": [\n" + //
                        "    {\"id\": \"1\", \"title\": \"scan paperwork\", \"doneStatus\": \"false\", \"description\": \"\", \"tasksof\": [{\"id\": \"1\"}], \"categories\": [{\"id\": \"1\"}]},\n" + //
                        "    {\"id\": \"2\", \"title\": \"file paperwork\", \"doneStatus\": \"false\", \"description\": \"\", \"tasksof\": [{\"id\": \"1\"}]}\n" + //
                        "  ]\n" + //
                        "}", result, JSONCompareMode.NON_EXTENSIBLE);
    }

    @When("the user attempts to view only todos with title of value file paperwork")
    public void viewFilteredTodos() throws MalformedURLException, IOException {
        URL url = new URL(BASE_URL + "?title=file%20paperwork");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        this.status = con.getResponseCode();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder content = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        this.result = content.toString();
        con.disconnect();
    }    

    @Then("the todo with id 2 is returned")
    public void assertFilterSuccess() throws JSONException {
        JSONAssert.assertEquals("{\n" + //
                        "  \"todos\": [\n" + //
                        "    {\"id\": \"2\", \"title\": \"file paperwork\", \"doneStatus\": \"false\", \"description\": \"\", \"tasksof\": [{\"id\": \"1\"}]}\n" + //
                        "  ]\n" + //
                        "}", result, JSONCompareMode.NON_EXTENSIBLE);
    }

    @When("the user attempts to view only todos with blah of value blah")
    public void invalidFilter() throws MalformedURLException, IOException {
        URL url = new URL(BASE_URL + "?blah=blah");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        this.status = con.getResponseCode();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder content = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        this.result = content.toString();
        con.disconnect();
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

