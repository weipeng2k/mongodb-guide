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
 * @author weipeng2k 2019年11月03日 下午16:43:41
 */
@SpringBootTest(classes = ArrayFindTest.Config.class)
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
public class ArrayFindTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void all() {
        DBCollection collection = mongoTemplate.getCollection("foo");

        DBObject query = new BasicDBObject();

        DBObject condition = new BasicDBObject();
        condition.put("$all", new int[]{1, 2});

        query.put("value", condition);

        System.out.println(query);

        DBCursor dbObjects = collection.find(query);
        dbObjects.forEach(System.out::println);
    }

    @Configuration
    @Import(MongoConfig.class)
    static class Config {

    }
}
