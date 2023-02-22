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
import org.example.app.utilities.ValueUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

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
    kafkaData.setVariable(ValueUtility.setStringValue(variable));
    String model= kafkaProperties.get("model-send-message").replace("${variable}",
        kafkaData.getVariable()).replace("${description}",desc)
        .replace("${value}",value);
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

  @When("System listen message for kafka topic {}")
  public void listenMessageKafka(String topic) {
    kafkaData.setConsumer(kafkaUtility.createConsumer(kafkaProperties.get("server"),
        kafkaProperties.get("groupId"),topic));
  }

  @Then("validate system success to listen related message")
  public void validateSystemSuccessToListenRelatedMessage() throws InterruptedException {
    assertThat("kafka message is not match",
        kafkaUtility.findMessageByVariable(kafkaData.getConsumer(),kafkaData.getVariable()),
        equalTo(kafkaData.getModel()));
  }
}
