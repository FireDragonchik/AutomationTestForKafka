package utils;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ProducerUtils extends KafkaTestUtils {

    private static KafkaProducer<String, String> kafkaProducer;

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
