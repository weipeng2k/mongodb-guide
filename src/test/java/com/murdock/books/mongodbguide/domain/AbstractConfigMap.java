package com.murdock.books.mongodbguide.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <pre>
 * 抽象的ConfigMap实现，使用者可以通过将模型继承它来完成属性的设置和获取
 *
 * </pre>
 *
 * @author weipeng2k 2018年12月18日 下午18:09:06
 */
public abstract class AbstractConfigMap {

    /**
     * KV配置存储
     */
    private Map<ConfigKey, ConfigValue> configMap = new HashMap<>();

    /**
     * 根据{@link ConfigKey}返回{@link ConfigValue}
     *
     * @param configKey key, 不能为空
     * @param <T>       type
     * @return ConfigValue, 如果有就返回，没有创建一个新的
     */
    @SuppressWarnings("all")
    public <T extends Serializable> ConfigValue<T> config(ConfigKey<T> configKey) {
        Objects.requireNonNull(configKey, "configKey is null");
        return configMap.computeIfAbsent(configKey, ak -> new ConfigValue());
    }

    /**
     * 判断当前的{@link AbstractConfigMap}是否包含指定的{@link ConfigKey}
     *
     * @param configKey key，不能为空
     * @param <T>       type
     * @return 如果返回true，则包含
     */
    public <T extends Serializable> boolean hasConfig(ConfigKey<T> configKey) {
        Objects.requireNonNull(configKey, "configKey is null");
        return configMap.containsKey(configKey);
    }

    public Map<ConfigKey, ConfigValue> getConfigMap() {
        return configMap;
    }

    public void setConfigMap(Map<ConfigKey, ConfigValue> configMap) {
        this.configMap = configMap;
    }
}
