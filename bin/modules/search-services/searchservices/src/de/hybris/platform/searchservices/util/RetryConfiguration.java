/**
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.util;

import java.time.Duration;
import java.util.Objects;

/**
 * Retry configuration.
 */
public final class RetryConfiguration {

    public static final Integer DEFAULT_MAX_ATTEMPTS = 5;
    public static final Duration DEFAULT_TIMEOUT = Duration.ofMillis(5000);
    public static final Duration DEFAULT_INTERVAL = Duration.ofMillis(1000);

    private Integer maxAttempts = DEFAULT_MAX_ATTEMPTS;
    private Duration timeout = DEFAULT_TIMEOUT;
    private Duration initialDelay;
    private Duration interval = DEFAULT_INTERVAL;
    private Duration maxInterval;
    private Double multiplier;

    private RetryConfiguration() {
    }

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public Duration getInitialDelay() {
        return initialDelay;
    }

    public Duration getInterval() {
        return interval;
    }

    public Duration getMaxInterval() {
        return maxInterval;
    }

    public Double getMultiplier() {
        return multiplier;
    }

    public static DefaultRetryConfigurationBuilder builder() {
        return new DefaultRetryConfigurationBuilder();
    }

    public static final class DefaultRetryConfigurationBuilder {

        private final RetryConfiguration retryConfiguration;

        public DefaultRetryConfigurationBuilder() {
            retryConfiguration = new RetryConfiguration();
        }

        public DefaultRetryConfigurationBuilder withMaxAttempts(final Integer maxAttempts) {
            Objects.requireNonNull(maxAttempts);

            if (maxAttempts <= 0) {
                throw new IllegalArgumentException("maxAttempts must be greater than or equal 1");
            }

            retryConfiguration.maxAttempts = maxAttempts;
            return this;
        }

        public DefaultRetryConfigurationBuilder withTimeout(final Duration timeout) {
            Objects.requireNonNull(timeout);

            retryConfiguration.timeout = timeout;
            return this;
        }

        public DefaultRetryConfigurationBuilder withInitialDelay(final Duration initialDelay) {
            Objects.requireNonNull(initialDelay);

            retryConfiguration.initialDelay = initialDelay;
            return this;
        }

        public DefaultRetryConfigurationBuilder withInterval(final Duration interval) {
            Objects.requireNonNull(interval);

            retryConfiguration.interval = interval;
            return this;
        }

        public DefaultRetryConfigurationBuilder withMaxInterval(final Duration maxInterval) {
            Objects.requireNonNull(maxInterval);

            retryConfiguration.maxInterval = maxInterval;
            return this;
        }

        public DefaultRetryConfigurationBuilder withMultiplier(final Double multiplier) {
            Objects.requireNonNull(multiplier);

            if (multiplier < 1) {
                throw new IllegalArgumentException("multiplier must be greater than or equal 1");
            }

            retryConfiguration.multiplier = multiplier;
            return this;
        }

        public RetryConfiguration build() {
            return retryConfiguration;
        }
    }
}
