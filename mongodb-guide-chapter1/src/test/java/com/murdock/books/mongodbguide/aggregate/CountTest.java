package com.murdock.books.mongodbguide.aggregate;

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

/**
 * @author weipeng2k 2019年02月16日 下午20:43:00
 */
@SpringBootTest(classes = CountTest.Config.class)
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
public class CountTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void count_with_condition() {
        Criteria criteria = Criteria.where("age").lt(25);
        Query query = Query.query(criteria);
        System.out.println(query);
        long l25num = mongoTemplate.count(query, "author_test_collection");

        System.out.println(l25num);

        criteria = Criteria.where("age").gte(18);
        query = Query.query(criteria);
        System.out.println(query);
        long le18num = mongoTemplate.count(query, "author_test_collection");

        System.out.println(le18num);
    }

    @Test
    public void count_without_condition() {
        Query query = new Query();
        long all = mongoTemplate.count(query, "author_test_collection");
        System.out.println(all);
    }

    @Configuration
    @Import(MongoConfig.class)
    static class Config {

    }
}
