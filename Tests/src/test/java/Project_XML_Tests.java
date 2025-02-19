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

public class Project_XML_Tests {
    private static final String BASE_URL = "http://localhost:4567/projects";
    private static Process appUnderTest;
    private static final String JAR_PATH = "C:\\Users\\abrown108\\Downloads\\Application_Being_Tested\\Application_Being_Tested\\runTodoManagerRestAPI-1.5.5.jar";

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
    public void getAllProjects() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL);
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

        // assert
        assertEquals(200, status);
        assertEquals("{\"projects\":[{\"id\":\"1\",\"title\":\"Office Work\",\"completed\":\"false\",\"active\":\"false\",\"description\":\"\",\"tasks\":[{\"id\":\"2\"},{\"id\":\"1\"}]}]}", result);
    }

    @Test
    public void getProject() throws MalformedURLException, IOException {
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

        // assert
        assertEquals(200, status);
        assertEquals("{\"projects\":[{\"id\":\"1\",\"title\":\"Office Work\",\"completed\":\"false\",\"active\":\"false\",\"description\":\"\",\"tasks\":[{\"id\":\"2\"},{\"id\":\"1\"}]}]}", result);
    }

    @Test
    public void getProjectTasks() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "/1/tasks");
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

        // assert
        assertEquals(200, status);
        assertEquals("{\"todos\":[{\"id\":\"2\",\"title\":\"file paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"tasksof\":[{\"id\":\"1\"}]},{\"id\":\"1\",\"title\":\"scan paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"tasksof\":[{\"id\":\"1\"}],\"categories\":[{\"id\":\"1\"}]}]}", result);
    }

    @Test
    public void testCreateProject() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/xml");
        con.setRequestProperty("Accept", "application/xml");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        String xmlInputString = "<project><title>test</title><completed>false</completed><active>false</active><description>test</description></project>";
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = xmlInputString.getBytes("utf-8");
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
        assertEquals("<project><active>false</active><description>test</description><id>1</id><completed>false</completed><title>test</title><tasks><id>2</id></tasks></project>", result);
    }

    @Test
    public void testUpdateProjectPost() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "/1");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/xml");
        con.setRequestProperty("Accept", "application/xml");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        String xmlInputString = "<project><title>test</title><completed>false</completed><active>false</active><description>test</description></project>";
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = xmlInputString.getBytes("utf-8");
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
        assertEquals("<project><active>false</active><description>test</description><id>2</id><completed>false</completed><title>test</title></project>", result);
    }

    @Test
    public void testUpdateProjectPut() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "/1");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/xml");
        con.setRequestProperty("Accept", "application/xml");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        String xmlInputString = "<project><title>test</title><completed>false</completed><active>false</active><description>test</description></project>";
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = xmlInputString.getBytes("utf-8");
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
        assertEquals("<project><active>false</active><description>test</description><id>2</id><completed>false</completed><title>test</title></project>", result);
    }

    @Test
    public void getAllProjectsHead() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/xml");
        con.setRequestProperty("Accept", "application/xml");
        con.setRequestMethod("HEAD");

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
        assertEquals(200, status);
    }

    @Test
    public void testDeleteProjectTask() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "/1/tasks/1");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/xml");
        con.setRequestProperty("Accept", "application/xml");
        con.setRequestMethod("DELETE");
        con.setDoOutput(true);
        String xmlInputString = "";
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = xmlInputString.getBytes("utf-8");
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
        assertEquals(200, status);
    }

    @Test
    public void testDeleteProject() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "/1");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/xml");
        con.setRequestProperty("Accept", "application/xml");
        con.setRequestMethod("DELETE");
        con.setDoOutput(true);
        String xmlInputString = "";
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = xmlInputString.getBytes("utf-8");
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
        assertEquals(200, status);
    }
}
