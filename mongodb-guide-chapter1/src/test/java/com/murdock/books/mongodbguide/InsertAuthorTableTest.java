package com.murdock.books.mongodbguide;

import com.murdock.books.mongodbguide.common.config.MongoConfig;
import com.murdock.books.mongodbguide.domain.Author;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author weipeng2k 2019年01月01日 下午19:25:20
 */
@SpringBootTest(classes = InsertAuthorTableTest.Config.class)
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
public class InsertAuthorTableTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    @Ignore
    public void insert_collection() {
        if (!mongoTemplate.collectionExists("author_test_collection")) {
            mongoTemplate.createCollection("author_test_collection");
        }

        List<Author> authors = IntStream.range(0, 1_000_000)
                .mapToObj(i -> {
                    Author author = new Author();
                    Random random = new Random();
                    author.setAge(18 + random.nextInt(20));
                    author.setName("Author-" + i);
                    return author;
                })
                .collect(Collectors.toList());

        authors.forEach(a -> mongoTemplate.insert(a, "author_test_collection"));
    }


    @Configuration
    @Import(MongoConfig.class)
    static class Config {

    }
}
