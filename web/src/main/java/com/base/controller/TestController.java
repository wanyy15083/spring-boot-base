package com.base.controller;

import com.base.service.TestService;
import com.base.model.database.Person;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * just for test !!!!
 */
@RestController
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private TestService testService;

    @Value("${server.port}")
    private String port;

    @RequestMapping(value = "/test/property", method = RequestMethod.GET)
    String getProperties() {
        return port;
    }

    @RequestMapping(value = "/test/person", method = RequestMethod.GET)
    List<Person> getPerson(@Param(value = "name") String name) {
        logger.info("name = {}", name);
        logger.info("invoke getPerson2()");
        testService.getPerson2(name);
        logger.info("invoke getPerson()");
        return testService.getPerson(name);
    }

    @RequestMapping(value = "/test/person", method = RequestMethod.POST, consumes = "application/json;charset=utf-8")
    TestResponse addNewPerson(@RequestBody Person person) {
        logger.info("person= {}", person);

        testService.insertPerson(person);

        TestResponse tr = new TestResponse();
        tr.setStatus(0);
        tr.setMessage("OK");
        return tr;
    }

    @RequestMapping(value = "/test/rabbit", method = RequestMethod.POST)
    TestResponse sendRabbitMessage(@RequestBody String message) {
        logger.info("will send rabbit message: {}", message);
//        testService.sendRabbitMessage(message);

        TestResponse tr = new TestResponse();
        tr.setStatus(0);
        tr.setMessage("OK");
        return tr;
    }

    private static class TestResponse{
        private int status;
        private String message;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}

