package org.example.steps;

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
    kafkaData.setKey(ValueUtility.setStringValue(key));
    kafkaUtility.publishEventToKafka(kafkaData.getProducer(),topic,kafkaData.getModel(),
        kafkaData.getKey());

  }

  @When("System listen message for kafka topic {}")
  public void listenMessageKafka(String topic) {
    kafkaData.setConsumer(kafkaUtility.createConsumer(kafkaProperties.get("server"),
        kafkaProperties.get("groupId"),topic));
  }

  @Then("validate system success to listen related message by {}")
  public void validateSystemSuccessToListenRelatedMessage(String condition) throws InterruptedException {
    if (condition.equalsIgnoreCase("message")){
      assertThat("kafka message is not match",
          kafkaUtility.findMessageByBody(kafkaData.getConsumer(),kafkaData.getVariable()),
          equalTo(kafkaData.getModel()));
    }else{
      assertThat("kafka message is not match",
          kafkaUtility.findMessageByKey(kafkaData.getConsumer(),kafkaData.getKey()),
          equalTo(kafkaData.getModel()));
    }
  }
}
