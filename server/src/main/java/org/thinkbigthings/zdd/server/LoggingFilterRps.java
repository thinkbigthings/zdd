package org.thinkbigthings.zdd.server;

import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.servlet.*;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static java.time.Instant.now;


@Component
public class LoggingFilterRps implements Filter {

    private final long binSizeMs = 20;
    private final Runnable logger = () -> log(concurrentCopyAndClear());
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5);
    private final ConcurrentHashMap<Long, AtomicLong> timeToRequestCount = new ConcurrentHashMap();

    public LoggingFilterRps() {
        timeToRequestCount.computeIfAbsent(binSizeMs, b -> new AtomicLong(0L));
        executor.scheduleAtFixedRate(logger, 0, 1, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdownNow();
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        long startTime = System.currentTimeMillis();
        chain.doFilter(req, res);
        long elapsed = System.currentTimeMillis() - startTime;

        executor.submit(() -> accumulateStatistic(elapsed));
    }

    private void accumulateStatistic(Long elapsed) {
        timeToRequestCount.computeIfAbsent(elapsed, b -> new AtomicLong(0L));
        timeToRequestCount.get(elapsed).incrementAndGet();
    }

    private void log(Map<Long,Long> copy) {

        Instant logTime = now();

        var maxTimeMs = copy.entrySet().stream()
//                .filter(e -> ! e.getValue().equals(0L))
                .mapToLong(e -> e.getKey())
                .max()
                .orElse(0L);

//        var minTimeMs = copy.entrySet().stream()
//                .filter(e -> ! e.getValue().equals(0L))
//                .mapToLong(e -> e.getKey())
//                .min()
//                .orElse(0L);

        var numRequests = copy.values().stream().mapToLong(Long::valueOf).sum();

        var totalTime = copy.entrySet().stream()
                .map(e -> e.getKey() * e.getValue())
                .mapToLong(Long::valueOf)
                .sum();

        var averageResponseTime = Math.round((double)totalTime / (double)numRequests);

        System.out.println(logTime + " reqs, avg-ms, max-ms: [" + numRequests +", "
                + averageResponseTime + ", " + maxTimeMs+"]");

    }

    // copy and clear values atomically without locking the map
    // then can work on the copy without synchronization
    private Map<Long,Long> concurrentCopyAndClear() {
        Map<Long,Long> copy = new HashMap<>();
        timeToRequestCount.forEachEntry(1024, e -> {
            long value = e.getValue().getAndSet(0L);
            if(value != 0) {
                copy.put(e.getKey(), value);
            }
        });
        return copy;
    }

    private Map.Entry<Long,String> format(Map.Entry<Long, Long> entry) {
        return Map.entry(entry.getKey(), String.format("%03d", entry.getValue()));
    }
}
