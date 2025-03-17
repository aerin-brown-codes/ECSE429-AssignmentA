package StepDefinitions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CreateProjectTest {
    private static final String BASE_URL = "http://localhost:4567/projects";
    private static Process appUnderTest;
    private static final String JAR_PATH = "/Users/laurenceperreault/Documents/Application_Being_Tested/runTodoManagerRestAPI-1.5.5.jar";
    private int status;
    private String result;

    @BeforeEach
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

    @Test
    public void testCreateProjectJSON() throws IOException {
        String jsonPayload = "{\"title\":\"House work\",\"description\":\"Household chores\"}";
        sendRequest(jsonPayload, "application/json");
        assertEquals(201, status);
    }

    @Test
    public void testCreateProjectXML() throws IOException {
        String xmlPayload = "<project><title>House work</title><description>Household chores</description></project>";
        sendRequest(xmlPayload, "application/xml");
        assertEquals(201, status);
    }

    @Test
    public void testCreateProjectWithWrongFormat() throws IOException {
        String xmlPayload = "<project><title>House work</title><description>Household chores</description></project>";
        sendRequest(xmlPayload, "application/json");
        assertEquals(400, status);
    }

    private void sendRequest(String payload, String contentType) throws IOException {
        URL url = new URL(BASE_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", contentType);
        con.setDoOutput(true);

        try (OutputStream os = con.getOutputStream()) {
            os.write(payload.getBytes());
            os.flush();
        }

        status = con.getResponseCode();
        con.disconnect();
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
