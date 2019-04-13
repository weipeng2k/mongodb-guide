package com.murdock.books.mongodbguide.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

/**
 * @author weipeng2k 2019年03月09日 下午20:38:38
 */
@Getter
@Setter
@ToString
public class TaskInstanceRuleConfig {

    private Long id;

    private int status;

    private Map<Integer, Serializable> configMap;
}
