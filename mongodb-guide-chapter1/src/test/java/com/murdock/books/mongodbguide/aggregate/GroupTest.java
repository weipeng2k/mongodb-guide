package com.murdock.books.mongodbguide.aggregate;

import com.murdock.books.mongodbguide.config.MongoConfig;
import com.murdock.books.mongodbguide.domain.BlogAuthorScore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author weipeng2k 2019年02月17日 下午13:41:37
 */
@SpringBootTest(classes = GroupTest.Config.class)
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
public class GroupTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void group_condition() {
        Criteria age = Criteria.where("author.age").lt(25);

        GroupBy groupBy = new GroupBy("author.name");
        groupBy.initialDocument("{ score: 0, name : \"\"}").reduceFunction(
                "function (doc, pre) {var c = 0; for (var i=0,len=doc.comments.length; i<len; i++) {c+=doc.comments[i].score;} pre.score += c;pre.name=doc.author.name;}");

        GroupByResults<BlogAuthorScore> blogAuthorScores = mongoTemplate.group(age, "blog_test_collection", groupBy,
                BlogAuthorScore.class);
        for (BlogAuthorScore blogAuthorScore : blogAuthorScores) {
            System.out.println(blogAuthorScore);
        }

    }

    @Import(MongoConfig.class)
    @Configuration
    static class Config {

    }
}
