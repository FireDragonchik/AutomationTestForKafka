package tests;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.Assert;
import org.junit.Test;
import utils.ConsumerUtils;
import utils.KafkaAdminUtils;
import utils.ProducerUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;


public class TestKafka {

    private static final String TOPIC_NAME = "Test-topic-3";

    @Test
    public void test() {

        KafkaAdminUtils.createTopicIfNotExists(TOPIC_NAME, 3, (short) 1);
        ConsumerUtils.subscribeToTopics(Collections.singletonList(TOPIC_NAME));
        Future<RecordMetadata> recordMetadataFuture = ProducerUtils.sendMessage(TOPIC_NAME, "key_1", "Test Message 1");
        List<Future<RecordMetadata>> messagesList = ProducerUtils.sendMessages(TOPIC_NAME, "base-key-", "base-value-");
        messagesList.add(recordMetadataFuture);
//        KafkaTestUtils.printAllTopicsList();
//        KafkaTestUtils.printAllTopicsInfo();
        Assert.assertEquals("Not all messages were read",
                messagesList.size(), ConsumerUtils.readSubscribedTopicMessages().count());
    }
}
