package com.murdock.books.mongodbguide.domain;

import com.alibaba.fastjson.annotation.JSONType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <pre>
 * ConfigKey的设计类似lst-task-core中message的AttributeKey设计方式
 *
 * 但是在Key的选择上，使用了Integer。这里的目的是保持在DB中的行的占比是有限的，节约且高效的。
 *
 * </pre>
 *
 * @author weipeng2k 2018年12月19日 上午10:57:44
 */
@JSONType(typeName = "K")
public final class ConfigKey<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = 4905707365371388158L;

    /**
     * key --> ConfigKey，防止ConfigKey过多，有点intern的概念
     */
    private static ConcurrentMap<Integer, ConfigKey> keyNames = new ConcurrentHashMap<>();
    /**
     * key
     */
    private Integer key;

    /**
     * 根据key返回对应的{@link ConfigKey}
     *
     * @param key key
     * @param <T> type
     * @return configKey, not null
     */
    @SuppressWarnings("all")
    public static <T extends Serializable> ConfigKey<T> valueOf(Integer key) {
        Objects.requireNonNull(key, "key is null");

        return keyNames.computeIfAbsent(key, ConfigKey::newInstance);
    }

    /**
     * 新建一个{@link ConfigKey}并返回
     *
     * @param key key for ConfigKey, can not be null.
     * @param <T> type
     * @return configKey
     */
    private static <T extends Serializable> ConfigKey<T> newInstance(Integer key) {
        Objects.requireNonNull(key, "key is null");

        ConfigKey<T> configKey = new ConfigKey<>();
        configKey.key = key;
        return configKey;
    }

    /**
     * 该key是否有对应的{@link ConfigKey}
     */
    public static boolean exists(Integer key) {
        Objects.requireNonNull(key, "key is null");
        return keyNames.containsKey(key);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(key);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        key = in.readInt();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConfigKey<?> configKey = (ConfigKey<?>) o;
        return Objects.equals(key, configKey.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }
}
