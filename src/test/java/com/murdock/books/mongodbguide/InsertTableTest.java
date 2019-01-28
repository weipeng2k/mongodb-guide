package com.murdock.books.mongodbguide;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.murdock.books.mongodbguide.config.MongoConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

/**
 * @author weipeng2k 2019年01月01日 下午19:25:20
 */
@SpringBootTest(classes = InsertTableTest.Config.class)
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
public class InsertTableTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void insert_collection() {
        if (!mongoTemplate.collectionExists("mongo_test_collection")) {
            mongoTemplate.createCollection("mongo_test_collection");
        }
        DBCollection mongoTestCollection = mongoTemplate.getCollection("mongo_test_collection");
        // author
        String[] jobs = new String[]{"developer", "teacher", "driver", "police", "officer"};
        Random random = new Random();
        for (int i = 0; i< 1000; i++) {
            DBObject dbObject = new BasicDBObject();
            dbObject.put("name", "liu" + i);
            dbObject.put("age", random.nextInt(40));
            dbObject.put("job", jobs[random.nextInt(4)]);
            WriteResult writeResult = mongoTestCollection.insert(dbObject);
            System.out.println(writeResult);
        }
    }


    @Configuration
    @Import(MongoConfig.class)
    static class Config {

    }
}
