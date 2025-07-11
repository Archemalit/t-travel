package ru.tbank.itis.notificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.tbank.itis.notificationservice.config.KafkaConsumerPropertiesConfig;
import ru.tbank.itis.notificationservice.config.KafkaNotificationTopicPropertiesConfig;
import ru.tbank.itis.notificationservice.config.KafkaProducerPropertiesConfig;

@SpringBootApplication
@EnableConfigurationProperties({
        KafkaNotificationTopicPropertiesConfig.class,
        KafkaConsumerPropertiesConfig.class,
        KafkaProducerPropertiesConfig.class,
})
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

}
