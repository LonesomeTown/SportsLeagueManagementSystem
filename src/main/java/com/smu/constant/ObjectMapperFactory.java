package com.smu.constant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 对象映射器工厂
 *
 * @author T.W
 * @date 2021/12/29
 */
public class ObjectMapperFactory {
    private ObjectMapperFactory() {
    }

    private static final ObjectMapper OBJECT_MAPPER = generateObjectMapper();

    private static ObjectMapper generateObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        //过滤null字段
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //忽略不存在的字段
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    public static ObjectMapper produce() {
        return OBJECT_MAPPER;
    }
}