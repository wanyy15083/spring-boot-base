package com.base.service.impl;

import com.base.service.TestService;
import com.base.dao.PersonDao;
import com.base.model.QueueMessage;
import com.base.model.database.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 */
@Service
public class TestServiceImpl implements TestService {
    private static final Logger logger = LoggerFactory.getLogger(TestServiceImpl.class);

    @Autowired
    private PersonDao personDao;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbit.exchange.ex_test}")
    private String testExchange;

    @Value("${rabbit.routing.key.status}")
    private String statusRoutingKey;

    @Value("${rabbit.queue.statuschange}")
    private String statusChangeQueue;

    @Override
    public String sayHello() {
        return "hello spring";
    }

    @Override
//    @Cacheable("zzexample:cache:persons")
     public List<Person> getPerson(String name) {
        logger.info("service will invoke dao to get persons");
        return personDao.getPersons(name);
    }

    @Override
//    @Cacheable("zzexample:cache:persons")
    public List<Person> getPerson2(String name) {
        logger.info("service will invoke dao to get persons");
        return personDao.getPersons(name);
    }

    @Override
    public int insertPerson(Person person) {
        personDao.insertOrUpdatePerson(person);
        return 0;
    }

//    @RabbitListener(queues = "test.statuschange")
    public void handleStatusChangeMessage(Message message) {
        try {
            logger.info("receive MQ message = {}",message);
            QueueMessage msg = QueueMessage.fromAmqpMessage(message);
            logger.info("receive MQ status message: {}", msg.getPayload());
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("failed to process message {}, but it's consumed yet.", message);
        }
    }

    public void sendRabbitMessage(String message) {
        QueueMessage queueMessage = new QueueMessage(message);
        Message msg = queueMessage.toAmqpMessage();
        logger.info("send MQ message = {}",msg);
        rabbitTemplate.send(testExchange, statusRoutingKey, msg);
    }

//    @Scheduled(cron = "0 0/3 * * * *")
    public void testSchedule() {
        DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        logger.info("scheduler trigger at {}", df.format(new Date()));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("scheduler done");
    }

//    @Scheduled(fixedRate = 300000)
    public void testSchedule2() {
        DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        logger.info("scheduler2 trigger at {}", df.format(new Date()));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("scheduler2 done");
    }
}
