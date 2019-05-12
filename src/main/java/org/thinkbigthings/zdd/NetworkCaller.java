package org.thinkbigthings.zdd;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionService;

import static java.nio.charset.StandardCharsets.UTF_8;

public class NetworkCaller {


    private static HttpClient client;

    public static void main(String[] args) throws Exception {

        // clients are immutable and thread safe
        // don't check certificates (so can use self-signed) and don't verify hostname
        SSLContext sc = SSLContext.getInstance("TLSv1.3");
        sc.init(null, new TrustManager[]{new InsecureTrustManager()}, new SecureRandom());
        System.getProperties().setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());

        client = HttpClient.newBuilder()
                .sslContext(sc)
                .build();

        String info = "https://localhost:8080/actuator/info";

        String url = "https://localhost:8080/users";

        requestStreaming(info);


        System.out.println("Program done.");
        System.exit(0);
    }

    public static void requestStreaming(String url) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        // TODO make a perf branch with this code in it

        // TODO call a bunch of times (say, start with 3 threads for 10 seconds)
        // https://stackoverflow.com/questions/4912228/when-should-i-use-a-completionservice-over-an-executorservice

        // TODO post to create a user, verify success response status

        // TODO retrieve created user by name


        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept( s -> processResponse(s))
                .join();
    }

    public static void processResponse(HttpResponse<String> response) {
        processResponseBody(new ByteArrayInputStream(response.body().getBytes(UTF_8)));
    }

    public static void processResponseBody(InputStream stream) {

        try(BufferedReader br = new BufferedReader(new InputStreamReader(stream, UTF_8))) {
            br.lines().forEach(NetworkCaller::processLine);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        System.out.println("Processing Done!");
    }

    public static void processLine(String line) {
        System.out.print(".");
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