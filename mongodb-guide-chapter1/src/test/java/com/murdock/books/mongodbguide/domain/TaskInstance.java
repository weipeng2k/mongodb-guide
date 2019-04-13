package com.murdock.books.mongodbguide.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author weipeng2k 2019年03月09日 上午10:28:26
 */
@Getter
@Setter
@ToString
public class TaskInstance {

    @Id
    private Long id;

    @Field("a_id")
    private Long actorId;
    @Field("g_c")
    private Date gmtCreate;

    private Date gmtModified;

    private Map<Integer, Serializable> ba_1;
    private Map<Integer, Serializable> ba_2;

    private Map<Integer, Serializable> rc_2012;
    private Map<Integer, Serializable> rc_202099;
    private Map<Integer, Serializable> rc_202030;

    List<TaskInstanceRuleConfig> ruleConfigList;

}
