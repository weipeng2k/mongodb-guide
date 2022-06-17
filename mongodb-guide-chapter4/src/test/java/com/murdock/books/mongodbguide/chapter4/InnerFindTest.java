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
@SpringBootTest(classes = InnerFindTest.Config.class)
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
public class InnerFindTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void inner_doc_property() {
        DBCollection collection = mongoTemplate.getCollection("foo");

        DBObject query = new BasicDBObject();

        DBObject condition = new BasicDBObject();
        condition.put("$gte", 80);

        query.put("map.math", condition);

        System.out.println(query);

        DBCursor dbObjects = collection.find(query);
        dbObjects.forEach(System.out::println);
    }

    @Test
    public void inner_doc_elem_match() {
        DBCollection collection = mongoTemplate.getCollection("foo");

        DBObject query = new BasicDBObject();

        DBObject condition = new BasicDBObject();
        condition.put("$gte", 90);

        query.put("math", condition);

        DBObject elementMath = new BasicDBObject();

        elementMath.put("$elemMatch", query);

        DBObject arrayQuery = new BasicDBObject();

        arrayQuery.put("maps", elementMath);

        System.out.println(arrayQuery);

        DBCursor dbObjects = collection.find(arrayQuery);
        dbObjects.forEach(System.out::println);
    }


    @Configuration
    @Import(MongoConfig.class)
    static class Config {

    }
}
