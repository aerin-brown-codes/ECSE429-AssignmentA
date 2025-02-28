import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
public class Todo_JSON_Failure_Behaviour {
    private static final String BASE_URL = "http://localhost:4567/todos";
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
    public void getTodoFailure() throws MalformedURLException, IOException {
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

        // assert
        assertEquals(404, status);
        assertEquals("{\"errorMessages\":[\"Could not find an instance with todos/25\"]}", result);
    }

    @Test
    public void testCreateTodo() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        String jsonInputString = "{}";
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
        assertEquals(400, status);
        assertEquals("{\"errorMessages\":[\"title : field is mandatory\"]}", result);
    }

    @Test
    public void testUpdateTodoPut() throws MalformedURLException, IOException {
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
            fail("No error stream returned from API");
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

        // assert
        assertEquals(400, status);
        assertEquals("{\"errorMessages\":[\"title : field is mandatory\"]}", result);
    }

    @Test
    public void testDeleteTodo() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "/25");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("DELETE");

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
        assertEquals("{\"errorMessages\":[\"Could not find any instances with todos/25\"]}", result);

    }

    @Test
    public void testDeleteTodoProjectRelationship() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "/25/tasksof/25");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("DELETE");

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
        assertEquals("{\"errorMessages\":[\"Could not find any instances with todos/25/tasksof/25\"]}", result);

    }

    @Test
    public void testDeleteTodoCategoryRelationship() throws MalformedURLException, IOException {
        URL url = new URL(BASE_URL + "/25/categories/25");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("DELETE");

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
        assertEquals("{\"errorMessages\":[\"Could not find any instances with todos/25/categories/25\"]}", result);

    }
}
