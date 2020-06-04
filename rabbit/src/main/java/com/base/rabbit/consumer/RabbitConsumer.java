package com.base.rabbit.consumer;

import com.base.rabbit.config.RabbitProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.BlockingQueueConsumer;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

//
//@EnableRabbit
//@Configuration("rabbitConsumer")
public class RabbitConsumer {
    private static final Logger logger = LoggerFactory.getLogger(RabbitConsumer.class);

    private static final AtomicInteger counter = new AtomicInteger();

    private static final Map<String, CachingConnectionFactory> connectionFactories = new ConcurrentHashMap<>();
    private static final Map<String, MessageListenerContainer> listenerContainers = new ConcurrentHashMap<>();


    public static void main(String[] args) throws InterruptedException {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(56722);
        connectionFactory.setUsername("test");
        connectionFactory.setPassword("test");
        connectionFactory.setVirtualHost("/test");
        String id = "rabbit#" + counter.getAndIncrement();
        connectionFactories.put(id, connectionFactory);

//        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
//        admin.declareQueue(new Queue("foo"));

        MessageConverter messageConverter = new SimpleMessageConverter();
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN);

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames("foo");
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setConcurrentConsumers(2);
        container.setMaxConcurrentConsumers(10);
        container.setMessageListener(new MessageListenerAdapter(new SimpleAdapter(), messageConverter));
        container.start();
        listenerContainers.put(id, container);

//        RabbitTemplate template = new RabbitTemplate(connectionFactory);
//        template.setMessageConverter(messageConverter);
//
//        List<BlockingQueue<?>> queues = getQueues(container);

        Thread.sleep(10000);
        int n = 0;
        while (true) {
//            for (int i = 1; i <= 200; i++) {
//
//                template.send("ex_test", "foo", new Message("foo # ID: id".replace("#", String.valueOf(i)).replace("id", java.util.UUID.randomUUID().toString()).getBytes(), messageProperties));
//
//            }
//            Thread.sleep(1000);
//            if (++n % 10 == 0) {
//                logger.warn(count(queues));
//            }
        }
    }

    private static String count(List<BlockingQueue<?>> queues) {
        int n = 0;
        for (BlockingQueue<?> queue : queues) {
            n += queue.size();
        }
        return "Total queue size: " + n;
    }


    private static List<BlockingQueue<?>> getQueues(SimpleMessageListenerContainer container) {
        DirectFieldAccessor accessor = new DirectFieldAccessor(container);
        List<BlockingQueue<?>> queues = new ArrayList<BlockingQueue<?>>();
        @SuppressWarnings("unchecked")
        Set<BlockingQueueConsumer> consumers = (Set<BlockingQueueConsumer>) accessor.getPropertyValue("consumers");
        for (BlockingQueueConsumer consumer : consumers) {
            accessor = new DirectFieldAccessor(consumer);
            queues.add((BlockingQueue<?>) accessor.getPropertyValue("queue"));
        }
        return queues;
    }


    private static class SimpleAdapter {

        SimpleAdapter() {
            super();
        }

        @SuppressWarnings("unused")
        public void handleMessage(String input) {
            logger.debug("Got it: " + input);
        }
    }


    @Autowired
    private RabbitListenerEndpointRegistry registry;

    private final ConcurrentMap<String, SimpleMessageListenerContainer> allQueueContainerMap = new ConcurrentHashMap<>();
    private static final String CONTAINER_NOT_EXISTS = "消息队列%s对应的监听容器不存在！";
    private volatile boolean hasInit = false;

    @Autowired
    private ConnectionFactory connectionFactory;

    public static void buildProperties(RabbitProperties rabbitProperties, RabbitProperties.Source source) {
        rabbitProperties.setAddresses("127.0.0.1:5672");
        rabbitProperties.setUsername("test");
        rabbitProperties.setPassword("test");
        rabbitProperties.setVirtualHost("/test");
        rabbitProperties.setRequestedHeartbeat(30000);
        rabbitProperties.setConcurrency(2);
        rabbitProperties.setMaxConcurrency(10);
//        rabbitProperties.setAutoAck(true);
        source.setExchange("ex_test");
        source.setRoutingKey("test");
        source.setQueue("test.rabbit");
        source.setExchangeAutoDelete(false);
        source.setExchangeDurable(true);
        source.setQueueAutoDelete(false);
        source.setQueueExclusive(false);
        source.setQueueDurable(true);
    }

//    @Bean
//    public TestConsumer consumer() {
//        return new TestConsumer();
//    }

    public ConnectionFactory getConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/test");
        connectionFactory.setUsername("test");
        connectionFactory.setPassword("test");
        return connectionFactory;
    }

    public void registry() {
        RabbitProperties properties = new RabbitProperties();
        RabbitProperties.Source source = new RabbitProperties.Source();
        buildProperties(properties, source);
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(getConnectionFactory());
        container.setQueueNames(source.getQueue());
        container.setMessageListener(new MessageListenerAdapter(new HelloWorldHandler()));
        container.afterPropertiesSet();
        container.start();
//        RabbitListenerConfigurer configurer = new RabbitListenerConfigurer() {
//
//            @Override
//            public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
//                SimpleRabbitListenerEndpoint endpoint = new SimpleRabbitListenerEndpoint();
//                endpoint.setQueueNames(source.getQueue());
//                endpoint.setMessageListener(message -> {
//                    System.out.println(QueueMessage.fromAmqpMessage(message));
//                });
//                registrar.registerEndpoint(endpoint);
//            }
//        };
    }

    public boolean resetQueueConcurrentConsumers(String queueName, int concurrentConsumers) {
        Assert.state(concurrentConsumers > 0, "参数 'concurrentConsumers' 必须大于0.");
        SimpleMessageListenerContainer container = findContainerByQueueName(queueName);
        if (container.isActive() && container.isRunning()) {
            container.setConcurrentConsumers(concurrentConsumers);
            return true;
        }
        return false;
    }


    public boolean restartMessageListener(String queueName) {
        SimpleMessageListenerContainer container = findContainerByQueueName(queueName);
        Assert.state(!container.isRunning(), String.format("消息队列%s对应的监听容器正在运行！", queueName));
        container.start();
        return true;
    }

    public boolean stopMessageListener(String queueName) {
        SimpleMessageListenerContainer container = findContainerByQueueName(queueName);
        Assert.state(container.isRunning(), String.format("消息队列%s对应的监听容器未运行！", queueName));
        container.stop();
        return true;
    }

    public List<MessageQueueDatail> statAllMessageQueueDetail() {
        List<MessageQueueDatail> queueDetailList = new ArrayList<>();
        getQueue2ContainerAllMap().entrySet().forEach(entry -> {
            String queueName = entry.getKey();
            SimpleMessageListenerContainer container = entry.getValue();
            MessageQueueDatail deatil = new MessageQueueDatail(queueName, container);
            queueDetailList.add(deatil);
        });

        return queueDetailList;
    }

    /**
     * 根据队列名查找消息监听容器
     *
     * @param queueName
     * @return
     */
    private SimpleMessageListenerContainer findContainerByQueueName(String queueName) {
        String key = StringUtils.trim(queueName);
        SimpleMessageListenerContainer container = getQueue2ContainerAllMap().get(key);
        if (container == null) {
//
//
//            ApplicationContext ctx = SpringContextUtils.getApplicationContext();
//            DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) ctx.getAutowireCapableBeanFactory();
//            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(RabbitConsumer.class);
////        beanDefinitionBuilder.addPropertyValue("name", "张三");
//            defaultListableBeanFactory.registerBeanDefinition("rabbitConsumer", beanDefinitionBuilder.getBeanDefinition());
//
//            defaultListableBeanFactory.removeBeanDefinition("rabbitConsumer");

        }
        Assert.notNull(container, String.format(CONTAINER_NOT_EXISTS, key));
        return container;
    }

    private ConcurrentMap<String, SimpleMessageListenerContainer> getQueue2ContainerAllMap() {
        if (!hasInit) {
            synchronized (allQueueContainerMap) {
                if (!hasInit) {
                    registry.getListenerContainers().forEach(container -> {
                        SimpleMessageListenerContainer simpleContainer = (SimpleMessageListenerContainer) container;
                        Arrays.stream(simpleContainer.getQueueNames()).forEach(queueName ->
                                allQueueContainerMap.putIfAbsent(StringUtils.trim(queueName), simpleContainer));
                    });
                    hasInit = true;
                }
            }
        }
        return allQueueContainerMap;
    }


    /**
     * 消息队列详情
     *
     * @author liuzhe
     * @date 2018/04/04
     */
    public static final class MessageQueueDatail {
        /**
         * 队列名称
         */
        private String queueName;

        /**
         * 监听容器标识
         */
        private String containerIdentity;

        /**
         * 监听是否有效
         */
        private boolean activeContainer;

        /**
         * 是否正在监听
         */
        private boolean running;

        /**
         * 活动消费者数量
         */
        private int activeConsumerCount;

        public MessageQueueDatail(String queueName, SimpleMessageListenerContainer container) {
            this.queueName = queueName;
            this.running = container.isRunning();
            this.activeContainer = container.isActive();
            this.activeConsumerCount = container.getActiveConsumerCount();
            this.containerIdentity = "Container@" + ObjectUtils.getIdentityHexString(container);
        }

        public String getQueueName() {
            return queueName;
        }

        public void setQueueName(String queueName) {
            this.queueName = queueName;
        }

        public String getContainerIdentity() {
            return containerIdentity;
        }

        public void setContainerIdentity(String containerIdentity) {
            this.containerIdentity = containerIdentity;
        }

        public boolean getActiveContainer() {
            return activeContainer;
        }

        public void setActiveContainer(boolean activeContainer) {
            this.activeContainer = activeContainer;
        }

        public boolean getRunning() {
            return running;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        public int getActiveConsumerCount() {
            return activeConsumerCount;
        }

        public void setActiveConsumerCount(int activeConsumerCount) {
            this.activeConsumerCount = activeConsumerCount;
        }

        @Override
        public String toString() {
            return "MessageQueueDatail{" +
                    "queueName='" + queueName + '\'' +
                    ", containerIdentity='" + containerIdentity + '\'' +
                    ", activeContainer=" + activeContainer +
                    ", running=" + running +
                    ", activeConsumerCount=" + activeConsumerCount +
                    '}';
        }
    }
}
