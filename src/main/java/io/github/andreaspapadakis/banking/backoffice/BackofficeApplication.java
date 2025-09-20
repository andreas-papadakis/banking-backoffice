package io.github.andreaspapadakis.banking.backoffice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages =
    "io.github.andreaspapadakis.banking.backoffice.shared.config")
public class BackofficeApplication {

  public static void main(String[] args) {
    SpringApplication.run(BackofficeApplication.class, args);
  }

}
