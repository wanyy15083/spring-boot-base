package com.base.rabbit.config;


import org.apache.commons.lang3.StringUtils;

public class RabbitProperties {

    private String host = "localhost";
    private int port = 5672;
    private String username = "guest";
    private String password = "guest";
    private String virtualHost;
    private String addresses;
    private int requestedHeartbeat;
    private int connectionTimeout;
    private int channelCacheSize;
    private Integer concurrency;
    private Integer maxConcurrency;
    private Integer batchSize;
    private boolean autoAck;
    private Integer prefetch;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public String getAddresses() {
        return addresses;
    }

    public void setAddresses(String addresses) {
        this.addresses = addresses;
    }

    public int getRequestedHeartbeat() {
        return requestedHeartbeat;
    }

    public void setRequestedHeartbeat(int requestedHeartbeat) {
        this.requestedHeartbeat = requestedHeartbeat;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getChannelCacheSize() {
        return channelCacheSize;
    }

    public void setChannelCacheSize(int channelCacheSize) {
        this.channelCacheSize = channelCacheSize;
    }

    public Integer getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(Integer concurrency) {
        this.concurrency = concurrency;
    }

    public Integer getMaxConcurrency() {
        return maxConcurrency;
    }

    public void setMaxConcurrency(Integer maxConcurrency) {
        this.maxConcurrency = maxConcurrency;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public boolean getAutoAck() {
        return autoAck;
    }

    public void setAutoAck(boolean autoAck) {
        this.autoAck = autoAck;
    }

    public Integer getPrefetch() {
        return prefetch;
    }

    public void setPrefetch(Integer prefetch) {
        this.prefetch = prefetch;
    }

    @Override
    public String toString() {
        return "RabbitProperties{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", virtualHost='" + virtualHost + '\'' +
                ", addresses='" + addresses + '\'' +
                ", requestedHeartbeat=" + requestedHeartbeat +
                ", connectionTimeout=" + connectionTimeout +
                ", channelCacheSize=" + channelCacheSize +
                ", concurrency=" + concurrency +
                ", maxConcurrency=" + maxConcurrency +
                ", batchSize=" + batchSize +
                ", autoAck=" + autoAck +
                ", prefetch=" + prefetch +
                '}';
    }

    public String determineAddresses() {
        if (StringUtils.isNotBlank(this.addresses)) {
            return this.host + ":" + this.port;
        }
        return addresses;
    }


    public static class Source {
        private String exchange = "";
        private String routingKey = "";
        private String queue = "";
        private boolean exchangeDurable = true;
        private boolean exchangeAutoDelete = false;
        private boolean queueDurable = true;
        private boolean queueExclusive = false;
        private boolean queueAutoDelete = false;

        public String getExchange() {
            return exchange;
        }

        public void setExchange(String exchange) {
            this.exchange = exchange;
        }

        public String getRoutingKey() {
            return routingKey;
        }

        public void setRoutingKey(String routingKey) {
            this.routingKey = routingKey;
        }

        public String getQueue() {
            return queue;
        }

        public void setQueue(String queue) {
            this.queue = queue;
        }

        public boolean setExchangeDurable() {
            return exchangeDurable;
        }

        public void setExchangeDurable(boolean exchangeDurable) {
            this.exchangeDurable = exchangeDurable;
        }

        public boolean setExchangeAutoDelete() {
            return exchangeAutoDelete;
        }

        public void setExchangeAutoDelete(boolean exchangeAutoDelete) {
            this.exchangeAutoDelete = exchangeAutoDelete;
        }

        public boolean setQueueDurable() {
            return queueDurable;
        }

        public void setQueueDurable(boolean queueDurable) {
            this.queueDurable = queueDurable;
        }

        public boolean setQueueExclusive() {
            return queueExclusive;
        }

        public void setQueueExclusive(boolean queueExclusive) {
            this.queueExclusive = queueExclusive;
        }

        public boolean setQueueAutoDelete() {
            return queueAutoDelete;
        }

        public void setQueueAutoDelete(boolean queueAutoDelete) {
            this.queueAutoDelete = queueAutoDelete;
        }

        @Override
        public String toString() {
            return "Source{" +
                    "exchange='" + exchange + '\'' +
                    ", routingKey='" + routingKey + '\'' +
                    ", queue='" + queue + '\'' +
                    ", exchangeDurable=" + exchangeDurable +
                    ", exchangeAutoDelete=" + exchangeAutoDelete +
                    ", queueDurable=" + queueDurable +
                    ", queueExclusive=" + queueExclusive +
                    ", queueAutoDelete=" + queueAutoDelete +
                    '}';
        }
    }

}
