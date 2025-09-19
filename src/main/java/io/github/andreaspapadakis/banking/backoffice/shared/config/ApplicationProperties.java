package io.github.andreaspapadakis.banking.backoffice.shared.config;

import org.springframework.stereotype.Component;

@Component
public record ApplicationProperties(RandomNumberGeneratorProperty randomNumberGeneratorProperty) {}
