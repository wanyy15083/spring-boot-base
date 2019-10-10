package com.base.dao;

import com.base.model.database.Person;

import java.util.List;

/**
 * Created by bj-s2-w1631 on 18-8-31.
 */
public interface PersonDao {

    List<Person> getPersons(String name);

    List<Person> getPersons(Person person);

    int insertOrUpdatePerson(Person person);

    int insertBatch(List<Person> personList);
}
