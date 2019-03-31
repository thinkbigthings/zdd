package org.thinkbigthings.zdd.perf;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix="connect")
public class AppProperties {

    protected String host;
    protected Integer port;
    protected boolean insertOnly = false;
    protected Duration latency = Duration.ofMillis(1);
    protected Integer threads = 2;
    protected Duration testDuration = Duration.ofMinutes(60);


    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public boolean isInsertOnly() {
        return insertOnly;
    }

    public void setInsertOnly(boolean insertOnly) {
        this.insertOnly = insertOnly;
    }

    public Duration getLatency() {
        return latency;
    }

    public void setLatency(Duration latency) {
        this.latency = latency;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public Duration getTestDuration() {
        return testDuration;
    }

    public void setTestDuration(Duration testDuration) {
        this.testDuration = testDuration;
    }
}