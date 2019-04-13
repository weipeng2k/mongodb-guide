package com.murdock.books.mongodbguide.find;

import com.murdock.books.mongodbguide.config.MongoConfig;
import com.murdock.books.mongodbguide.domain.Blog;
import com.murdock.books.mongodbguide.domain.Comment;
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

import java.util.ArrayList;
import java.util.List;

/**
 * @author weipeng2k 2019年02月13日 上午11:25:12
 */
@SpringBootTest(classes = ArrayQueryTest.Config.class)
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
public class ArrayQueryTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * <pre>
     * { "comments" : { "$all" : [ { $java : Comment(author=CommentAuthor-1, score=1, comment=comment:1) } ] } }
     * </pre>
     */
    @Test
    public void array_query() {
        List<Comment> commentList = new ArrayList<>();
        Comment comment = new Comment();
        comment.setComment("comment:1");
        comment.setScore(1);
        comment.setAuthor("CommentAuthor-1");
        commentList.add(comment);
        Query query = Query.query(Criteria.where("comments").all(commentList));
        System.out.println(query);
        mongoTemplate.find(query, Blog.class, "blog_test_collection")
                .forEach(System.out::println);
    }

    /**
     * <pre>
     * { "author.age" : { "$gte" : 27}}
     * </pre>
     */
    @Test
    public void inner_query() {
        Query query = Query.query(Criteria.where("author.age").gte(27));
        System.out.println(query);
        System.out.println(mongoTemplate.findOne(query, Blog.class, "blog_test_collection"));
    }

    @Configuration
    @Import(MongoConfig.class)
    static class Config {

    }
}
