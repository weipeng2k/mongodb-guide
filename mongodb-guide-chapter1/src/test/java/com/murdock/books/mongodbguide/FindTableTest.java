package com.murdock.books.mongodbguide;

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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

/**
 * @author weipeng2k 2019年01月01日 下午19:25:20
 */
@SpringBootTest(classes = FindTableTest.Config.class)
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
public class FindTableTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void find_collection() {
        if (!mongoTemplate.collectionExists("mongo_test_collection")) {
            mongoTemplate.createCollection("mongo_test_collection");
        }
        DBCollection mongoTestCollection = mongoTemplate.getCollection("mongo_test_collection");

        DBCursor dbCursor = mongoTestCollection.find();
        for (DBObject dbObject : dbCursor) {
            System.out.println(dbObject);
        }
    }

    @Test
    public void find_query_project() {
        Query query = new Query();
        query.addCriteria(Criteria.where("age").gt(18));
        query.addCriteria(Criteria.where("job").is("developer"));
        List<Map> mongo_test_collection = mongoTemplate.find(query, Map.class, "mongo_test_collection");
        mongo_test_collection
                .forEach(System.out::println);
    }


    @Configuration
    @Import(MongoConfig.class)
    static class Config {

    }
}
