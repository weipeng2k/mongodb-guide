package com.murdock.books.mongodbguide.chapter3;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.murdock.books.mongodbguide.common.config.MongoConfig;
import org.junit.Assert;
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

/**
 * @author weipeng2k 2019年06月16日 下午22:13:42
 */
@SpringBootTest(classes = ArrayUpdateTest.Config.class)
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
public class ArrayUpdateTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Before
    public void init() {
        DBObject dbObject = new BasicDBObject();
        mongoTemplate.getCollection("foo").remove(dbObject);
    }

    @Test
    public void push() {
        // insert
        DBObject dbObject = new BasicDBObject();
        dbObject.put("name", "test");
        dbObject.put("age", 18);

        mongoTemplate.getCollection("foo").insert(dbObject);

        System.out.println("Insert:" + mongoTemplate.getCollection("foo").findOne());

        DBObject query = new BasicDBObject();
        query.put("name", "test");

        DBObject update = new BasicDBObject();
        DBObject prop = new BasicDBObject();

        prop.put("hobbies", "reading");
        update.put("$push", prop);
        WriteResult foo = mongoTemplate.getCollection("foo").update(query, update);
        Assert.assertTrue(foo.getN() > 0);
        System.out.println(mongoTemplate.getCollection("foo").findOne());

        System.out.println("======================");

        prop.put("hobbies", "tvgaming");
        foo = mongoTemplate.getCollection("foo").update(query, update);
        Assert.assertTrue(foo.getN() > 0);
        System.out.println(mongoTemplate.getCollection("foo").findOne());
    }

    @Configuration
    @Import(MongoConfig.class)
    static class Config {

    }
}
