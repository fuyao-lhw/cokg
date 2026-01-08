package com.norway.cokgbackend.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @Description: TODO
 * @Author: Norway
 * @Email: 1959415641@qq.com
 * @Date: 2026/1/3 20:38
 */
public class JsonUtil {
    // ==================== Jackson 实现 ====================
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Jackson 将JavaBean转为JSON格式字符串
     * @param bean 待转换的JavaBean对象
     * @param <T> JavaBean类型
     * @return JSON格式字符串
     */
    public static <T> String bean2JsonByJackson(T bean) {
        if (bean == null) {
            return null; // 空对象返回null，也可返回""或"{}"
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(bean);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JavaBean转JSON字符串失败（Jackson）", e);
        }
    }
}
