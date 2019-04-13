package com.murdock.books.mongodbguide.find;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.murdock.books.mongodbguide.config.MongoConfig;
import com.murdock.books.mongodbguide.domain.People;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

/**
 * @author weipeng2k 2019年02月13日 上午11:25:12
 */
@SpringBootTest(classes = FindBasicTest.Config.class)
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
public class FindBasicTest {

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * <pre>
     * db.mongo_test_collection.find();
     *
     * </pre>
     */
    @Test
    public void find_all() {
        MongoDatabase mongoClientDatabase = mongoClient.getDatabase("test");
        MongoCollection<Document> mongoTestCollection = mongoClientDatabase.getCollection("mongo_test_collection");
        int size = 0;
        for (Document document : mongoTestCollection.find()) {
            String name = document.getString("name");
            String job = document.getString("job");
            System.out.println(String.format("[%s,%s]", name, job));
            size++;
        }

        System.out.println("ALL:" + size);
    }

    /**
     * <pre>
     * db.mongo_test_collection.find();
     *
     * </pre>
     */
    @Test
    public void find_all_template() {
        Query query = new Query();
        mongoTemplate.find(query, Map.class, "mongo_test_collection")
                .forEach(map -> {
                    String name = (String) map.get("name");
                    String job = (String) map.get("job");
                    System.out.println(String.format("[%s,%s]", name, job));
                });
    }

    /**
     * <pre>
     * db.mongo_test_collection.find({"age":36})
     * </pre>
     */
    @Test
    public void find_with_condition() {
        MongoDatabase mongoClientDatabase = mongoClient.getDatabase("test");
        MongoCollection<Document> mongoTestCollection = mongoClientDatabase.getCollection("mongo_test_collection");
        BsonDocument bsonDocument = new BsonDocument();
        BsonInt32 bsonInt32 = new BsonInt32(36);
        bsonDocument.put("age", bsonInt32);
        for (Document document : mongoTestCollection.find(bsonDocument)) {
            String name = document.getString("name");
            String job = document.getString("job");
            Integer age = document.getInteger("age");
            System.out.println(String.format("[%s,%s,%d]", name, job, age));
        }
    }

    @Test
    public void find_with_condition_template() {
        Query query = new Query(Criteria.where("age").is(36));
        mongoTemplate.find(query, Map.class, "mongo_test_collection")
                .forEach(map -> {
                    String name = (String) map.get("name");
                    String job = (String) map.get("job");
                    Integer age = (Integer) map.get("age");
                    System.out.println(String.format("[%s,%s,%d]", name, job, age));
                });
    }

    /**
     * <pre>
     * db.mongo_test_collection.find({"age":36})
     * using domain
     * </pre>
     */
    @Test
    public void find_with_condition_template_domain() {
        Query query = new Query(Criteria.where("age").is(36));
        mongoTemplate.find(query, People.class, "mongo_test_collection")
                .forEach(System.out::println);
    }

    @Configuration
    @Import(MongoConfig.class)
    static class Config {

    }
}
