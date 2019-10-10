package com.base.dao.impl;

import com.base.dao.PersonDao;
import com.base.dao.mapper.PersonMapper;
import com.base.model.database.Person;
import com.base.model.database.PersonExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by bj-s2-w1631 on 18-8-31.
 */
@Repository
public class PersonDaoImpl implements PersonDao {
    private static final Logger logger = LoggerFactory.getLogger(PersonDaoImpl.class);

    @Autowired
    private PersonMapper personMapper;

    @Override
    @Cacheable("zzexample:cache:persons")
    public List<Person> getPersons(String name) {
        logger.info("query db for persons");
        PersonExample personExample = new PersonExample();
        PersonExample.Criteria criteria = personExample.createCriteria();
        criteria.andFnameEqualTo(name);
        return personMapper.selectByExample(personExample);
    }

    @Override
    @Cacheable("zzexample:cache:persons")
    public List<Person> getPersons(Person person) {
        logger.info("query db for persons");
        PersonExample personExample = new PersonExample();
        PersonExample.Criteria criteria = personExample.createCriteria();
        criteria.andFnameEqualTo(person.getFname());
        return personMapper.selectByExample(personExample);
    }

    @Override
    public int insertOrUpdatePerson(Person person) {
        logger.info("update person: {}", person);
        List<Person> persons = getPersons(person.getFname());
        if (persons.size() > 0) {
            person.setId(persons.get(0).getId());
            personMapper.updateByPrimaryKey(person);
        } else {
            personMapper.insertSelective(person);
        }
        return 0;
    }

    @Override
    public int insertBatch(List<Person> personList) {
        return personMapper.insertBatch(personList);
    }
}
