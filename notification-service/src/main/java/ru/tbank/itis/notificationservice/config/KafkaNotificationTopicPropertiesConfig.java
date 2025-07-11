package ru.tbank.itis.notificationservice.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("kafka.topic")
public record KafkaNotificationTopicPropertiesConfig(
        @NotEmpty String name, @Min(1) Integer partitions, @Min(1) Integer replicas) {}
