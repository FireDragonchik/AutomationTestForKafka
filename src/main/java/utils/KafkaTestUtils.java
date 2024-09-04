package utils;

import logger.Logger;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class KafkaTestUtils {

    private final static String BOOTSTRAP_SERVER = "127.0.0.1:9092";
    private final static String CONSUMER_GROUP_ID = "test-app";

    public static Logger log = Logger.getInstance();

    private static KafkaConsumer<String, String> kafkaConsumer;
    private static KafkaProducer<String, String> kafkaProducer;

    public static void createTopicIfNotExists(String topicName) {
        createTopicIfNotExists(topicName, 1, (short) 1);
    }

    public static void createTopicIfNotExists(String topicName, int partitions, short replicationFactor) {

        List<String> topicNames = new ArrayList<>(getAllTopics().keySet());
        if (!topicNames.contains(topicName)) {
            Properties properties = new Properties();
            properties.put(
                    AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER
            );

            try (Admin admin = Admin.create(properties)) {
                NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);

                CreateTopicsResult result = admin.createTopics(
                        Collections.singleton(newTopic)
                );

                KafkaFuture<Void> future = result.values().get(topicName);
                future.get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void subscribeToTopics(List<String> topics) {
        getKafkaConsumer().subscribe(topics);
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

    public static void readSubscribedTopicMessages() {
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
    }

    public static Future<RecordMetadata> sendMessage(String topic, String key, String value) {
        Future<RecordMetadata> recordMetadataResult = sendToProducer(topic, key, value);
        flushAndCloseProducer();
        return recordMetadataResult;
    }

    public static List<Future<RecordMetadata>> sendMessages(String topic, String baseKey, String baseValue) {
        kafkaProducer = null;
        List<Future<RecordMetadata>> recordsMetadata = IntStream.range(0, 10)
                .mapToObj(index -> sendToProducer(topic, baseKey + index, baseValue + index))
                .collect(Collectors.toList());

        flushAndCloseProducer();
        return recordsMetadata;
    }

    private static Future<RecordMetadata> sendToProducer(String topic, String key, String value) {
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, key, value);
        Future<RecordMetadata> recordMetadataResult = getKafkaProducer().send(producerRecord, (recordMetadata, exception) -> {
            // executes every time a record is successfully sent or an exception is thrown
            if (exception == null) {
                // the record was successfully sent
                log.info("\nReceived new metadata." +
                                "\n\tTopic: %s" +
                                "\n\tKey: %s" +
                                "\n\tValue: %s" +
                                "\n\tPartition: %d" +
                                "\n\tOffset: %d" +
                                "\n\tTimestamp: %s",
                        recordMetadata.topic(),
                        producerRecord.key(),
                        producerRecord.value(),
                        recordMetadata.partition(),
                        recordMetadata.offset(),
                        DateUtils.getDateFormatter().format(new Date(recordMetadata.timestamp())));
            } else {
                log.error("Error while producing", exception);
            }
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException exception) {
            log.error("Interrupted Exception: ", exception);
        }

        return recordMetadataResult;
    }

    private static void flushAndCloseProducer() {
        getKafkaProducer().flush();
        getKafkaProducer().close();
    }

    private static Map<String, List<PartitionInfo>> getAllTopics() {
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

    private static KafkaProducer<String, String> getKafkaProducer() {
        if (kafkaProducer == null) {
            Properties properties = new Properties();
            properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
            properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            kafkaProducer = new KafkaProducer<>(properties);
        }
        return kafkaProducer;
    }
}
