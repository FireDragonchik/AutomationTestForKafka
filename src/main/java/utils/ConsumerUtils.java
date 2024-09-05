package utils;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ConsumerUtils extends KafkaTestUtils {

    private final static String CONSUMER_GROUP_ID = "test-app";

    private static KafkaConsumer<String, String> kafkaConsumer;

    public static void subscribeToTopics(List<String> topics) {
        getKafkaConsumer().subscribe(topics);
    }

    public static ConsumerRecords<String, String> readSubscribedTopicMessages() {
        ConsumerRecords<String, String> records =
                getKafkaConsumer().poll(Duration.ofSeconds(3));

        int index = 1;
        for (ConsumerRecord<String, String> record : records) {
            log.info("\nMessage %d:" +
                            "\n\tKey: %s" +
                            "\n\tValue: %s" +
                            "\n\tPartition: %s" +
                            "\n\tOffset: %s",
                    index,
                    record.key(),
                    record.value(),
                    record.partition(),
                    record.offset());
            index++;
        }

        return records;
    }

    public static void printAllTopicsList() {
        getAllTopics().forEach((topicInfo, partitionInfos) -> System.out.println("Topic name: " + topicInfo));
    }

    public static void printAllTopicsInfo() {
        getAllTopics().forEach((topicInfo, partitionInfos) -> {
            log.info("Topic name: %s", topicInfo);
            log.info("Partitions info:");
            partitionInfos.forEach(System.out::println);
        });
    }

    protected static Map<String, List<PartitionInfo>> getAllTopics() {
        return getKafkaConsumer().listTopics();
    }

    private static KafkaConsumer<String, String> getKafkaConsumer() {
        if (kafkaConsumer == null) {
            Properties properties = new Properties();
            properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP_ID);
            properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

            kafkaConsumer = new KafkaConsumer<>(properties);
        }
        return kafkaConsumer;
    }
}
