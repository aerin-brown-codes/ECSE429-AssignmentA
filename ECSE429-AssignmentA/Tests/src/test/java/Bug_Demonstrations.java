import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;


import static org.junit.jupiter.api.Assertions.assertEquals;

public class Bug_Demonstrations {
    private static final String BASE_URL = "http://localhost:4567/";
    private static Process appUnderTest;
    private static final String JAR_PATH = "/Users/laurenceperreault/Documents/Application_Being_Tested/runTodoManagerRestAPI-1.5.5.jar";

    @BeforeEach
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

    @Test
    public void testTaskProjectJSONExpected() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "todos/1/tasksof");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        String jsonInputString = "{\"id\":1}";
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

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

        // assert
        assertEquals(404, status);
        assertEquals("{\"errorMessages\":[\"Could not find thing matching value for id\"]}", result);
    }

    @Test
    public void testTaskCategoryJSONExpected() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "todos/1/categories");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        String jsonInputString = "{\"id\":1}";
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

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

        // assert
        assertEquals(404, status);
        assertEquals("{\"errorMessages\":[\"Could not find thing matching value for id\"]}", result);
    }

    @Test
    public void testProjectTaskJSONExpected() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "projects/1/tasks");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        String jsonInputString = "{\"id\":1}";
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

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

        // assert
        assertEquals(404, status);
        assertEquals("{\"errorMessages\":[\"Could not find thing matching value for id\"]}", result);
    }

    @Test
    public void testTaskProjectJSONUnexpected() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "todos/1/tasksof");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        String jsonInputString = "{\"id\":\"1\"}";
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

        // assert
        assertEquals(201, status);
    }

    @Test
    public void testTaskCategoryJSONUnexpected() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "todos/1/categories");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        String jsonInputString = "{\"id\":\"1\"}";
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

        // assert
        assertEquals(201, status);
    }

    @Test
    public void testProjectTaskJSONUnexpected() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "projects/1/tasks");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        String jsonInputString = "{\"id\":\"1\"}";
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

        // assert
        assertEquals(201, status);
    }

    @Test
    public void testTaskProjectXMLExpected() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "todos/1/tasksof");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/xml");
        con.setRequestProperty("Accept", "application/xml");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        String jsonInputString = "<project><id>1</id></project>";
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

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

        // assert
        assertEquals(404, status);
        assertEquals("<errorMessages><errorMessage>Could not find thing matching value for id</errorMessage></errorMessages>", result);
    }

    @Test
    public void testTaskCategoryXMLExpected() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "todos/1/categories");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/xml");
        con.setRequestProperty("Accept", "application/xml");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        String jsonInputString = "<project><id>1</id></project>";
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

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

        // assert
        assertEquals(404, status);
        assertEquals("<errorMessages><errorMessage>Could not find thing matching value for id</errorMessage></errorMessages>", result);
    }

    @Test
    public void testProjectTaskXMLExpected() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "projects/1/tasks");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/xml");
        con.setRequestProperty("Accept", "application/xml");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        String jsonInputString = "<project><id>1</id></project>";
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

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

        // assert
        assertEquals(404, status);
        assertEquals("<errorMessages><errorMessage>Could not find thing matching value for id</errorMessage></errorMessages>", result);
    }
}
