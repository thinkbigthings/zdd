package org.thinkbigthings.zdd.perf;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="connect")
public class AppProperties {

    protected String host;
    protected Integer port;

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
}