import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Project_JSON_Failure_Behaviour {
    private static final String BASE_URL = "http://localhost:4567/projects";
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
    public void getProject() throws MalformedURLException, IOException {
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
        assertEquals("{\"errorMessages\":[\"Could not find an instance with projects/25\"]}", result);
    }

    @Test
    public void getProjectTasks() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "/25/tasks");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        // act
        int status = con.getResponseCode();
        InputStream errorStream = con.getErrorStream();
        BufferedReader in = (errorStream != null) ? new BufferedReader(new InputStreamReader(errorStream)) : null;
        StringBuilder content = new StringBuilder();
        if (in != null) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
        } else {
            System.out.println("No error response received.");
        }

        String result = content.toString();
        System.out.println("Response: " + result);
        con.disconnect();

        // assert
        assertEquals(200, status);
        if (!result.isEmpty()) {
            assertEquals("{\"errorMessages\":[\"Could not find an instance with projects/25/tasks\"]}", result);
        }
    }

    @Test
    public void testDeleteProjectTask() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "/25/tasks/25");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("DELETE");
        con.setDoOutput(true);
        String jsonInputString = "";
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // act
        int status = con.getResponseCode();
        InputStream errorStream = con.getErrorStream();
        BufferedReader in = (errorStream != null) ? new BufferedReader(new InputStreamReader(errorStream)) : null;

        StringBuilder content = new StringBuilder();
        if (in != null) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
        } else {
            System.out.println("No error response received.");
        }

        String result = content.toString();
        System.out.println("Response: " + result);
        con.disconnect();

        // assert
        assertEquals(404, status);
        if (!result.isEmpty()) {
            assertEquals("{\"errorMessages\":[\"Could not find any instances with projects/25/tasks/25\"]}", result);
        }
    }

    @Test
    public void testDeleteProject() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "/1");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("DELETE");
        con.setDoOutput(true);
        String jsonInputString = "";
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // act
        int status = con.getResponseCode();
        InputStream errorStream = con.getErrorStream();
        BufferedReader in = (errorStream != null) ? new BufferedReader(new InputStreamReader(errorStream)) : null;
        StringBuilder content = new StringBuilder();
        if (in != null) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
        } else {
            System.out.println("No error response received.");
        }

        String result = content.toString();
        System.out.println("Response: " + result);
        con.disconnect();

        // assert
        assertEquals(200, status);
        assertEquals("", result);
    }
}