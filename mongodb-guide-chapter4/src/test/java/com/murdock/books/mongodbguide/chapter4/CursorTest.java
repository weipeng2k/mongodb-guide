package com.murdock.books.mongodbguide.chapter4;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.murdock.books.mongodbguide.common.config.MongoConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author weipeng2k 2019年12月15日 下午16:24:12
 */
@SpringBootTest(classes = CursorTest.Config.class)
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
public class CursorTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void limit() {
        DBCollection collection = mongoTemplate.getCollection("foo");

        DBCursor dbObjects = collection.find().limit(2);
        dbObjects.forEach(System.out::println);
    }

    @Test
    public void skip() {
        DBCollection collection = mongoTemplate.getCollection("foo");

        DBCursor dbObjects = collection.find().skip(2);
        dbObjects.forEach(System.out::println);
    }

    @Test
    public void sort() {
        DBCollection collection = mongoTemplate.getCollection("foo");

        DBObject dbObject = new BasicDBObject();
        dbObject.put("name", 1);
        DBCursor dbObjects = collection.find().limit(3).sort(dbObject);
        dbObjects.forEach(System.out::println);
    }

    @Configuration
    @Import(MongoConfig.class)
    static class Config {

    }
}
