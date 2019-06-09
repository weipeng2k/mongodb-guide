package com.murdock.books.mongodbguide.chapter3;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.murdock.books.mongodbguide.common.config.MongoConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author weipeng2k 2019年06月09日 下午19:22:12
 */
@SpringBootTest(classes = InsertTest.Config.class)
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
public class InsertTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Before
    public void init() {
        DBObject dbObject = new BasicDBObject();
        mongoTemplate.getCollection("foo").remove(dbObject);
    }

    @Test
    public void insert() {
        DBObject dbObject = new BasicDBObject();
        dbObject.put("name", "test");
        dbObject.put("age", 18);
        mongoTemplate.getCollection("foo").insert(dbObject);
    }

    @Test
    public void batch_insert() {
        List<DBObject> dbObjectList = IntStream.range(18, 30)
                .mapToObj(age -> {
                    DBObject dbObject = new BasicDBObject();
                    dbObject.put("name", "test");
                    dbObject.put("age", age);
                    return dbObject;
                })
                .collect(Collectors.toList());

        mongoTemplate.getCollection("foo").insert(dbObjectList);
    }


    @Configuration
    @Import(MongoConfig.class)
    static class Config {

    }
}
