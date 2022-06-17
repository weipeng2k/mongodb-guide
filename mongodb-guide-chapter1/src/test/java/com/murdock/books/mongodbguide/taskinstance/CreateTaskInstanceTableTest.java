package com.murdock.books.mongodbguide.taskinstance;

import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.murdock.books.mongodbguide.common.config.MongoConfig;
import com.murdock.books.mongodbguide.constants.TaskInstanceConstants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author weipeng2k 2019年01月01日 下午19:25:20
 */
@SpringBootTest(classes = CreateTaskInstanceTableTest.Config.class)
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
public class CreateTaskInstanceTableTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void test() {
        DB db = mongoTemplate.getDb();
        System.out.println(db);
    }

    @Test
    public void help() {
        CommandResult command = mongoTemplate.getDb().command("help");
        System.out.println(command);
    }

    @Test
    public void eval() {
        CommandResult command = mongoTemplate.getDb().doEval("1 + 2");
        Assert.assertTrue(command.ok());
        System.out.println(command);
    }

    @Test
    public void create_collection() {
        if (!mongoTemplate.collectionExists(TaskInstanceConstants.COLLECTION_NAME)) {
            mongoTemplate.createCollection(TaskInstanceConstants.COLLECTION_NAME);
        }
        DBCollection mongoTestCollection = mongoTemplate.getCollection(TaskInstanceConstants.COLLECTION_NAME);
        long count = mongoTestCollection.count();
        Assert.assertTrue(count >= 0);
    }

    @Test
    public void create_index() {
        Index index = new Index();
        index.named("actor_index").on("a_id", Sort.Direction.DESC).background();
        mongoTemplate.indexOps(TaskInstanceConstants.COLLECTION_NAME).ensureIndex(index);
    }

    @Test
    public void create_sub_index() {
        Index index = new Index();
        index.named("biz_key_1001_index").on("ba_1.1001", Sort.Direction.DESC).background();
        mongoTemplate.indexOps(TaskInstanceConstants.COLLECTION_NAME).ensureIndex(index);
    }

    @Test
    public void create_id_index() {
        Index index = new Index();
        index.named("id_index").on("ba_1.1001", Sort.Direction.DESC).background();
        mongoTemplate.indexOps(TaskInstanceConstants.COLLECTION_NAME).ensureIndex(index);
    }

    @Test
    public void create_list_index() {
        Index index = new Index();
        index.named("rule_config_list_204001_index").on("ruleConfigList.configMap.204001", Sort.Direction.DESC).background();
        mongoTemplate.indexOps(TaskInstanceConstants.COLLECTION_NAME).ensureIndex(index);
    }

    @Test
    public void create_list2_index() {
        Index index = new Index();
        index.named("rule_config_list_205001_index").on("ruleConfigList.configMap.205001", Sort.Direction.DESC).background();
        mongoTemplate.indexOps(TaskInstanceConstants.COLLECTION_NAME).ensureIndex(index);
    }

    @Test
    public void create_list22_index() {
        Index index = new Index();
        index.named("rule_config_list_status_index").on("ruleConfigList.status", Sort.Direction.DESC).background();
        mongoTemplate.indexOps(TaskInstanceConstants.COLLECTION_NAME).ensureIndex(index);
    }


    @Configuration
    @Import(MongoConfig.class)
    static class Config {

    }
}
