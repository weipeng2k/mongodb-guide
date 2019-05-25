package com.murdock.books.mongodbguide;

import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.murdock.books.mongodbguide.common.config.MongoConfig;
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

/**
 * @author weipeng2k 2019年01月01日 下午19:25:20
 */
@SpringBootTest(classes = CreateTableTest.Config.class)
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
public class CreateTableTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void test() {
        DB db = mongoTemplate.getDb();
        System.out.println(db);
    }

    @Test
    public void help() {
        CommandResult command = mongoTemplate.getDb().command("help");
        System.out.println(command);
    }

    @Test
    public void eval() {
        CommandResult command = mongoTemplate.getDb().doEval("1 + 2");
        Assert.assertTrue(command.ok());
        System.out.println(command);
    }

    @Test
    public void create_collection() {
        if (!mongoTemplate.collectionExists("mongo_test_collection")) {
            mongoTemplate.createCollection("mongo_test_collection");
        }
        DBCollection mongoTestCollection = mongoTemplate.getCollection("mongo_test_collection");
        long count = mongoTestCollection.count();
        Assert.assertTrue(count >= 0);
    }


    @Configuration
    @Import(MongoConfig.class)
    static class Config {

    }
}
