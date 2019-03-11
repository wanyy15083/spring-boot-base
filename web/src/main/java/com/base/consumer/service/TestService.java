package com.base.consumer.service;

import com.base.model.database.Person;

import java.util.List;

/**
 *
 * person account service
 */
public interface TestService {
    int insertPerson(Person person);

    List<Person> getPerson(String name);

    List<Person> getPerson2(String name);

    String sayHello();

}

