package io.github.andreaspapadakis.banking.backoffice.shared.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.random-number-generator")
public record RandomNumberGeneratorProperty(String apiKey, String apiUrl) {}
