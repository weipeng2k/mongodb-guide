package com.murdock.books.mongodbguide.taskinstance;

import com.mongodb.WriteResult;
import com.murdock.books.mongodbguide.common.config.MongoConfig;
import com.murdock.books.mongodbguide.constants.TaskInstanceConstants;
import com.murdock.books.mongodbguide.domain.TaskInstance;
import com.murdock.books.mongodbguide.domain.TaskInstanceRuleConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author weipeng2k 2019年02月13日 上午11:25:12
 */
@SpringBootTest(classes = InnerQueryTest.Config.class)
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
public class InnerQueryTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * <pre>
     * [TaskInstanceRuleConfig(id=1968, status=1, configMap={204001=X, 205001=3}), TaskInstanceRuleConfig(id=1780, status=0, configMap={204001=X, 205001=3})]
     * </pre>
     */
    @Test
    public void array_query() {
        Map<Integer, Serializable> map = new HashMap<>();
        map.put(205001, 3);
        TaskInstanceRuleConfig ruleConfig = new TaskInstanceRuleConfig();
        ruleConfig.setConfigMap(map);
        Query query = Query.query(Criteria.where("ruleConfigList").elemMatch(Criteria.where("configMap.205001").is(3)));
        query.addCriteria(Criteria.where("id").is(138256L));
        System.out.println(query);
        mongoTemplate.find(query, TaskInstance.class, TaskInstanceConstants.COLLECTION_NAME)
                .forEach(System.out::println);
    }

    @Test
    public void upsert_id_using_list() {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(138256L));
        Update update = new Update();
        List<TaskInstanceRuleConfig> newList = new ArrayList<>();
        TaskInstanceRuleConfig taskInstanceRuleConfig = new TaskInstanceRuleConfig();
        taskInstanceRuleConfig.setStatus(3);
        taskInstanceRuleConfig.setId(1212L);
        newList.add(taskInstanceRuleConfig);
        update.set("ruleConfigList", newList);
        WriteResult upsert = mongoTemplate.upsert(query, update, TaskInstance.class,
                TaskInstanceConstants.COLLECTION_NAME);

        System.out.println(upsert);
    }

    @Test
    public void query() {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(138256L));
        System.out.println(query);
        mongoTemplate.find(query, TaskInstance.class, TaskInstanceConstants.COLLECTION_NAME)
                .forEach(System.out::println);
    }

    @Configuration
    @Import(MongoConfig.class)
    static class Config {

    }
}
