package com.example.notify_crawler.producer;

import com.example.notify_crawler.notice.domain.Notice;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class NoticeSerializer implements org.apache.kafka.common.serialization.Serializer<List<Notice>> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, List<Notice> data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new RuntimeException("List<Notice> 직렬화 오류", e);
        }
    }
}
