package com.murdock.books.mongodbguide.find;

import com.murdock.books.mongodbguide.config.MongoConfig;
import com.murdock.books.mongodbguide.domain.People;
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
 * @author weipeng2k 2019年02月13日 上午11:25:12
 */
@SpringBootTest(classes = QueryConditionTest.Config.class)
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
public class QueryConditionTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * <pre>
     * db.mongo_test_collection.find({"age":{"$gte":18, "$lte":36}})
     * </pre>
     */
    @Test
    public void range_query() {
        Query query = Query.query(Criteria.where("age").gte(18).lte(36));
        System.out.println(query);
        mongoTemplate.find(query, People.class, "mongo_test_collection")
                .forEach(System.out::println);
    }

    /**
     * <pre>
     * { "$or" : [ { "job" : "developer"} , { "job" : "teacher"}]}
     * 注意：criteria的用法，从左到右
     * </pre>
     */
    @Test
    public void or_query() {
        Criteria criteria = new Criteria();
        Query query = Query.query(criteria.orOperator(Criteria.where("job").is("developer"), Criteria.where("job").is("teacher")));
        System.out.println(query);
        mongoTemplate.find(query, People.class, "mongo_test_collection")
                .forEach(System.out::println);
    }

    /**
     * <pre>
     * Query: { "$and" : [ { "$or" : [ { "job" : "developer"} , { "job" : "teacher"}]} , { "age" : { "$gte" : 18 , "$lte" : 36}}]}
     *
     * 查询18到36之间的developer或者teacher
     * </pre>
     */
    @Test
    public void or_and_query() {
        Criteria and = new Criteria();
        Criteria job = new Criteria();
        Criteria orJob = job.orOperator(Criteria.where("job").is("developer"), Criteria.where("job").is("teacher"));
        Criteria andOr = and.andOperator(orJob, Criteria.where("age").gte(18).lte(36));
        Query query = Query.query(andOr);
        System.out.println(query);
        mongoTemplate.find(query, People.class, "mongo_test_collection")
                .forEach(System.out::println);

    }

    /**
     * <pre>
     * { "job" : { "$in" : [ "developer" , "police"]}}
     * </pre>
     */
    @Test
    public void in_query() {
        Criteria in = Criteria.where("job").in("developer", "police");
        Query query = Query.query(in);
        System.out.println(query);
        mongoTemplate.find(query, People.class, "mongo_test_collection")
                .forEach(System.out::println);
    }

    /**
     * <pre>
     * { "job" : { "$not" : { "$in" : [ "developer" , "police"]}}}
     * </pre>
     */
    @Test
    public void not_query() {
        Query query = Query.query(Criteria.where("job").not().in("developer", "police"));
        System.out.println(query);
        mongoTemplate.find(query, People.class, "mongo_test_collection")
                .forEach(System.out::println);
    }


    @Configuration
    @Import(MongoConfig.class)
    static class Config {

    }
}
