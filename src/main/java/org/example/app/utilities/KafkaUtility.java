package org.example.app.utilities;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Properties;

/**
 * @author nikolaus.wijaya on 18/02/2023
 * @project example-kafka-automation-serenity
 */
@Component("org.example.app.utilities.KafkaUtility")
public class KafkaUtility {
  public Producer<String, String> createProducer(String bootstrapServers, String clientId){
    Properties properties = new Properties();
    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    properties.setProperty(ProducerConfig.CLIENT_ID_CONFIG, clientId);
    properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    return new KafkaProducer<>(properties);
  }

  public Consumer<String, String> createConsumer(String bootstrapServers, String groupId, String topic) {
    final Properties props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

    // Create the consumer using props.
    final Consumer<String, String> consumer = new KafkaConsumer<>(props);

    // Subscribe to the topic.
    consumer.subscribe(Collections.singletonList(topic));
    return consumer;
  }

  public void publishEventToKafka(Producer<String, String> producer,String topic, String value, String key){
    try{
      ProducerRecord<String, String> record;
      if (key.equalsIgnoreCase("")){
        record= new ProducerRecord(topic, value);
      }else{
        record = new ProducerRecord(topic, key, value);
      }
      producer.send(record);
      producer.flush();
      producer.close();
    }catch (Exception e){
      e.printStackTrace();
      System.out.println("Failed to publish event -  error: "+e);
    }
  }

  public String findMessageByBody(Consumer<String, String> consumer, String valueBody) throws InterruptedException {
    final int giveUp = 150;
    int noRecordsCount = 0;
    String value = "";
    boolean status = false;
    while (true) {
      final ConsumerRecords<String, String> consumerRecords =
          consumer.poll(1000);

      if (consumerRecords.count() == 0) {
        noRecordsCount++;
        if (noRecordsCount > giveUp)
          break;
        else
          continue;
      }

      for (ConsumerRecord<String, String> record : consumerRecords) {
        if (record.value().contains(valueBody)) {
          value = record.value();
          status = true;
          break;
        }
      }
      consumerRecords.forEach(record -> {
        System.out.printf("Consumer Record:(%s, %s, %s, %s)\n",
            record.key(), record.value(),
            record.partition(), record.offset());
      });

      consumer.commitAsync();
      if (status) {
        break;
      }
    }

    consumer.close();
    return value;
  }

  public String findMessageByKey(Consumer<String, String> consumer, String key) throws InterruptedException {
    final int giveUp = 150;
    int noRecordsCount = 0;
    String value = "";
    boolean status = false;
    while (true) {
      final ConsumerRecords<String, String> consumerRecords =
          consumer.poll(1000);

      if (consumerRecords.count() == 0) {
        noRecordsCount++;
        if (noRecordsCount > giveUp)
          break;
        else
          continue;
      }

      for (ConsumerRecord<String, String> record : consumerRecords) {
        if (record.key().contains(key)) {
          value = record.value();
          status = true;
          break;
        }
      }
      consumerRecords.forEach(record -> {
        System.out.printf("Consumer Record:(%s, %s, %s, %s)\n",
            record.key(), record.value(),
            record.partition(), record.offset());
      });

      consumer.commitAsync();
      if (status) {
        break;
      }
    }

    consumer.close();
    return value;
  }
}
