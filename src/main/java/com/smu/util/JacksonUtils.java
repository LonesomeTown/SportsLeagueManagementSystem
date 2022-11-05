package com.smu.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smu.constant.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Jackson封装工具类
 *
 * @author T.W 2020/11/6
 */
public class JacksonUtils {

    private static final Logger logger = LoggerFactory.getLogger(JacksonUtils.class);

    private JacksonUtils() throws InstantiationException {
        throw new InstantiationException("Utilities are not allowed to be instantiated");
    }

    /**
     * 转换JSON为对象
     *
     * @param json json字符串
     * @param cls  对象
     * @return T
     */
    public static <T> T convertObj(String json, Class<T> cls) {
        ObjectMapper produce = ObjectMapperFactory.produce();
        try {
            return produce.readValue(json, cls);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    /**
     * 抓换Json为对象List
     *
     * @param json json字符串
     * @param cls  对象
     * @return List<T>
     */
    public static <T> List<T> convertList(String json, Class<T> cls) {
        ObjectMapper produce = ObjectMapperFactory.produce();
        List<T> objList;
        try {
            JavaType t = produce.getTypeFactory().constructParametricType(
                    List.class, cls);
            objList = produce.readValue(json, t);
            return objList;
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(),e);
        }
        return new ArrayList<>();
    }

    /**
     * 转化JSON
     *
     * @param t 對象
     * @return String
     */
    public static <T> String toJson(T t) {
        if (t instanceof String) {
            return (String) t;
        }
        ObjectMapper produce = ObjectMapperFactory.produce();
        try {
            return JsonFormatTool.formatJson(produce.writeValueAsString(t));
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(),e);
        }
        return null;
    }
}
