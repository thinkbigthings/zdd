package org.thinkbigthings.zdd.perf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Address;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;
import org.thinkbigthings.zdd.dto.AddressDTO;
import org.thinkbigthings.zdd.dto.UserDTO;

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
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.IntStream;

@Component
public class LoadTester {

    public static final class MediaType {
        public static final String APPLICATION_JSON_VALUE = "application/json";
    }

    private HttpClient client;
    private Duration duration;
    private int numThreads;
    private Duration latency;
    private boolean insertOnly;
    private String baseUrl;

    private URI users;
    private URI info;
    private URI health;

    private Random random = new Random();
    private ObjectMapper mapper = new ObjectMapper();
    private Faker faker = new Faker(Locale.US, new Random());

    private ScheduledThreadPoolExecutor executor;

    public LoadTester(AppProperties config) {

        baseUrl = "https://" + config.getHost() + ":" + config.getPort();

        users = URI.create(baseUrl + "/user");
        info = URI.create(baseUrl + "/actuator/info");
        health = URI.create(baseUrl + "/actuator/health");

        duration = config.getTestDuration();
        insertOnly = config.isInsertOnly();
        numThreads = config.getThreads();
        latency = config.getLatency();

        executor = new ScheduledThreadPoolExecutor(numThreads);

        System.out.println("Number Threads: " + numThreads);
        System.out.println("Insert only: " + insertOnly);
        System.out.println("Latency: " + latency.toMillis()+"ms");

        String hms = String.format("%d:%02d:%02d",
                duration.toHoursPart(),
                duration.toMinutesPart(),
                duration.toSecondsPart());

        System.out.println("Running test for " + hms + " (hh:mm:ss) connecting to " + baseUrl);


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


        Instant end = Instant.now().plus(duration);

        for(int i = 0; i < numThreads; i++) {
            executor.submit(() -> makeCalls());
        }

        while(Instant.now().isBefore(end)) {
            sleep(Duration.ofMillis(1000));
        }
        executor.shutdown();

        System.out.println("Users summary: " + get(info));
    }

    private void sleep(Duration sleepDuration) {
        if(sleepDuration.isZero()) {
            return;
        }
        try {
            Thread.sleep(sleepDuration.toMillis());
        }
        catch(InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void makeCalls() {
        if(insertOnly) {
            doInserts();
        }
        else {
            doCRUD();
        }
        executor.submit(() -> makeCalls());
    }

    private void doCRUD() {

        UserDTO user = userSupplier(UUID.randomUUID().toString());
        URI userUrl = URI.create(users.toString() + "/" + user.username);

        try {
            post(users, user);

            get(userUrl);

            user.displayName = user.displayName+"-updated";
            user.phoneNumber = faker.phoneNumber().phoneNumber();
            user.email = faker.internet().emailAddress();
            user.heightCm = user.heightCm + 1;
            user.addresses.clear();
            user.addresses.add(randomAddress());
            put(userUrl, user);

            get(userUrl);

            get(info);

            get(health);

            get(users);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void doInserts() {

        IntStream.range(0, 1000).forEach(i -> {
            try {
                post(users, userSupplier(UUID.randomUUID().toString()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private UserDTO userSupplier(String suffix) {
        String name = "user" + suffix;
        UserDTO newUser = new UserDTO();
        newUser.username = name;
        newUser.displayName = name;
        newUser.phoneNumber = faker.phoneNumber().phoneNumber();
        newUser.heightCm = 150 + random.nextInt(40);
        newUser.email = faker.internet().emailAddress();
        newUser.addresses.add(randomAddress());
        return newUser;
    }

    private AddressDTO randomAddress() {

        AddressDTO address = new AddressDTO();

        Address randomAddress = faker.address();
        address.line1 = randomAddress.streetAddress();
        address.city = randomAddress.city();
        address.state = randomAddress.state();
        address.zip = randomAddress.zipCode();

        return address;
    }

    public void put(URI uri, UserDTO newUser) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .PUT(jsonFor(newUser))
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();

        sleep(latency);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        sleep(latency);

        processResponse(response);
    }

    public void post(URI uri, UserDTO newUser) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(jsonFor(newUser))
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();

        sleep(latency);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        sleep(latency);

        processResponse(response);
    }

    public String get(URI uri) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        sleep(latency);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        sleep(latency);

        processResponse(response);

        return response.body();
    }

    public HttpRequest.BodyPublisher jsonFor(Object object) {

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
