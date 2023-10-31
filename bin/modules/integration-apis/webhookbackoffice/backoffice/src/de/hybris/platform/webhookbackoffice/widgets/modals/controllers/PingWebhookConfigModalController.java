/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookbackoffice.widgets.modals.controllers;

import static de.hybris.platform.apiregistrybackoffice.constants.ApiregistrybackofficeConstants.NOTIFICATION_TYPE;

import com.hybris.cockpitng.components.Editor;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationbackoffice.widgets.modals.generator.IntegrationObjectJsonGenerator;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.webhookservices.exceptions.WebhookConfigurationValidationException;
import de.hybris.platform.webhookservices.filter.WebhookFilterService;
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel;
import de.hybris.platform.webhookservices.service.WebhookValidationService;

import java.net.SocketTimeoutException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.http.ResponseEntity;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Button;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;

import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent.Level;
import com.hybris.cockpitng.annotations.SocketEvent;
import com.hybris.cockpitng.annotations.ViewEvent;
import com.hybris.cockpitng.util.DefaultWidgetController;
import com.hybris.cockpitng.util.notifications.NotificationService;

import rx.Observable;

import java.util.Objects;

/**
 * Controller to load mocked JSON and send to destination.
 */
public class PingWebhookConfigModalController extends DefaultWidgetController
{

	protected static final String WEBHOOK_CONFIG_PARAM = "templateWebhookConfiguration";
	protected static final String WEBHOOK_SEND_BUTTON = "webhookbackofficeMockedJsonSendButton";
	protected static final String PING_WEBHOOK_CONFIG_SUCCESS = "webhookbackoffice.pingWebhookConfiguration.info.msg.pingSuccess";
	protected static final String PING_WEBHOOK_CONFIG_FAILURE = "webhookbackoffice.pingWebhookConfiguration.info.msg.pingFailure";
	protected static final String SELECT_ENTITY_ERROR = "webhookbackoffice.pingWebhookConfiguration.info.msg.selectEntityError";
	protected static final String ITEM_FILTERED_OUT_WARNING = "webhookbackoffice.pingWebhookConfiguration.info.msg.itemFilteredOutWarning";
	protected static final String REFERENCE = "Reference";

	protected static final String ERROR_400 = "webhookbackoffice.pingWebhookConfiguration.error.msg.400";
	protected static final String ERROR_401 = "webhookbackoffice.pingWebhookConfiguration.error.msg.401";
	protected static final String ERROR_403 = "webhookbackoffice.pingWebhookConfiguration.error.msg.403";
	protected static final String ERROR_404 = "webhookbackoffice.pingWebhookConfiguration.error.msg.404";
	protected static final String ERROR_405 = "webhookbackoffice.pingWebhookConfiguration.error.msg.405";
	protected static final String ERROR_500 = "webhookbackoffice.pingWebhookConfiguration.error.msg.500";
	protected static final String ERROR_503 = "webhookbackoffice.pingWebhookConfiguration.error.msg.503";
	protected static final String ERROR_DEFAULT = "webhookbackoffice.pingWebhookConfiguration.error.msg.default";
	protected static final String ERROR_SOCKET_TIME_OUT = "webhookbackoffice.pingWebhookConfiguration.error.msg.timeout";
	protected static final String ERROR_EMPTY_PAYLOAD = "webhookbackoffice.pingWebhookConfiguration.error.msg.empty.payload";
	protected static final Map<Integer, String> HTTP_ERROR_CODE_MAP = Map.of(
			HttpStatus.BAD_REQUEST.value(), ERROR_400,
			HttpStatus.UNAUTHORIZED.value(), ERROR_401,
			HttpStatus.FORBIDDEN.value(), ERROR_403,
			HttpStatus.NOT_FOUND.value(), ERROR_404,
			HttpStatus.METHOD_NOT_ALLOWED.value(), ERROR_405,
			HttpStatus.INTERNAL_SERVER_ERROR.value(), ERROR_500,
			HttpStatus.SERVICE_UNAVAILABLE.value(), ERROR_503);

	@WireVariable
	protected transient IntegrationObjectJsonGenerator outboundIntegrationObjectJsonGenerator;

	@WireVariable
	protected transient WebhookValidationService defaultWebhookValidationService;

	@WireVariable
	protected transient NotificationService notificationService;

	@WireVariable
	protected transient WebhookFilterService defaultWebhookFilterService;

	protected Textbox webhookbackofficeJsonTextBox;
	protected Textbox webhookbackofficeJsonTextDestinationPathBox;
	protected Textbox webhookbackofficeIODestinationPathBox;
	protected Button webhookbackofficeMockedJsonSendButton;
	protected Editor itemModelInstanceEditor;
	protected Tabbox webhookbackofficeMetadataTabbox;
	protected Tab webhookbackofficeJsonTab;
	protected Tab webhookbackofficeIntegrationObjectInstanceTab;

	protected WebhookConfigurationModel webhookConfiguration;

	private static final Logger LOG = Log.getLogger(PingWebhookConfigModalController.class);

	@SocketEvent(socketId = WEBHOOK_CONFIG_PARAM)
	public void loadTestWebhookConfigurationModal(final WebhookConfigurationModel webhookConfigurationModel)
	{
		if (webhookConfigurationModel != null)
		{
			webhookConfiguration = webhookConfigurationModel;
			final String webhookPayload = outboundIntegrationObjectJsonGenerator.generateJson(
					webhookConfiguration.getIntegrationObject());
			webhookbackofficeJsonTextBox.setValue(webhookPayload);

			setDestinationPath();

			if (webhookConfiguration.getIntegrationObject() != null)
			{
				final String typeCode = webhookConfiguration.getIntegrationObject().getRootItem().getCode();
				itemModelInstanceEditor.setType(REFERENCE + "(" + typeCode + ")");
			}
		}
	}

	@ViewEvent(componentID = WEBHOOK_SEND_BUTTON, eventName = Events.ON_CLICK)
	public void sendWebhookPayload()
	{
		if (webhookbackofficeMetadataTabbox.getSelectedTab().equals(webhookbackofficeJsonTab))
		{
			final String mockedJson = webhookbackofficeJsonTextBox.getText().replaceAll(System.lineSeparator(), "");

			if (webhookConfiguration == null && StringUtils.isBlank(mockedJson))
			{
				final String errorMessage = getLabel(PING_WEBHOOK_CONFIG_FAILURE, new String[]
						{ getDestinationUrl(), getLabel(ERROR_EMPTY_PAYLOAD) });
				handleErrorMessage(errorMessage);
			}
			else if (pingWebhookDestination(mockedJson))
			{
				handleSuccessMessage();
			}
		}
		else if (webhookbackofficeMetadataTabbox.getSelectedTab().equals(webhookbackofficeIntegrationObjectInstanceTab))
		{
			final Object item = itemModelInstanceEditor.getValue();

			if (Objects.isNull(item))
			{
				final String errorMessage = getLabel(SELECT_ENTITY_ERROR);
				notificationService.notifyUser(Strings.EMPTY, NOTIFICATION_TYPE, Level.FAILURE, errorMessage);
				LOG.error(errorMessage);
			}
			else
			{
				defaultWebhookFilterService.filter((ItemModel) item, webhookConfiguration.getFilterLocation())
				                           .ifPresentOrElse(itemModel -> sendToWebhookDestination(this.webhookConfiguration, itemModel),
						                           ()-> popUpFilterWarningMsg((ItemModel) item));
			}
		}
	}

	private void popUpFilterWarningMsg(final ItemModel item)
	{
		final String warningMessage = getLabel(ITEM_FILTERED_OUT_WARNING, new String[]{ item.getItemtype() });
		handleWarningMessage(warningMessage);
	}

	private void sendToWebhookDestination(final WebhookConfigurationModel webhookConfiguration, final ItemModel itemModel)
	{
		final Observable<ResponseEntity<Map>> observable = defaultWebhookValidationService.pingWebhookDestination(webhookConfiguration, itemModel);
		observable.subscribe(response -> handleObservableResponseMessage(response), error -> handleObservableErrorMessage(error));
	}

	protected String getFallbackMessage(final int statusCode)
	{
		return getLabel(HTTP_ERROR_CODE_MAP.getOrDefault(statusCode, ERROR_DEFAULT));
	}

	private boolean pingWebhookDestination(final String mockedJson)
	{
		try
		{
			defaultWebhookValidationService.pingWebhookDestination(this.webhookConfiguration, mockedJson);
		}
		catch (final WebhookConfigurationValidationException e)
		{
			final String errorMessage;

			if (e.getCause() instanceof HttpClientErrorException)
			{
				errorMessage = getFallbackMessage(((HttpClientErrorException) e.getCause()).getRawStatusCode());
			}
			else if (e.getCause() instanceof HttpServerErrorException)
			{
				errorMessage = getFallbackMessage(((HttpServerErrorException) e.getCause()).getRawStatusCode());
			}
			else if (e.getCause() instanceof SocketTimeoutException)
			{
				errorMessage = getLabel(ERROR_SOCKET_TIME_OUT);
			}
			else
			{
				errorMessage = getLabel(ERROR_DEFAULT);
			}

			handleErrorMessage(getLabel(PING_WEBHOOK_CONFIG_FAILURE, new String[]{ getDestinationUrl(), errorMessage }));

			return false;
		}

		return true;
	}

	private void handleSuccessMessage()
	{
		final String successMessage = getLabel(PING_WEBHOOK_CONFIG_SUCCESS, new String[]
				{ getDestinationUrl() });

		notificationService.notifyUser(Strings.EMPTY, NOTIFICATION_TYPE, Level.SUCCESS, successMessage);
		LOG.info(successMessage);
	}

	private void handleErrorMessage(final String errorMessage)
	{
		notificationService.notifyUser(Strings.EMPTY, NOTIFICATION_TYPE, Level.FAILURE, errorMessage);
		LOG.error(errorMessage);
	}

	private void handleWarningMessage(final String warningMessage)
	{
		notificationService.notifyUser(Strings.EMPTY, NOTIFICATION_TYPE, Level.WARNING, warningMessage);
		LOG.warn(warningMessage);
	}

	private void handleObservableResponseMessage(final ResponseEntity<Map> response)
	{

		if (response.getStatusCode().is2xxSuccessful())
		{
			handleSuccessMessage();
		}
		else
		{
			final String errorCodeReason = getFallbackMessage(response.getStatusCodeValue());
			handleErrorMessage(getLabel(PING_WEBHOOK_CONFIG_FAILURE, new String[]{ getDestinationUrl(), errorCodeReason }));
		}
	}

	private void handleObservableErrorMessage(final Throwable error)
	{
		final String errorCodeReason;

		if (error instanceof HttpClientErrorException)
		{
			errorCodeReason = getFallbackMessage(((HttpClientErrorException) error).getRawStatusCode());
		}
		else if (error instanceof HttpServerErrorException)
		{
			errorCodeReason = getFallbackMessage(((HttpServerErrorException) error).getRawStatusCode());
		}
		else if (error instanceof SocketTimeoutException)
		{
			errorCodeReason = getLabel(ERROR_SOCKET_TIME_OUT);
		}
		else
		{
			errorCodeReason = getLabel(ERROR_DEFAULT);
			LOG.error(error.getMessage(), error);
		}

		final String errorMessage = getLabel(PING_WEBHOOK_CONFIG_FAILURE, new String[]
				{ getDestinationUrl(), errorCodeReason });

		handleErrorMessage(errorMessage);
	}

	private String getDestinationUrl()
	{
		return webhookConfiguration.getDestination() != null ? webhookConfiguration.getDestination().getUrl() : "";
	}

	private void setDestinationPath()
	{
		webhookbackofficeJsonTextDestinationPathBox.setValue(getDestinationUrl());
		webhookbackofficeIODestinationPathBox.setValue(getDestinationUrl());
	}
}
