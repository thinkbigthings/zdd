package org.thinkbigthings.zdd.perf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class Application {


    private static HttpClient client;

    public static void main(String[] args) throws Exception {

        // clients are immutable and thread safe
        // don't check certificates (so can use self-signed) and don't verify hostname
        SSLContext sc = SSLContext.getInstance("TLSv1.3");
        sc.init(null, new TrustManager[]{new InsecureTrustManager()}, new SecureRandom());
        System.setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());

        client = HttpClient.newBuilder()
                .sslContext(sc)
                .build();

        URI users = URI.create("https://localhost:8080/user");
        URI info = URI.create("https://localhost:8080/actuator/info");
        URI health = URI.create("https://localhost:8080/actuator/health");

        Instant end = Instant.now().plus(Duration.of(60, ChronoUnit.SECONDS));
        Long n = 1L;
        while(Instant.now().isBefore(end)) {

            System.out.println("loop " + n++);
            UserDTO user = userSupplier(UUID.randomUUID().toString());
            URI userUrl = URI.create(users.toString() + "/" + user.username);

            post(users, user);

            get(userUrl);

            user.displayName = user.displayName+"-updated";
            put(userUrl, user);

            get(userUrl);

            get(info);

            get(health);
        }

        System.out.println("Program done.");

    }

    private static UserDTO userSupplier(String suffix) {
        String name = "user" + suffix;
        UserDTO newUser = new UserDTO();
        newUser.username = name;
        newUser.displayName = name;
        newUser.email = name+"@email.com";
        return newUser;
    }

    public static HttpRequest.BodyPublisher jsonFor(Object object) {

        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
            json = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Creating request body: " + json);
        return HttpRequest.BodyPublishers.ofString(json);
    }

    public static void put(URI uri, UserDTO newUser) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .PUT(jsonFor(newUser))
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            processResponse(response);

    }

    public static void post(URI uri, UserDTO newUser) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(jsonFor(newUser))
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        processResponse(response);

    }

    public static void get(URI uri) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        processResponse(response);
    }

    public static void processResponse(HttpResponse<String> response) {

        System.out.println("process response received: " + response.body());

        if(response.statusCode() != 200) {
            String message = "Return status code was " + response.statusCode();
            System.out.println(message);
            throw new RuntimeException(message);
        }

    }

    public static class UserDTO {
        public UUID externalId;
        public String username = "";
        public String email = "";
        public String displayName = "";
    }

    public static class InsecureTrustManager implements X509TrustManager {

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) {
        }
    }
}