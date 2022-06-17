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
@SpringBootTest(classes = UpdateTest.Config.class)
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
public class UpdateTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Before
    public void init() {
        DBObject dbObject = new BasicDBObject();
        mongoTemplate.getCollection("foo").remove(dbObject);
    }

    @Test
    public void update() {
        // insert
        DBObject dbObject = new BasicDBObject();
        dbObject.put("name", "test");
        dbObject.put("age", 18);

        mongoTemplate.getCollection("foo").insert(dbObject);

        DBObject query = new BasicDBObject();

        DBCursor dbCursor = mongoTemplate.getCollection("foo").find(query);
        dbCursor.forEachRemaining(System.out::println);

        dbObject = new BasicDBObject();
        dbObject.put("level", "high");
        dbObject.put("age", 20);

        // 完全更新，覆盖
        WriteResult result = mongoTemplate.getCollection("foo").update(query, dbObject);
        System.out.println(result.getN() >= 1);

        System.out.println("=====updated======");

        dbCursor = mongoTemplate.getCollection("foo").find(query);
        dbCursor.forEachRemaining(System.out::println);

    }

    @Test
    public void inc() {
        // insert
        DBObject dbObject = new BasicDBObject();
        dbObject.put("name", "test");
        dbObject.put("age", 18);

        mongoTemplate.getCollection("foo").insert(dbObject);

        System.out.println(mongoTemplate.getCollection("foo").findOne());

        DBObject query = new BasicDBObject();
        query.put("name", "test");

        DBObject update = new BasicDBObject();
        DBObject prop = new BasicDBObject();
        prop.put("age", 1);
        update.put("$inc", prop);

        mongoTemplate.getCollection("foo").update(query, update);
        mongoTemplate.getCollection("foo").update(query, update);

        System.out.println("=====update $inc======");

        System.out.println(mongoTemplate.getCollection("foo").findOne());
    }

    @Test
    public void set() {
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

        prop.put("sex", "male");
        update.put("$set", prop);
        //language=mongodb
        //foo.update({"name":"test"}, {"$set":{"sex":"test"}})
        WriteResult foo = mongoTemplate.getCollection("foo").update(query, update);
        Assert.assertTrue(foo.getN() > 0);

        System.out.println("foo.update({\"name\":\"test\"}, {\"$set\":{\"sex\":\"test\"}})");
        System.out.println(mongoTemplate.getCollection("foo").findOne());
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
