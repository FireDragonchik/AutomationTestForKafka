## How to use Kafka at your automation

### Background :
Apache Kafka is an open-source distributed event streaming platform used by thousands of companies for high-performance data pipelines, streaming analytics, 
data integration, and mission-critical applications.

one of the common function from kafka is to integrate data for each apps/service. 
as a QA we need to validate that data already sent by our apps/service to the correct kafka topic, and need to validate the content of data, 
or sometimes we need to produce event to validate our apps consumer run correctly. 
This automation code is one of the simple example how to produce and consume event from kafka.

### Detail of automation:
This automation code is created using BDD concept with Serenity frammewrok which has been integrated with Spring Boot
- Serenity version: 2.6.0
- Spring boot version: 2.7.5
- Spring kafka version: 3.0.2
- hamcrest version: 2.2

### Requirement:
- Java 11
- Maven
- Kafka (local/server)

### How to start
- Makesure you already have kafka in your local pc/ server
- create kafka topic with name first_topic, this is the example comment to create kafka topic from CLI
> kafka-topics.sh --bootstrap-server localhost:9092 --topic first_topic --create --partitions 3 --replication-factor 1
- clone this automation code and wait all dependency successfully downloaded
- run this automation using MyRunner.java

### Serenity Report
![alt text](https://github.com/AryawanWijaya/example-kafka-automation-serenityBdd/blob/master/SerenityReportExample.jpg?raw=true)
![alt text](https://github.com/AryawanWijaya/example-kafka-automation-serenityBdd/blob/master/KafkaReportExample.jpg?raw=true)