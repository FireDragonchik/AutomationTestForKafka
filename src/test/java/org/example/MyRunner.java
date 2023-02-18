package org.example;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
    stepNotifications = true,
    features="src/test/resources/features/",
    glue="org.example",
    tags = "@PublishListenMessageWithoutKey")
public class MyRunner {}
