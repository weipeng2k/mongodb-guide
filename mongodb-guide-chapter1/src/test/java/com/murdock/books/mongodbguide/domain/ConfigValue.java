package com.murdock.books.mongodbguide.domain;

import com.alibaba.fastjson.annotation.JSONType;

import java.io.Serializable;

/**
 * <pre>
 * 配置值，该值是被包裹起来的
 * </pre>
 *
 * @author weipeng2k 2018年12月19日 上午10:48:39
 */
@JSONType(typeName = "V")
public final class ConfigValue<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = -8799130181641664315L;

    public ConfigValue() {

    }

    /**
     * 属性值
     */
    private T value;

    /**
     * 获取值
     *
     * @return 值
     */
    public T getValue() {
        return value;
    }

    /**
     * 设置值
     *
     * @param value 值
     */
    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ConfigValue{" +
                "value=" + value +
                '}';
    }
}
