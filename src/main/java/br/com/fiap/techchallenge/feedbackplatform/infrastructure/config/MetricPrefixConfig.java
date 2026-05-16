package br.com.fiap.techchallenge.feedbackplatform.infrastructure.config;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.MeterFilter;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

@Singleton
public class MetricPrefixConfig {

    @ConfigProperty(name = "feedback.metrics.prefix", defaultValue = "feedback.core.")
    String metricPrefix;

    @Produces
    @Singleton
    public MeterFilter addPrefixToMetrics() {
        return new MeterFilter() {
            @Override
            public Meter.Id map(Meter.Id id) {
                if (id.getName().startsWith(metricPrefix)) {
                    return id;
                }
                return id.withName(metricPrefix + id.getName());
            }
        };
    }
}