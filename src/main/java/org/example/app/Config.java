package org.example.app;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author nikolaus.wijaya on 18/02/2023
 * @project example-kafka-automation-serenity
 */

@EnableConfigurationProperties
@ComponentScan(basePackages = "org.example")
public class Config{
}
