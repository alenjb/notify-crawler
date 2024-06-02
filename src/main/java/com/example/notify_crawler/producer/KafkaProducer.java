package com.example.notify_crawler.producer;

import com.example.notify_crawler.notice.domain.Notice;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

@Service
public class KafkaProducer {
    private static final String TOPIC = "notify-crawler-topic";

    public static void produce(List<Notice> newNotices) {
        // Kafka Producer 설정
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "15.164.34.15:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, NoticeSerializer.class.getName());

        // Kafka Producer 생성
        org.apache.kafka.clients.producer.KafkaProducer<String, List<Notice>> producer = new org.apache.kafka.clients.producer.KafkaProducer<>(props);

        // Kafka에 메시지 보내기
        ProducerRecord<String, List<Notice>> record = new ProducerRecord<>(TOPIC, newNotices);
        producer.send(record);

        // Producer 종료
        producer.flush();
        producer.close();
    }
}