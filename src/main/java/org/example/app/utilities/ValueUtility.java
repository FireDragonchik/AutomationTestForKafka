package org.example.app.utilities;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * @author nikolaus.wijaya on 22/02/2023
 * @project example-kafka-automation-serenity
 */
@Component("org.example.app.utilities.ValueUtility")
public class ValueUtility {
  public static String setStringValue(String value) {
    switch (value.toUpperCase()) {
      case "NULL":
        return null;
      case "EMPTY":
        return "";
      case "RANDOM_ALPHABET":
        return RandomStringUtils.randomAlphabetic(5).toUpperCase();
      case "RANDOM_NUMBER":
        return RandomStringUtils.randomNumeric(5);
      case "RANDOM_GMAIL":
        return RandomStringUtils.randomAlphanumeric(5).toLowerCase() + "@gmail.com";
      case "RANDOM_ALPHANUMERIC":
      case "RANDOM":
        return RandomStringUtils.randomAlphanumeric(5).toUpperCase();
      case "CURRENT_DATE":
        return LocalDate.now().toString();
      case "PREVIOUS_DATE":
        return LocalDate.now().minusDays(1).toString();
      case "NEXT_DATE":
        return LocalDate.now().plusDays(1).toString();
      case "LAST_MONTH":
        return LocalDate.now().minusMonths(1).toString();
      case "NEXT_MONTH":
        return LocalDate.now().plusMonths(1).toString();
      case "LAST_YEAR":
        return LocalDate.now().minusYears(1).toString();
      case "NEXT_YEAR":
        return LocalDate.now().plusYears(1).toString();
      case "DEFAULT_DATE":
        return "2500-12-31";
      default:
        return value;
    }
  }
}
