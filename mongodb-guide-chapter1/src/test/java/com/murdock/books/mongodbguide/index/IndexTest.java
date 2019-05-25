package com.murdock.books.mongodbguide.index;

import com.murdock.books.mongodbguide.common.config.MongoConfig;
import com.murdock.books.mongodbguide.domain.Author;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author weipeng2k 2019年02月14日 下午20:10:11
 */
@SpringBootTest(classes = IndexTest.Config.class)
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
public class IndexTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * <pre>
     *
     * without:
     * Find author:Author(name=Author-777777, age=26), cost 522 ms.
     * with:
     * Find author:Author(name=Author-777777, age=26), cost 5 ms.
     *
     * </pre>
     */
    @Test
    public void with_or_without_index() {
        Query query = Query.query(Criteria.where("name").is("Author-777777"));
        long start = System.currentTimeMillis();
        Author author = mongoTemplate.findOne(query, Author.class, "author_test_collection");
        System.out.println("Find author:" + author + ", cost " + (System.currentTimeMillis() - start) + " ms.");
        start = System.currentTimeMillis();
        author = mongoTemplate.findOne(query, Author.class, "author_test_collection");
        System.out.println("Find author:" + author + ", cost " + (System.currentTimeMillis() - start) + " ms.");
        start = System.currentTimeMillis();
        author = mongoTemplate.findOne(query, Author.class, "author_test_collection");
        System.out.println("Find author:" + author + ", cost " + (System.currentTimeMillis() - start) + " ms.");
    }

    /**
     * 一百万，耗时：3826
     */
    @Test
    public void create_index() {
        mongoTemplate.indexOps("author_test_collection").dropAllIndexes();
        long start = System.currentTimeMillis();
        Index index = new Index().named("name_unique").on("name", Sort.Direction.ASC).unique();
        mongoTemplate.indexOps("author_test_collection").ensureIndex(index);
        System.out.println("cost:" + (System.currentTimeMillis() - start) + " ms.");
    }

    @Configuration
    @Import(MongoConfig.class)
    static class Config {

    }
}
