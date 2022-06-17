package com.murdock.books.mongodbguide.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class ConfigMapUtils {

    private static final TypeReference<Map<ConfigKey, ConfigValue>> CONFIGMAP_TYPE_REFERENCE =
            new TypeReference<Map<ConfigKey, ConfigValue>>() {
            };

    static {
        ParserConfig.getGlobalInstance().addAccept("com.murdock.books");
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
    }

    /**
     * 序列化jsonString
     * <pre>
     *     如configMap==null,返回null
     * </pre>
     *
     * @param configMap
     * @param <T>
     * @return
     */
    public static <T extends AbstractConfigMap> String toJson(T configMap) {
        if (configMap == null) {
            return null;
        }

        return JSON.toJSONString(configMap.getConfigMap(), SerializerFeature.WriteClassName);
    }

    /**
     * 反序列化到 configMap
     * <pre>
     *    如configMapString为null或empty,返回new HashMap<>()
     * </pre>
     *
     * @param configMapString 反序列化string
     * @return
     */
    public static Map<ConfigKey, ConfigValue> toConfigMap(String configMapString) {
        if (configMapString == null || configMapString.equals("")) {
            return new HashMap<>();
        }

        return JSON.parseObject(configMapString, CONFIGMAP_TYPE_REFERENCE);
    }

}
