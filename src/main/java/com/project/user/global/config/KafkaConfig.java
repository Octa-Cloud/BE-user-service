package com.project.user.global.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.*;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.*;

@Configuration
@EnableKafka
@org.springframework.kafka.annotation.EnableKafkaRetryTopic // ★ @RetryableTopic 활성화 (명시)
public class KafkaConfig {

    @Bean
    public ProducerFactory<String,String> producerFactory(KafkaProperties props){
        Map<String,Object> cfg = new HashMap<>(props.buildProducerProperties());
        cfg.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        cfg.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        cfg.put(ProducerConfig.ACKS_CONFIG, "all");                 // 브로커 전부 ACK 요구
//        cfg.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);    // 멱등 프로듀서
//        cfg.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1); // 순서 보장
        return new DefaultKafkaProducerFactory<>(cfg);
    }
    @Bean public KafkaTemplate<String,String> kafkaTemplate(ProducerFactory<String,String> pf){ return new KafkaTemplate<>(pf); }

    @Bean
    public ConsumerFactory<String,String> consumerFactory(KafkaProperties props){
        Map<String,Object> cfg = new HashMap<>(props.buildConsumerProperties());
        cfg.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        cfg.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        cfg.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);   // 수동 커밋(ACK)
        cfg.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"); // 클라우드 운영 기본: 최신부터
        cfg.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);       // 배치 사이즈(사건당 작업시간 고려)
        cfg.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300_000); // 최대 처리 간격(5분)
        return new DefaultKafkaConsumerFactory<>(cfg);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String,String> kafkaListenerContainerFactory(
            ConsumerFactory<String,String> cf){
        var f = new ConcurrentKafkaListenerContainerFactory<String,String>();
        f.setConsumerFactory(cf);
        f.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        return f;
    }

    @Bean(name="kafkaManualAckFactory")
    public ConcurrentKafkaListenerContainerFactory<String,String> kafkaManualAckFactory(
            ConsumerFactory<String,String> cf){
        var f = new ConcurrentKafkaListenerContainerFactory<String,String>();
        f.setConsumerFactory(cf);
        f.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE); // 우리가 직접 ack
        //f.setConcurrency(1); // 파티션 1개 기준(필요 시 조정)
        return f;
    }

    @Bean
    public org.springframework.scheduling.TaskScheduler taskScheduler(){
        // @RetryableTopic 의 지연 재시도 스케줄링에 사용
        var t = new ThreadPoolTaskScheduler();
        t.setPoolSize(2);
        t.setThreadNamePrefix("user-sched-");
        t.initialize();
        return t;
    }
}