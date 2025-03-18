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
import java.io.*;



import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.After;

public class UpdateTodosTests {
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
    
    @When("the user updates the todo with id 1 to have title wash dishes, description run dishwasher, and done status false via POST")
    public void updatePostTodos() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        String jsonInputString = "{\"title\":\"wash dishes\",\"doneStatus\":false,\"description\":\"run dishwasher\"}";
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

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

    @Then("status code 201 is received")
    public void assert201() {
        assertEquals(201, status);
    }

    @Then("a todo object is received with title wash dishes, description run dishwasher, and done status false")
    public void assertJsonSuccess() {
        assertEquals("{\"id\":\"3\",\"title\":\"wash dishes\",\"doneStatus\":\"false\",\"description\":\"run dishwasher\"}", result);
    }

    @When("the user updates the todo with id 1 to have title wash dishes, description run dishwasher, and done status false via PUT")
    public void updatePutTodos() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "/1");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        String jsonInputString = "<todo><title>wash dishes</title><doneStatus>false</doneStatus><description>run dishwasher</description></todo>";
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

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

    @When("the user sends a PUT request for the todo with id 1 with no body")
    public void emptyPost() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "/1");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("PUT");
        con.setDoOutput(true);
        String jsonInputString = "{}";
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // act
        int status = con.getResponseCode();
        InputStream errorStream = con.getErrorStream();
        if (errorStream != null) {
            BufferedReader in = new BufferedReader(new InputStreamReader(errorStream));
        } else {
            assertEquals(true, false);
        }
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

    @Then("status code 400 is received")
    public void assert400() {
        assertEquals(400, status);
    }
    
    @Then("the error message is title: field is mandatory")
    public void assertError() {
        assertEquals("{\"errorMessages\":[\"title : field is mandatory\"]}", result);
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
