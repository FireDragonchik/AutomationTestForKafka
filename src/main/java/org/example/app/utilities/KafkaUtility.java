package org.example.app.utilities;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;

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
}
