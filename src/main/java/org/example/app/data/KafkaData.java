package org.example.app.data;

import lombok.Data;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.stereotype.Component;

/**
 * @author nikolaus.wijaya on 18/02/2023
 * @project example-kafka-automation-serenity
 */

@Data
@Component("org.example.app.data.KafkaData")
public class KafkaData {
   private String model;
   private Producer<String, String> producer;
}
