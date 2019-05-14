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


        for (long n = 1L; n <= 1_000; n++) {
            System.out.println("loop " + n);
            post(users, userSupplier(n));
            get(info);

            // TODO program hangs after a few hundred calls unless this sleep is here
            // would like to figure out why
            try{sleep(10);}catch(InterruptedException e) {e.printStackTrace();}
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

    private static HttpRequest.BodyPublisher toJsonPublisher(Object object) {

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

    public static void post(String url, User newUser) {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(toJsonPublisher(newUser))
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

//    public static void processResponse(HttpResponse<String> response) {
//        processResponseBody(new ByteArrayInputStream(response.body().getBytes(UTF_8)));
//    }
//
//    public static void processResponseBody(InputStream stream) {
//
//        try(BufferedReader br = new BufferedReader(new InputStreamReader(stream, UTF_8))) {
//            br.lines().forEach(NetworkCaller::processLine);
//        }
//        catch(Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println("Processing Done!");
//    }
//
//    public static void processLine(String line) {
//        System.out.println(line);
//    }

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