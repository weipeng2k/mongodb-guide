package com.murdock.books.mongodbguide;

import com.murdock.books.mongodbguide.common.config.MongoConfig;
import com.murdock.books.mongodbguide.domain.Author;
import com.murdock.books.mongodbguide.domain.Blog;
import com.murdock.books.mongodbguide.domain.Comment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author weipeng2k 2019年01月01日 下午19:25:20
 */
@SpringBootTest(classes = InsertBlogTableTest.Config.class)
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
public class InsertBlogTableTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void insert_collection() {
        if (!mongoTemplate.collectionExists("blog_test_collection")) {
            mongoTemplate.createCollection("blog_test_collection");
        }

        List<Author> authors = IntStream.range(18, 28)
                .mapToObj(i -> {
                    Author author = new Author();
                    author.setAge(i);
                    author.setName("Author-" + i);
                    return author;
                })
                .collect(Collectors.toList());

        List<Comment> comments = IntStream.range(1, 101)
                .mapToObj(i -> {
                    Comment comment = new Comment();
                    comment.setAuthor("CommentAuthor-" + i);
                    comment.setScore(i % 10);
                    comment.setComment("comment:" + i);
                    return comment;
                })
                .collect(Collectors.toList());

        for (int i = 0; i < 10000; i++) {
            Blog blog = new Blog();
            blog.setAuthor(authors.get(i % 10));
            blog.setContent("content:" + i);
            List<Comment> commentList = new ArrayList<>();
            for (int j = 0; j < 10; j++) {
                Random random = new Random();
                commentList.add(comments.get(random.nextInt(100)));
            }
            blog.setComments(commentList);

            mongoTemplate.insert(blog, "blog_test_collection");
        }
    }


    @Configuration
    @Import(MongoConfig.class)
    static class Config {

    }
}
