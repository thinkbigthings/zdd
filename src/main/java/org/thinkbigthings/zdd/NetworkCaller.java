package org.thinkbigthings.zdd;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

import static java.lang.Thread.sleep;

public class NetworkCaller {


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

        String info = "https://localhost:8080/actuator/info";

        String users = "https://localhost:8080/user";

        String health = "https://localhost:8080/actuator/health";
        Instant end = Instant.now().plus(Duration.of(60, ChronoUnit.SECONDS));

        Long n = 1L;
        while(Instant.now().isBefore(end)) {

            System.out.println("loop " + n);
            User user = userSupplier(n);

            n++;

            String userUrl = users+"/"+user.getUsername();

            post(users, user);

            get(userUrl);

            user.setDisplayName(user.getDisplayName()+"updated");
            put(userUrl, user);

            get(userUrl);

            get(info);

            get(health);
        }

        System.out.println("Program done.");
        System.exit(0);
    }

    private static User userSupplier(long number) {
        String name = "user" + number;
        User newUser = new User(name);
        newUser.setRegistration(null);
        newUser.setEmail(name+"@email.com");
        return newUser;
    }

    private static HttpRequest.BodyPublisher jsonFor(Object object) {

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

    public static void put(String url, User newUser) {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .PUT(jsonFor(newUser))
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            processResponse(response);

        } catch (IOException | InterruptedException e ) {
            throw new RuntimeException(e);
        }

    }

    public static void post(String url, User newUser) {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(jsonFor(newUser))
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            processResponse(response);

        } catch (IOException | InterruptedException e ) {
            throw new RuntimeException(e);
        }

    }

    public static void get(String url) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
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