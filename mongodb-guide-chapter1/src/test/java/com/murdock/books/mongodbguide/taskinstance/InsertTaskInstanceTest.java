package com.murdock.books.mongodbguide.taskinstance;

import com.murdock.books.mongodbguide.common.config.MongoConfig;
import com.murdock.books.mongodbguide.constants.TaskInstanceConstants;
import com.murdock.books.mongodbguide.domain.Author;
import com.murdock.books.mongodbguide.domain.TaskInstance;
import com.murdock.books.mongodbguide.domain.TaskInstanceRuleConfig;
import com.murdock.books.mongodbguide.domain.Wrapper;
import org.junit.Ignore;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.LongStream;

/**
 * @author weipeng2k 2019年01月01日 下午19:25:20
 */
@SpringBootTest(classes = InsertTaskInstanceTest.Config.class)
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
public class InsertTaskInstanceTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    static String[] TABLE = {"A", "B", "C", "D", "E"};

    @Test(expected = Exception.class)
    public void insert_duplicate() {
        TaskInstance taskInstance = new TaskInstance();
        taskInstance.setId(123L);
        taskInstance.setGmtCreate(new Date());
        taskInstance.setGmtModified(new Date());
        Random random = new Random();
        long actorId = random.nextInt(1000);
        taskInstance.setActorId(actorId);

        Map<Integer, Serializable> ba1 = new HashMap<>();
        ba1.put(1000, "YouKu");
        ba1.put(1001, random.nextInt(2000));
        taskInstance.setBa_1(ba1);

        Map<Integer, Serializable> ba2 = new HashMap<>();
        ba2.put(2000, random.nextInt(3000));
        Author author = new Author();
        author.setName("Author" + random.nextInt(10));
        author.setAge(random.nextInt(29));
        ba2.put(2001, author);
        taskInstance.setBa_2(ba2);

        Map<Integer, Serializable> rc_2012 = new HashMap<>();
        Wrapper wrapper = new Wrapper();
        List<Author> authors1 = new ArrayList<>();
        authors1.add(author);
        authors1.add(author);
        wrapper.setAuthors(authors1);
        rc_2012.put(3000, wrapper);
        taskInstance.setRc_2012(rc_2012);

        Map<Integer, Serializable> rc_202099 = new HashMap<>();
        rc_202099.put(4000, TABLE[random.nextInt(5)]);
        taskInstance.setRc_202099(rc_202099);
        mongoTemplate.insert(taskInstance, TaskInstanceConstants.COLLECTION_NAME);
    }

    @Test
    public void long_id() {
        System.out.println(Long.MAX_VALUE);
    }

    @Test
    public void upsert() {
        TaskInstance taskInstance = new TaskInstance();
        taskInstance.setId(123L);
        taskInstance.setGmtCreate(new Date());
        taskInstance.setGmtModified(new Date());
        Random random = new Random();
        long actorId = random.nextInt(1000);
        taskInstance.setActorId(actorId);

        Map<Integer, Serializable> ba1 = new HashMap<>();
        ba1.put(1000, "YouKu");
        ba1.put(1001, random.nextInt(2000));
        taskInstance.setBa_1(ba1);

        Map<Integer, Serializable> ba2 = new HashMap<>();
        ba2.put(2000, random.nextInt(3000));
        Author author = new Author();
        author.setName("Author" + random.nextInt(10));
        author.setAge(random.nextInt(29));
        ba2.put(2001, author);
        taskInstance.setBa_2(ba2);

        Map<Integer, Serializable> rc_2012 = new HashMap<>();
        Wrapper wrapper = new Wrapper();
        List<Author> authors1 = new ArrayList<>();
        authors1.add(author);
        authors1.add(author);
        wrapper.setAuthors(authors1);
        rc_2012.put(3000, wrapper);
        taskInstance.setRc_2012(rc_2012);

        Map<Integer, Serializable> rc_202099 = new HashMap<>();
        rc_202099.put(4000, TABLE[random.nextInt(5)]);
        taskInstance.setRc_202099(rc_202099);
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(1_000_00L));
        Update update = new Update();
        update.set("a_id", actorId);
        update.set("gmtModified", new Date());
        update.set("rc_202099", rc_202099);
        mongoTemplate.upsert(query, update, TaskInstance.class, TaskInstanceConstants.COLLECTION_NAME);
    }

    @Test
    @Ignore
    public void insert_collection() {
        LongStream.range(1_300_00, 1_500_00)
                .parallel()
                .mapToObj(id -> {
                    TaskInstance taskInstance = new TaskInstance();
                    taskInstance.setId(id);
                    taskInstance.setGmtCreate(new Date());
                    taskInstance.setGmtModified(new Date());
                    Random random = new Random();
                    long actorId = random.nextInt(1000);
                    taskInstance.setActorId(actorId);

                    Map<Integer, Serializable> ba1 = new HashMap<>();
                    ba1.put(1000, "YouKu");
                    ba1.put(1001, random.nextInt(2000));
                    taskInstance.setBa_1(ba1);

                    Map<Integer, Serializable> ba2 = new HashMap<>();
                    ba2.put(2000, random.nextInt(3000));
                    Author author = new Author();
                    author.setName("Author" + random.nextInt(10));
                    author.setAge(random.nextInt(29));
                    ba2.put(2001, author);
                    taskInstance.setBa_2(ba2);

                    Map<Integer, Serializable> rc_2012 = new HashMap<>();
                    Wrapper wrapper = new Wrapper();
                    List<Author> authors1 = new ArrayList<>();
                    authors1.add(author);
                    authors1.add(author);
                    wrapper.setAuthors(authors1);
                    rc_2012.put(3000, wrapper);
                    taskInstance.setRc_2012(rc_2012);

                    Map<Integer, Serializable> rc_202099 = new HashMap<>();
                    rc_202099.put(4000, TABLE[random.nextInt(5)]);
                    taskInstance.setRc_202099(rc_202099);

                    List<TaskInstanceRuleConfig> ruleConfigList = new ArrayList<>();


                    TaskInstanceRuleConfig taskInstanceRuleConfig = new TaskInstanceRuleConfig();
                    long trId = random.nextInt(10000);
                    taskInstanceRuleConfig.setId(trId);
                    taskInstanceRuleConfig.setStatus(random.nextInt(4));
                    Map<Integer, Serializable> ruMap = new HashMap<>();
                    ruMap.put(204001, "X");
                    ruMap.put(205001, random.nextInt(10));
                    taskInstanceRuleConfig.setConfigMap(ruMap);

                    ruleConfigList.add(taskInstanceRuleConfig);


                    TaskInstanceRuleConfig taskInstanceRuleConfig2 = new TaskInstanceRuleConfig();
                    long trId2 = random.nextInt(10000);
                    taskInstanceRuleConfig2.setId(trId2);
                    taskInstanceRuleConfig2.setStatus(random.nextInt(4));
                    Map<Integer, Serializable> ruMap2 = new HashMap<>();
                    ruMap2.put(204001, "X");
                    ruMap2.put(205001, random.nextInt(10));
                    taskInstanceRuleConfig2.setConfigMap(ruMap2);

                    ruleConfigList.add(taskInstanceRuleConfig2);

                    taskInstance.setRuleConfigList(ruleConfigList);
                    return taskInstance;
                })
                .forEach(taskInstance -> mongoTemplate.insert(taskInstance, TaskInstanceConstants.COLLECTION_NAME));


    }


    @Configuration
    @Import(MongoConfig.class)
    static class Config {

    }
}
