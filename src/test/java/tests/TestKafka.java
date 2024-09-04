package tests;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.Test;
import utils.KafkaTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;


public class TestKafka {

    private static final String TOPIC_NAME = "Test-topic-3";

    @Test
    public void test() {

        KafkaTestUtils.createTopicIfNotExists(TOPIC_NAME, 3, (short) 1);
        KafkaTestUtils.subscribeToTopics(Collections.singletonList(TOPIC_NAME));
        Future<RecordMetadata> recordMetadataFuture = KafkaTestUtils.sendMessage(TOPIC_NAME, "key_1", "Test Message 1");
        List<Future<RecordMetadata>> futureList = KafkaTestUtils.sendMessages(TOPIC_NAME, "base-key-", "base-value-");
//        KafkaTestUtils.printAllTopicsList();
//        KafkaTestUtils.printAllTopicsInfo();

        KafkaTestUtils.readSubscribedTopicMessages();
    }
}
