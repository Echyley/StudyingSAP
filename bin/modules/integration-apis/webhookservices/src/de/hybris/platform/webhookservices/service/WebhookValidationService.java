/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.service;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.webhookservices.exceptions.WebhookConfigurationValidationException;
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel;
import javax.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import rx.Observable;
import java.util.Map;

/**
 * A service that provides means to test if a {@link WebhookConfigurationModel} valid.
 */
public interface WebhookValidationService
{

	/**
	 * Send Mocked Integration Object Payload to Destination to validate WebhookConfiguration
	 * @param webhookConfiguration WebhookConfiguration
	 * @param webhookPayload Mocked Payload of Integration Object
	 * @throws WebhookConfigurationValidationException Validation Exception
	 */
	void pingWebhookDestination(@NotNull WebhookConfigurationModel webhookConfiguration, @NotNull String webhookPayload) throws WebhookConfigurationValidationException;

	/**
	 * Send a test payload to check whether the WebhookConfiguration is working correctly.
	 * @param webhookConfig WebhookConfiguration to be tested
	 * @param item Item Model instance
	 * @return response entity
	 */
	Observable<ResponseEntity<Map>> pingWebhookDestination(@NotNull WebhookConfigurationModel webhookConfig, @NotNull ItemModel item);

}
