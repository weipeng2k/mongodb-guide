package com.murdock.books.mongodbguide;

import com.mongodb.BasicDBObject;
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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * @author weipeng2k 2019年01月01日 下午19:25:20
 */
@SpringBootTest(classes = UpdateTableTest.Config.class)
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
public class UpdateTableTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void update_collection() {
        if (!mongoTemplate.collectionExists("mongo_test_collection")) {
            mongoTemplate.createCollection("mongo_test_collection");
        }
        DBCollection mongoTestCollection = mongoTemplate.getCollection("mongo_test_collection");

        DBObject findOne = mongoTestCollection.findOne();
        System.out.println(findOne);

        DBObject query = new BasicDBObject();
        query.put("name", "weipeng");

        DBObject update = new BasicDBObject();
        update.put("job", "teacher");
        WriteResult result = mongoTestCollection.update(query, update);

        Assert.assertTrue(result.getN() > 0);
    }

    @Test
    public void update_updater_inc() {
        Query query = new Query();
        Criteria criteria = Criteria.where("_id").is("5c4d45f82ae4120a2cf3e129");
        query.addCriteria(criteria);
        Update update = new Update();
        update.inc("age", 1);
        WriteResult result = mongoTemplate.updateFirst(query, update, "mongo_test_collection");
        Assert.assertTrue(result.getN() > 0);
    }

    @Test
    public void update_updater_set() {
        Query query = new Query();
        Criteria criteria = Criteria.where("_id").is("5c4d45f82ae4120a2cf3e129");
        query.addCriteria(criteria);
        Update update = new Update();
        update.set("gmtModified", new Date());
        WriteResult result = mongoTemplate.updateFirst(query, update, "mongo_test_collection");
        Assert.assertTrue(result.getN() > 0);
    }

    @Test
    public void update_updater_push() {
        Query query = new Query();
        Criteria criteria = Criteria.where("_id").is("5c4d45f82ae4120a2cf3e129");
        query.addCriteria(criteria);
        Update update = new Update();
        update.push("comments", "good");
        WriteResult result = mongoTemplate.updateFirst(query, update, "mongo_test_collection");
        Assert.assertTrue(result.getN() > 0);
    }

    @Test
    public void update_updater_pull() {
        Query query = new Query();
        Criteria criteria = Criteria.where("_id").is("5c4d45f82ae4120a2cf3e129");
        query.addCriteria(criteria);
        Update update = new Update();
        update.pull("comments", "good");
        WriteResult result = mongoTemplate.updateFirst(query, update, "mongo_test_collection");
        Assert.assertTrue(result.getN() > 0);
    }


    @Configuration
    @Import(MongoConfig.class)
    static class Config {

    }
}
