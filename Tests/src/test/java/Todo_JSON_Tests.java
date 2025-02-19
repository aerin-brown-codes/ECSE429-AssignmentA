import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertEquals;
public class Todo_JSON_Tests {
    private static final String BASE_URL = "http://localhost:4567/todos";
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
    public void getAllTodos() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL);
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

        // assert
        assertEquals(200, status);
        assertEquals("{\"todos\":[{\"id\":\"1\",\"title\":\"scan paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"categories\":[{\"id\":\"1\"}],\"tasksof\":[{\"id\":\"1\"}]},{\"id\":\"2\",\"title\":\"file paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"tasksof\":[{\"id\":\"1\"}]}]}", result);
    }

    @Test
    public void getTodo() throws MalformedURLException, IOException {
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

        // assert
        assertEquals(200, status);
        assertEquals("{\"todos\":[{\"id\":\"1\",\"title\":\"scan paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"tasksof\":[{\"id\":\"1\"}],\"categories\":[{\"id\":\"1\"}]}]}", result);
    }

    @Test
    public void getTodoProjects() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "/1/tasksof");
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

        // assert
        assertEquals(200, status);
        assertEquals("{\"projects\":[{\"id\":\"1\",\"title\":\"Office Work\",\"completed\":\"false\",\"active\":\"false\",\"description\":\"\",\"tasks\":[{\"id\":\"1\"},{\"id\":\"2\"}]}]}", result);
    }

    @Test
    public void getTodoCategories() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "/1/categories");
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

        // assert
        assertEquals(200, status);
        assertEquals("{\"categories\":[{\"id\":\"1\",\"title\":\"Office\",\"description\":\"\"}]}", result);
    }


    @Test
    public void testCreateTodo() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        String jsonInputString = "{\"title\":\"test\",\"doneStatus\":false,\"description\":\"test\"}";
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
        assertEquals("{\"id\":\"3\",\"title\":\"test\",\"doneStatus\":\"false\",\"description\":\"test\"}", result);
    }

    @Test
    public void testUpdateTodoPost() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "/1");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        String jsonInputString = "{\"title\":\"test\",\"doneStatus\":true,\"description\":\"test\"}";
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
        assertEquals(200, status);
        assertEquals("{\"todos\":[{\"id\":\"1\",\"title\":\"test\",\"doneStatus\":\"true\",\"description\":\"test\",\"tasksof\":[{\"id\":\"1\"}],\"categories\":[{\"id\":\"1\"}]}]}", result);
    }

    @Test
    public void testUpdateTodoPut() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "/1");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        String jsonInputString = "{\"title\":\"test\",\"doneStatus\":true,\"description\":\"test\"}";
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
        assertEquals(200, status);
        assertEquals("{\"id\":\"1\",\"title\":\"test\",\"doneStatus\":\"true\",\"description\":\"test\"}", result);
    }

    @Test
    public void testDeleteTodo() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "/1");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("DELETE");

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
    public void testDeleteTodoProjectRelationship() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "/1/tasksof/1");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("DELETE");

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
    public void testDeleteTodoCategoryRelationship() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "/1/categories/1");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("DELETE");

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
    public void testHeadAllTodos() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
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
    public void testHeadTodo() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "/1");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
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
    public void testHeadTodoProjects() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "/1/tasksof");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
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
    public void testHeadTodoCategories() throws MalformedURLException, IOException {
        // arrange
        URL url = new URL(BASE_URL + "/1/categories");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
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
}
