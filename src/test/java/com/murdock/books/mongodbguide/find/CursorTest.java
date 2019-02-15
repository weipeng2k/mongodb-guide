package com.murdock.books.mongodbguide.find;

import com.murdock.books.mongodbguide.config.MongoConfig;
import com.murdock.books.mongodbguide.domain.Blog;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author weipeng2k 2019年02月13日 上午11:25:12
 */
@SpringBootTest(classes = CursorTest.Config.class)
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
public class CursorTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * <pre>
     * { }
     * </pre>
     */
    @Test
    public void array_query() {
        Query query = new Query();
        query.limit(1);
        query.skip(100);
        System.out.println(query);
        System.out.println(mongoTemplate.find(query, Blog.class, "blog_test_collection"));
    }

    @Test
    public void page_query() {
        long count = mongoTemplate.count(new Query(), "blog_test_collection");
        int pageSize = 100;
        int page = (int) (count / pageSize);
        for (int i = 0; i < page; i++) {
            Query query = new Query();
            query.limit(pageSize);
            query.skip(i * pageSize);
            mongoTemplate.find(query, Blog.class, "blog_test_collection").stream()
                    .map(Blog::getId)
                    .forEach(System.out::println);
            System.out.println("BATCH:" + i);
        }
    }

    @Configuration
    @Import(MongoConfig.class)
    static class Config {

    }
}
