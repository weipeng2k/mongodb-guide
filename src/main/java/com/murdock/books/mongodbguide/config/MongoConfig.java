package com.murdock.books.mongodbguide.config;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * mongo配置
 *
 * @author weipeng2k 2019年01月01日 下午19:21:51
 */
@Configuration
public class MongoConfig {
    @Bean
    public MongoClient mongo(@Value("${mongo.main.ip}") String mainIp, @Value("${mongo.main.port}") int mainPort) {
        return new MongoClient(mainIp, mainPort);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, "test");
    }
}
