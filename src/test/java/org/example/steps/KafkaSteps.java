package org.example.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.thucydides.core.steps.ScenarioSteps;
import org.example.app.ConfigSteps;
import org.example.app.data.KafkaData;
import org.example.app.properties.KafkaProperties;
import org.example.app.utilities.KafkaUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author nikolaus.wijaya on 18/02/2023
 * @project example-kafka-automation-serenity
 */

@ConfigSteps
@Component("org.example.steps.KafkaSteps")
public class KafkaSteps extends ScenarioSteps {

  @Autowired
  KafkaProperties  kafkaProperties;

  @Autowired
  KafkaData kafkaData;

  @Autowired
  KafkaUtility kafkaUtility;

  @Given("System prepare message that want to publish with variable: {} description: {} and value: {}")
  public void prepareMessage(String variable, String desc, String value) {
    String model= kafkaProperties.get("model-send-message").replace("${variable}",variable)
    .replace("${description}",desc).replace("${value}",value);
    kafkaData.setModel(model);
    System.out.println(kafkaData.getModel());
  }

  @When("System publish message to topic {}")
  public void publishMessageTo(String topic) {
    kafkaData.setProducer(kafkaUtility.createProducer(kafkaProperties.get("server"),
        kafkaProperties.get("clientId")));
    kafkaUtility.publishEventToKafka(kafkaData.getProducer(),topic,kafkaData.getModel(),"");

  }

  @When("System publish message  with key {} to topic {}")
  public void publishMessageTo(String key , String topic) {
    kafkaData.setProducer(kafkaUtility.createProducer(kafkaProperties.get("server"),
        kafkaProperties.get("clientId")));
    kafkaUtility.publishEventToKafka(kafkaData.getProducer(),topic,kafkaData.getModel(),key);

  }
}
