package com.norway.cokgbackend.repository;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.norway.cokgbackend.enums.RedisKeyEnum.AI_CHAT_MEMORY;
import static com.norway.cokgbackend.enums.RedisKeyEnum.AI_CHAT_SESSIONS;

/**
 * @Description: TODO
 * @Author: Norway
 * @Email: 1959415641@qq.com
 * @Date: 2026/5/20 12:19
 */
@Repository // 与数据库交互的注解
@Slf4j
public class RedisChatMemoryStore implements ChatMemoryStore {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        log.info("RedisChatMemoryStore - getMessage - memoryId: {}", memoryId);
        String message = stringRedisTemplate.opsForValue().get(memoryId.toString());
        return ChatMessageDeserializer.messagesFromJson(message);
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> list) {
        log.info("RedisChatMemoryStore - updateMessage - memoryId: {}; list: {}",
                memoryId, list);
        // 存储消息
        // list转json
        String json = ChatMessageSerializer.messagesToJson(list);
        // 一天过期
        stringRedisTemplate.opsForValue().set(memoryId.toString(), json, Duration.ofDays(1));


    }

    @Override
    public void deleteMessages(Object memoryId) {
        log.info("RedisChatMemoryStore - deleteMessage - memoryId: {}", memoryId);
        // 删除聊天消息
        stringRedisTemplate.delete(AI_CHAT_MEMORY.getKey(memoryId.toString()));
    }
}
