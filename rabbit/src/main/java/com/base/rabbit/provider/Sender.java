package com.base.rabbit.provider;

import com.base.rabbit.config.RabbitProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.SimpleRoutingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class Sender implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(Sender.class);

    private final Map<String, ConnectionFactory> connectionFactories = new ConcurrentHashMap<>();
    private final Map<String, MessageListenerContainer> listenerContainers = new ConcurrentHashMap<>();

    private static final AtomicInteger counter = new AtomicInteger();

    private final SimpleRoutingConnectionFactory connectionFactory = new SimpleRoutingConnectionFactory();

    private static final RabbitProperties properties;
    private static final RabbitProperties properties1;
    private static final RabbitProperties.Source source;
    private static final String id;
    private static final String id1;

    static {
        properties = new RabbitProperties();
        properties.setUsername("test");
        properties.setPassword("test");
        properties.setVirtualHost("/test");
        properties.setChannelCacheSize(50);

        properties1 = new RabbitProperties();
        properties1.setUsername("guest");
        properties1.setPassword("guest");
        properties1.setVirtualHost("/");
        properties1.setChannelCacheSize(50);

        source = new RabbitProperties.Source();
        source.setExchange("ex_test");
        source.setRoutingKey("test");
        source.setQueue("test.rabbit");
        id = getId();
        id1 = getId();
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        CachingConnectionFactory connectionFactory = createConnectionFactory(properties);
        registryConnectionFactory(id, connectionFactory);
        RabbitAdmin rabbitAdmin = createRabbitAdmin(connectionFactory);
        Exchange exchange = new DirectExchange(source.getExchange(), true, false);
        rabbitAdmin.declareExchange(exchange);

        Queue queue = new Queue(source.getQueue(), true, false, false);
        rabbitAdmin.declareQueue(queue);
        Binding binding = BindingBuilder.bind(queue).to(exchange).with(source.getRoutingKey()).noargs();
        rabbitAdmin.declareBinding(binding);

        CachingConnectionFactory connectionFactory1 = createConnectionFactory(properties1);
        registryConnectionFactory(id1, connectionFactory1);
        RabbitAdmin rabbitAdmin1 = createRabbitAdmin(connectionFactory1);
        rabbitAdmin1.declareExchange(exchange);
        rabbitAdmin1.declareQueue(queue);
        rabbitAdmin1.declareBinding(binding);

        MessageListenerContainer listenerContainer = createListenerContainer(properties.getPrefetch(), properties.getAckMode(),
                properties.getConcurrency(), properties.getMaxConcurrency(), connectionFactory, message -> {
                    String msg = null;
                    try {
                        msg = new String(message.getBody(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    logger.info("on message:{}", msg);
                }, source.getQueue());

        MessageListenerContainer listenerContainer1 = createListenerContainer(properties1.getPrefetch(), properties1.getAckMode(),
                properties1.getConcurrency(), properties1.getMaxConcurrency(), connectionFactory1, message -> {
                    String msg = null;
                    try {
                        msg = new String(message.getBody(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    logger.info("on 1 message:{}", msg);
                }, source.getQueue());
        registryMessageListenerContainer(id, listenerContainer);
        registryMessageListenerContainer(id1, listenerContainer1);
        listenerContainer.start();
        listenerContainer1.start();


        SimpleMessageListenerContainer listenerContainers = (SimpleMessageListenerContainer)getListenerContainer(id);
        listenerContainers.setConcurrentConsumers(5);

    }

    @Scheduled(cron = "0/3 * * * * *")
    public void send() throws Exception {
        RabbitTemplate template = new RabbitTemplate(getConnectionFactory(id));
        RabbitTemplate template1 = new RabbitTemplate(getConnectionFactory(id1));
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Message message = MessageBuilder.withBody(("data:" + date).getBytes("UTF-8")).build();
        String date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Message message1 = MessageBuilder.withBody(("data1:" + date1).getBytes("UTF-8")).build();
        template.send(source.getExchange(), source.getRoutingKey(), message);
        logger.info("send message:{}", "data:" + date);

        template1.send(source.getExchange(), source.getRoutingKey(), message1);
        logger.info("send 1 message:{}", "data1:" + date);
    }


    public static String getId() {
        return "rabbit#" + counter.getAndIncrement();
    }

    public MessageListenerContainer getListenerContainer(String id) {
        Assert.hasText(id, "Container identifier must not be empty");
        return this.listenerContainers.get(id);
    }

    public Set<String> getListenerContainerIds() {
        return Collections.unmodifiableSet(this.listenerContainers.keySet());
    }

    public Collection<MessageListenerContainer> getListenerContainers() {
        return Collections.unmodifiableCollection(this.listenerContainers.values());
    }

    public ConnectionFactory getConnectionFactory(String id) {
        Assert.hasText(id, "ConnectionFactory identifier must not be empty");
        return this.connectionFactories.get(id);
    }

    public Set<String> getConnectionFactoryIds() {
        return Collections.unmodifiableSet(this.connectionFactories.keySet());
    }

    public Collection<ConnectionFactory> getConnectionFactories() {
        return Collections.unmodifiableCollection(this.connectionFactories.values());
    }

    public void registryConnectionFactory(String id, ConnectionFactory connectionFactory) {
        Assert.notNull(connectionFactory, "'connectionFactory' must not be null.");
        Assert.hasText(id, "id must not be empty");
        synchronized (this.connectionFactories) {
            Assert.state(!this.connectionFactories.containsKey(id), "Another factory is already registered with id '" + id + "'");
            this.connectionFactories.put(id, connectionFactory);
        }
    }

    public void registryMessageListenerContainer(String id, MessageListenerContainer messageListenerContainer) {
        Assert.notNull(connectionFactory, "'messageListenerContainer' must not be null.");
        Assert.hasText(id, "id must not be empty");
        synchronized (this.listenerContainers) {
            Assert.state(!this.listenerContainers.containsKey(id), "Another container is already registered with id '" + id + "'");
            this.listenerContainers.put(id, messageListenerContainer);
        }
    }

    public ConnectionFactory unregisterConnectionFactory(String id) {
        return this.connectionFactories.remove(id);
    }

    public MessageListenerContainer unregisterListenerContainer(String id) {
        return this.listenerContainers.remove(id);
    }


    private CachingConnectionFactory createConnectionFactory(RabbitProperties properties) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(properties.getHost());
        connectionFactory.setPort(properties.getPort());
        connectionFactory.setUsername(properties.getUsername());
        connectionFactory.setPassword(properties.getPassword());
        connectionFactory.setVirtualHost(properties.getVirtualHost());
        connectionFactory.setChannelCacheSize(properties.getChannelCacheSize());
        return connectionFactory;
//        String id = "rabbit#" + counter.getAndIncrement();
//        connectionFactories.put(id, connectionFactory);


//        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
//        admin.declareQueue(new Queue("foo"));

//        MessageConverter messageConverter = new SimpleMessageConverter();
//        MessageProperties messageProperties = new MessageProperties();
//        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN);
    }

    public RabbitAdmin createRabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    public RabbitTemplate createRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setRetryTemplate(new RetryTemplate());
        return rabbitTemplate;
    }

    public MessageListenerContainer createListenerContainer(int prefetchCount, AcknowledgeMode acknowledgeMode,
                                                            int concurrentConsumers, int maxConcurrentConsumers, ConnectionFactory connectionFactory,
                                                            MessageListener listener, String... queueNames) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueNames);
        container.setPrefetchCount(prefetchCount);
        container.setAcknowledgeMode(acknowledgeMode);
        container.setConcurrentConsumers(concurrentConsumers);
        container.setMaxConcurrentConsumers(maxConcurrentConsumers);
        container.setMessageListener(listener);
        container.setConnectionFactory(connectionFactory);
//        container.start();
        return container;
    }

    public static class SimpleAdapter {
        private boolean abort;

        SimpleAdapter(boolean abort) {
            super();
            this.abort = abort;
        }

        public void handleMessage(String input) {
            logger.info("Got it: " + input);
        }
    }


    @Override
    public void destroy() throws Exception {
        for (MessageListenerContainer listenerContainer : getListenerContainers()) {
            if (listenerContainer instanceof DisposableBean) {
                try {
                    ((DisposableBean) listenerContainer).destroy();
                } catch (Exception ex) {
                    logger.warn("Failed to destroy listener container [" + listenerContainer + "]", ex);
                }
            }
        }

        for (ConnectionFactory connectionFactory : getConnectionFactories()) {
            if (connectionFactory instanceof DisposableBean) {
                try {
                    ((DisposableBean) connectionFactory).destroy();
                } catch (Exception ex) {
                    logger.warn("Failed to destroy connection factory [" + connectionFactory + "]", ex);
                }
            }
        }
    }
}
