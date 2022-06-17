package com.murdock.books.mongodbguide.chapter3;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
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

/**
 * @author weipeng2k 2019年10月04日 下午12:19:46
 */
@SpringBootTest(classes = AdvancedUpdateTest.Config.class)
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
public class AdvancedUpdateTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Before
    public void clean() {
        DBCollection collection = mongoTemplate.getCollection("foo");
        // 删除所有的数据
        DBObject dbObject = new BasicDBObject();
        collection.remove(dbObject);
    }


    @Test
    public void upsert() {
        DBCollection collection = mongoTemplate.getCollection("foo");

        DBObject query = new BasicDBObject();
        query.put("name", "test");

        DBObject update = new BasicDBObject();
        DBObject age = new BasicDBObject();
        age.put("age", 1);
        update.put("$inc", age);

        WriteResult update1 = collection.update(query, update, true, false);
        System.out.println(update1);

        DBObject one = collection.findOne();
        System.out.println("FindOne:" + one);

        collection.update(query, update, true, false);
        collection.update(query, update, true, false);
        collection.update(query, update, true, false);
        collection.update(query, update, true, false);

        one = collection.findOne();
        System.out.println("FindOne:" + one);
    }

    @Test
    public void findAndModify() {
        DBCollection collection = mongoTemplate.getCollection("foo");

        DBObject query = new BasicDBObject();
        query.put("name", "test");

        DBObject update = new BasicDBObject();
        DBObject age = new BasicDBObject();
        age.put("age", 20);
        update.put("$set", age);

        DBObject andModify = collection.findAndModify(query, null, null, false, update, true, true);
        System.out.println(andModify);
    }

    @Configuration
    @Import(MongoConfig.class)
    static class Config {

    }
}
