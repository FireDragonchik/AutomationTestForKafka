package utils;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaFuture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaAdminUtils extends KafkaTestUtils {

    public static void createTopicIfNotExists(String topicName) {
        createTopicIfNotExists(topicName, 1, (short) 1);
    }

    public static void createTopicIfNotExists(String topicName, int partitions, short replicationFactor) {

        List<String> topicNames = new ArrayList<>(ConsumerUtils.getAllTopics().keySet());
        if (!topicNames.contains(topicName)) {
            Properties properties = new Properties();
            properties.put(
                    AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER
            );

            try (Admin admin = Admin.create(properties)) {
                NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);

                CreateTopicsResult result = admin.createTopics(
                        Collections.singleton(newTopic)
                );

                KafkaFuture<Void> future = result.values().get(topicName);
                future.get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
