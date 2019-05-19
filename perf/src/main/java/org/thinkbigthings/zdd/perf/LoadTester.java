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
import java.util.stream.IntStream;

public class LoadTester {


    private HttpClient client;
    private Duration duration = Duration.of(60, ChronoUnit.SECONDS);

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

        Long n = 1L;
        while(Instant.now().isBefore(end)) {

//            // basic single threaded, creates 4.7K users
//            doCRUD();
//            n += 1;

            // creates 20K users
            int count = 10;
            IntStream.range(0, count).parallel().forEach(i -> doCRUD());
            n += count;
        }

        System.out.println("Performed operations for " + n + " users");
    }

    private void doCRUD() {

        URI users = URI.create("https://localhost:8080/user");
        URI info = URI.create("https://localhost:8080/actuator/info");
        URI health = URI.create("https://localhost:8080/actuator/health");

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

    public void get(URI uri) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        processResponse(response);
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
