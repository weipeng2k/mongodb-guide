package com.murdock.books.mongodbguide.aggregate;

import com.murdock.books.mongodbguide.common.config.MongoConfig;
import com.murdock.books.mongodbguide.domain.AuthorStats;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SkipOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author weipeng2k 2019年02月16日 下午20:43:00
 */
@SpringBootTest(classes = AggregateTest.Config.class)
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
public class AggregateTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * https://www.jianshu.com/p/5b4809324cf9
     */
    @Test
    public void aggregate_condition() {
        MatchOperation match = Aggregation.match(Criteria.where("age").gte(25));
        GroupOperation group = Aggregation.group("age").count().as("ncount").first("age").as("age");
        ProjectionOperation project = Aggregation.project("age", "ncount");

        Aggregation aggregation = Aggregation.newAggregation(match, group, project);
        AggregationResults<AuthorStats> ageStats = mongoTemplate.aggregate(aggregation,
                "author_test_collection", AuthorStats.class);

        ageStats.getMappedResults().forEach(System.out::println);
    }

    @Test
    public void aggregate_sort() {
        MatchOperation match = Aggregation.match(Criteria.where("age").gte(25));
        GroupOperation group = Aggregation.group("age").count().as("ncount").first("age").as("age");
        ProjectionOperation project = Aggregation.project("age", "ncount");
        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, "ncount");

        Aggregation aggregation = Aggregation.newAggregation(match, group, project, sort);
        AggregationResults<AuthorStats> ageStats = mongoTemplate.aggregate(aggregation,
                "author_test_collection", AuthorStats.class);

        ageStats.getMappedResults().forEach(System.out::println);
    }

    @Test
    public void aggregate_sort_skip_limit() {
        MatchOperation match = Aggregation.match(Criteria.where("age").gte(25));
        GroupOperation group = Aggregation.group("age").count().as("ncount").first("age").as("age");
        ProjectionOperation project = Aggregation.project("age", "ncount");
        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, "ncount");
        SkipOperation skip = Aggregation.skip(5L);
        LimitOperation limit = Aggregation.limit(3L);

        Aggregation aggregation = Aggregation.newAggregation(match, group, project, sort, skip, limit);
        AggregationResults<AuthorStats> ageStats = mongoTemplate.aggregate(aggregation,
                "author_test_collection", AuthorStats.class);

        ageStats.getMappedResults().forEach(System.out::println);
    }

    @Configuration
    @Import(MongoConfig.class)
    static class Config {

    }
}
