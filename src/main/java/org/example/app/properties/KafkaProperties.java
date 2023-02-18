package org.example.app.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * @author nikolaus.wijaya on 18/02/2023
 * @project example-kafka-automation-serenity
 */

@Data
@Component("org.example.app.properties.KafkaProperties")
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {
  private HashMap<String, String> data;

  public String get(String key){
    return data.get(key);
  }
}
