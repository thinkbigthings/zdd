package org.thinkbigthings.zdd.perf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class LoadTester {


    private HttpClient client;
    private Duration duration = Duration.of(60, ChronoUnit.SECONDS);

    private URI users = URI.create("https://localhost:8080/user");
    private URI info = URI.create("https://localhost:8080/actuator/info");
    private URI health = URI.create("https://localhost:8080/actuator/health");

    public LoadTester() {

        try {
            // clients are immutable and thread safe
            // don't check certificates (so can use self-signed) and don't verify hostname
            SSLContext sc = SSLContext.getInstance("TLSv1.3");
            sc.init(null, new TrustManager[]{new InsecureTrustManager()}, new SecureRandom());
            System.setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());

            client = HttpClient.newBuilder()
                    .sslContext(sc)
                    .build();
        }
        catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() throws Exception {

        System.out.println("Running test for " + duration);

        Instant end = Instant.now().plus(duration);

        int poolSize = 10;
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(poolSize);
        while(Instant.now().isBefore(end)) {
            while(executor.getQueue().size() < poolSize) {
                executor.submit(() -> doCRUD());
            }
            sleepMillis(10);
        }
        executor.shutdown();

        System.out.println("Users summary: " + get(info));
        System.out.println("6 calls per user");
    }


    private void sleepMillis(int millis) {
        try {
            Thread.sleep(millis);
        }
        catch(InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void doCRUD() {

        UserDTO user = userSupplier(UUID.randomUUID().toString());
        URI userUrl = URI.create(users.toString() + "/" + user.username);

        try {
            post(users, user);

            get(userUrl);

            user.displayName = user.displayName+"-updated";
            put(userUrl, user);

            get(userUrl);

            get(info);

            get(health);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }

    }

    private UserDTO userSupplier(String suffix) {
        String name = "user" + suffix;
        UserDTO newUser = new UserDTO();
        newUser.username = name;
        newUser.displayName = name;
        newUser.email = name+"@email.com";
        return newUser;
    }

    public void put(URI uri, UserDTO newUser) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .PUT(jsonFor(newUser))
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        processResponse(response);
    }

    public void post(URI uri, UserDTO newUser) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(jsonFor(newUser))
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        processResponse(response);
    }

    public String get(URI uri) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        processResponse(response);

        return response.body();
    }

    public HttpRequest.BodyPublisher jsonFor(Object object) {

        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
            json = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return HttpRequest.BodyPublishers.ofString(json);
    }

    public void processResponse(HttpResponse<String> response) {

        if(response.statusCode() != 200) {
            String message = "Return status code was " + response.statusCode();
            System.out.println(message);
            throw new RuntimeException(message);
        }
    }
}
