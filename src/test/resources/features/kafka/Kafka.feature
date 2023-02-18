@KafkaFeature
Feature: Validate kafka

  @Positve @PublishListenMessageWithoutKey
  Scenario: validate publish and listen message from kafka without key
    Given   System prepare message that want to publish with variable: testing description: testing desc and value: test
    When    System publish message to topic first_topic


  @Positve @PublishListenMessageWithKey
  Scenario: validate publish and listen message from kafka without key
    Given   System prepare message that want to publish with variable: testing description: testing desc and value: test
    When    System publish message  with key testing-key2 to topic first_topic