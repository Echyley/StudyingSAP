/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookbackoffice.widgets.modals.controllers;

import static de.hybris.platform.apiregistrybackoffice.constants.ApiregistrybackofficeConstants.NOTIFICATION_TYPE;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorUtils.createComboItem;
import static de.hybris.platform.webhookbackoffice.constants.WebhookbackofficeConstants.OBJECTS_UPDATED_EVENT;
import static de.hybris.platform.webhookbackoffice.constants.WebhookbackofficeConstants.OBJECTS_UPDATED_EVENT_TYPE;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.scripting.model.ScriptModel;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.webhookbackoffice.services.WebhookConfigBackofficeService;
import de.hybris.platform.webhookservices.event.WebhookEvent;
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel;
import de.hybris.platform.webhookservices.exceptions.WebhookConfigEventNotSupportedException;
import de.hybris.platform.webhookservices.exceptions.WebhookConfigNoEventConfigException;
import de.hybris.platform.webhookservices.exceptions.WebhookConfigNoEventTypeException;
import de.hybris.platform.webhookservices.exceptions.WebhookConfigNotRegisteredEventException;
import de.hybris.platform.webhookservices.exceptions.WebhookConfigNotValidLocationException;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Messagebox;

import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent.Level;
import com.hybris.cockpitng.annotations.SocketEvent;
import com.hybris.cockpitng.annotations.ViewEvent;
import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.core.events.CockpitEventQueue;
import com.hybris.cockpitng.core.events.impl.DefaultCockpitEvent;
import com.hybris.cockpitng.editor.defaultreferenceeditor.DefaultReferenceEditor;
import com.hybris.cockpitng.util.DefaultWidgetController;
import com.hybris.cockpitng.util.notifications.NotificationService;
import static de.hybris.platform.webhookbackoffice.services.WebhookConfigBackofficeService.DEFAULT_EVENT_TYPE;

public class CreateWebhookConfigurationModalController extends DefaultWidgetController
{
	private static final Logger LOG = Log.getLogger(CreateWebhookConfigurationModalController.class);
	private static final String LOGIC_LOCATION_TEMPLATE = "model://%s";
	private static final String NOT_SHOWED_WEBHOOK_EVENT_NAME = "WebhookEvent";

	@WireVariable
	protected transient ReadService readService;
	@WireVariable
	protected transient WebhookConfigBackofficeService webhookConfigBackofficeService;
	@WireVariable
	protected transient CockpitEventQueue cockpitEventQueue;
	@WireVariable
	protected transient NotificationService notificationService;

	protected Combobox integrationObjectComboBox;
	protected Editor consumedDestinationEditor;
	protected Combobox filterLocationComboBox;
	protected Combobox eventTypesComboBox;
	protected Button createButton;

	@SocketEvent(socketId = "createWebhookConfiguration")
	public void loadCreateWebhookConfigurationModal(final String openModal)
	{
		loadIntegrationObject();
		loadFilterLocations();
		loadItemEventTypes();
		createButton.setDisabled(true);

		integrationObjectComboBox.setAutocomplete(false);
		integrationObjectComboBox.setAutodrop(true);

		((DefaultReferenceEditor<?>) consumedDestinationEditor.getEditorRenderer()).setParentObject(
				new WebhookConfigurationModel());
	}

	@ViewEvent(componentID = "integrationObjectComboBox", eventName = Events.ON_CHANGE)
	public void integrationObjectComboBoxOnChange()
	{
		setCreateButtonStatus();
	}

	@ViewEvent(componentID = "consumedDestinationEditor", eventName = Editor.ON_VALUE_CHANGED)
	public void consumedDestinationEditorOnChange()
	{
		setCreateButtonStatus();
	}

	@ViewEvent(componentID = "createButton", eventName = Events.ON_CLICK)
	public void createWebhookConfiguration()
	{
		final IntegrationObjectModel integrationObject = integrationObjectComboBox.getSelectedItem().getValue();

		final Object consumedDestinationObject = consumedDestinationEditor.getValue();

		final ConsumedDestinationModel consumedDestination = (ConsumedDestinationModel) consumedDestinationObject;

		final String filterLocation = filterLocationComboBox.getSelectedItem() != null ?
				filterLocationComboBox.getSelectedItem().getLabel() : "";

		final String eventType = eventTypesComboBox.getSelectedItem() != null ? eventTypesComboBox.getSelectedItem()
		                                                                                          .getValue() : DEFAULT_EVENT_TYPE;

		final List<WebhookConfigurationModel> webhookList = webhookConfigBackofficeService.getWebhookConfiguration(
				integrationObject, consumedDestination, eventType);

		if (CollectionUtils.isNotEmpty(webhookList))
		{
			showErrorMessageBox("webhookbackoffice.createWebhookConfiguration.error.msg.webhookExists",
					"webhookbackoffice.createWebhookConfiguration.error.title.webhookExists");
			LOG.error(getLabel("webhookbackoffice.createWebhookConfiguration.error.title.webhookExists"));
		}
		else
		{
			final WebhookConfigurationModel newWebhook = createWebhookConfiguration(integrationObject, consumedDestination,
					filterLocation, eventType).orElse(null);

			if (newWebhook != null)
			{
				publishEvent(newWebhook);
				sendOutput("cancel", null);
				notificationService.notifyUser(Strings.EMPTY, NOTIFICATION_TYPE, Level.SUCCESS,
						getLabel("webhookbackoffice.createWebhookConfiguration.info.msg.webhookConfigCreated"));
			}
		}

	}

	/**
	 * return error message in resource bundle from exception's className.
	 *
	 * @param exception An exception, subclass of InterceptionException including {@link WebhookConfigEventNotSupportedException}
	 *                  {@link WebhookConfigNoEventTypeException} {@link WebhookConfigNoEventConfigException}
	 *                  {@link WebhookConfigNotRegisteredEventException} {@link WebhookConfigNotValidLocationException}
	 * @return error message
	 */
	public String convertExceptionToResourceMsg(final InterceptorException exception)
	{
		final String errorMessageFromExceptionPrefix = "webhookbackoffice.createWebhookConfiguration.error.msg.";
		return getLabel(errorMessageFromExceptionPrefix + exception.getClass().getSimpleName());
	}

	/**
	 * return error message title in resource bundle from exception's className.
	 *
	 * @param exception An exception, subclass of InterceptionException including {@link WebhookConfigEventNotSupportedException}
	 *                  {@link WebhookConfigNoEventTypeException} {@link WebhookConfigNoEventConfigException}
	 *                  {@link WebhookConfigNotRegisteredEventException} {@link WebhookConfigNotValidLocationException}
	 * @return error message title
	 */
	public String convertExceptionToResourceTitle(final InterceptorException exception)
	{
		final String errorMessageFromExceptionPrefix = "webhookbackoffice.createWebhookConfiguration.error.title.";
		return getLabel(errorMessageFromExceptionPrefix + exception.getClass().getSimpleName());
	}

	protected void showErrorMessageBox(final String msgKey, final String titleKey)
	{
		Messagebox.show(this.getLabel(msgKey), this.getLabel(titleKey), 1, Messagebox.ERROR);
	}

	private Comboitem getDefaultEventTypeCombobox()
	{
		return eventTypesComboBox.getItems()
		                         .stream()
		                         .filter(item -> DEFAULT_EVENT_TYPE.equals(item.getValue()))
		                         .findFirst()
		                         .orElse(null);
	}

	private void setCreateButtonStatus()
	{
		if (integrationObjectComboBox.getSelectedItem() != null && consumedDestinationEditor.getValue() != null)
		{
			createButton.setDisabled(false);
			return;
		}
		createButton.setDisabled(true);
	}

	private void loadIntegrationObject()
	{
		integrationObjectComboBox.getItems().clear();
		final List<IntegrationObjectModel> unsortedIntegrationObjectModels = readService.getIntegrationObjectModels();
		unsortedIntegrationObjectModels.stream()
		                               .sorted(Comparator.comparing(IntegrationObjectModel::getCode))
		                               .forEach(iO -> integrationObjectComboBox.appendChild(createComboItem(iO.getCode(), iO)));
	}

	private void loadFilterLocations()
	{
		filterLocationComboBox.getItems().clear();
		final List<ScriptModel> filterLocations = webhookConfigBackofficeService.getActiveGroovyScripts();
		filterLocations.stream()
		               .sorted(Comparator.comparing(ScriptModel::getCode))
		               .forEach(filterLocation -> filterLocationComboBox.appendChild(
				               createComboItem(String.format(LOGIC_LOCATION_TEMPLATE, filterLocation.getCode()),
						               filterLocation)));
	}

	private void loadItemEventTypes()
	{
		eventTypesComboBox.getItems().clear();

		getWebhookItemEventTypes()
				.stream()
				.forEach(et ->
						eventTypesComboBox.appendChild(
								createComboItem(getLabel(et), et)
						)
				);

		eventTypesComboBox.setSelectedItem(getDefaultEventTypeCombobox());
	}

	private List<String> getWebhookItemEventTypes()
	{
		final Reflections reflections = new Reflections(WebhookEvent.class);

		final var subClasses = reflections.getSubTypesOf(WebhookEvent.class);
		return subClasses.stream()
		                 .filter(event -> !event.getSimpleName().contains(NOT_SHOWED_WEBHOOK_EVENT_NAME))
		                 .map(Class::getCanonicalName)
		                 .collect(Collectors.toList());
	}

	private void publishEvent(final WebhookConfigurationModel webhook)
	{
		final DefaultCockpitEvent cockpitEvent = new DefaultCockpitEvent(OBJECTS_UPDATED_EVENT, webhook, null);
		cockpitEvent.getContext().put(OBJECTS_UPDATED_EVENT_TYPE, true);
		cockpitEventQueue.publishEvent(cockpitEvent);
	}

	private Optional<WebhookConfigurationModel> createWebhookConfiguration(final IntegrationObjectModel integrationObject,
	                                                                       final ConsumedDestinationModel consumedDestination,
	                                                                       final String filterLocation,
	                                                                       final String eventType)
	{
		try
		{
			return Optional.of(persistWebhookConfiguration(integrationObject, consumedDestination, filterLocation, eventType));
		}
		catch (final ModelSavingException modelSavingException)
		{
			if (modelSavingException.getCause() instanceof InterceptorException)
			{
				final String errorTitle = convertExceptionToResourceTitle(
						(InterceptorException) modelSavingException.getCause());
				final String errorMsg = convertExceptionToResourceMsg(
						(InterceptorException) modelSavingException.getCause());
				if (errorTitle != null || errorMsg != null)
				{
					Messagebox.show(errorMsg, errorTitle, 1, Messagebox.ERROR);
					LOG.error(errorMsg);
					return Optional.empty();
				}
			}
			showErrorMessageBox("webhookbackoffice.createWebhookConfiguration.error.msg.creationFailed",
					"webhookbackoffice.createWebhookConfiguration.error.title.creationFailed");
		}
		return Optional.empty();
	}

	private WebhookConfigurationModel persistWebhookConfiguration(final IntegrationObjectModel integrationObject,
	                                                              final ConsumedDestinationModel consumedDestination,
	                                                              final String filterLocation,
	                                                              final String eventType)
	{
		return webhookConfigBackofficeService.createWebhookConfiguration(integrationObject, consumedDestination, filterLocation,
				eventType);
	}
}
