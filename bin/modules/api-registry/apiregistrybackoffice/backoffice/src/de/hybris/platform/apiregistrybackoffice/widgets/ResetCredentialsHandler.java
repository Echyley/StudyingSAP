/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistrybackoffice.widgets;

import static org.zkoss.util.resource.Labels.getLabel;

import de.hybris.platform.apiregistrybackoffice.constants.ApiregistrybackofficeConstants;
import de.hybris.platform.apiregistrybackoffice.data.ApiRegistryResetCredentialsForm;
import de.hybris.platform.apiregistryservices.exceptions.ApiRegistrationException;
import de.hybris.platform.apiregistryservices.model.AbstractDestinationModel;
import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel;
import de.hybris.platform.apiregistryservices.model.ExposedOAuthCredentialModel;
import de.hybris.platform.apiregistryservices.services.ApiRegistrationService;
import de.hybris.platform.apiregistryservices.services.CredentialService;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.webservicescommons.model.OAuthClientDetailsModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.cockpitng.config.jaxb.wizard.CustomType;
import com.hybris.cockpitng.util.notifications.NotificationService;
import com.hybris.cockpitng.util.notifications.event.NotificationEvent;
import com.hybris.cockpitng.widgets.configurableflow.ConfigurableFlowController;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandler;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandlerAdapter;


/**
 * Handler for resolve Reset Credentials wizard.
 */
public class ResetCredentialsHandler implements FlowActionHandler
{
	public static final String NEW_CLIENT_ACTIVE_GRACE_PERIOD = "apiregistry_backoffice_resetCredentialsForm.newClientActiveGrace";
	public static final String NEW_CLIENT_ACTIVE = "apiregistry_backoffice_resetCredentialsForm.newClientActive";
	public static final String INVALID_CLIENT_ID = "apiregistry_backoffice_resetCredentialsForm.invalidClientId";
	public static final String SOME_DESTINATIONS_FAILED = "apiregistry_backoffice_resetCredentialsForm.someDestinationsFailed";
	public static final String ALL_DESTINATIONS_SUCCESS = "apiregistry_backoffice_resetCredentialsForm.allDestinationsRegistered";
	public static final String NO_DESTINATIONS_FOR_OAUTH_CLIENT = "apiregistry_backoffice_resetCredentialsForm.noDestinationsForOauthClient";
	public static final String NO_DETAILS = "apiregistry_backoffice_resetCredentialsForm.noOAuthDetails";
	public static final String REGISTRATION_FAILED_WEBSERVICES_DELIMITER = ", ";


	private static final Logger LOG = Logger.getLogger(ResetCredentialsHandler.class.getName());

	private NotificationService notificationService;

	private ApiRegistrationService apiRegistrationService;

	private CredentialService credentialService;

	private DestinationService<AbstractDestinationModel> destinationService;

	private FlexibleSearchService flexibleSearchService;

	@Override
	public void perform(final CustomType customType, final FlowActionHandlerAdapter adapter, final Map<String, String> parameters)
	{
		final ApiRegistryResetCredentialsForm resetCredentialsForm;
		resetCredentialsForm = adapter.getWidgetInstanceManager().getModel().getValue("apiregistry_backoffice_resetCredentialsForm",
				ApiRegistryResetCredentialsForm.class);

		final ExposedOAuthCredentialModel credential = resetCredentialsForm.getCredential();

		final ConfigurableFlowController controller = (ConfigurableFlowController) adapter.getWidgetInstanceManager()
				.getWidgetslot().getAttribute("widgetController");

		if (credentialHasEmptyOAuth(credential, controller, adapter))
		{
			return;
		}

		final String oldClientId = credential.getOAuthClientDetails().getClientId();
		if(stepOne(controller, oldClientId, resetCredentialsForm, adapter)) {
			return;
		}
		if(stepTwo(controller, adapter)) {
			return;
		}
		if(notifyUser(resetCredentialsForm,adapter)) {
			return;
		}
		if(stepThree(controller, resetCredentialsForm)) {
			return;
		}

		convertGracePeriod(adapter,oldClientId, resetCredentialsForm);
		adapter.done();
	}

	private boolean credentialHasEmptyOAuth(final ExposedOAuthCredentialModel credential,
			final ConfigurableFlowController controller, final FlowActionHandlerAdapter adapter)
	{
		// happens if we select credentials with empty oauth
		if (credential.getOAuthClientDetails() == null)
		{
			controller.getRenderer().refreshView();
			getNotificationService().notifyUser(adapter.getWidgetInstanceManager(), ApiregistrybackofficeConstants.NOTIFICATION_TYPE,
					NotificationEvent.Level.WARNING, getLabel(NO_DETAILS));
			return true;
		}
		else
		{
			return false;
		}
	}

	private boolean stepOne (final ConfigurableFlowController controller, final String oldClientId, final ApiRegistryResetCredentialsForm resetCredentialsForm, final FlowActionHandlerAdapter adapter) {
		if ("step1".equals(controller.getCurrentStep().getId()))
		{
			final List<ExposedDestinationModel> impactedDestinations = getDestinationService()
					.getActiveExposedDestinationsByClientId(oldClientId);
			final List<ExposedOAuthCredentialModel> impactedCredentials = getCredentialService()
					.getCredentialsByClientId(oldClientId);
			resetCredentialsForm.setImpactedDestinations(impactedDestinations);
			resetCredentialsForm.setImpactedCredentials(impactedCredentials);
			adapter.getWidgetInstanceManager().getModel().setValue("apiregistry_backoffice_resetCredentialsForm",
					resetCredentialsForm);
			adapter.next();
			if (impactedCredentials.size() + impactedDestinations.size() == 1)
			{
				adapter.next();
			}
			return true;
		}
		else {
			return false;
		}
	}

	private boolean stepTwo(final ConfigurableFlowController controller, final FlowActionHandlerAdapter adapter) {
		if ("step2".equals(controller.getCurrentStep().getId()))
		{
			adapter.next();
			return true;
		}
		else {
			return false;
		}
	}
	private boolean stepThree(final ConfigurableFlowController controller, final ApiRegistryResetCredentialsForm resetCredentialsForm) {
		// happens if we are on 3rd step and selected credentials were already flushed
		if (resetCredentialsForm.getImpactedCredentials().stream()
		                        .anyMatch(exposedOAuthCredentialModel -> exposedOAuthCredentialModel.getOAuthClientDetails() == null))
		{
			controller.getRenderer().refreshView();
			getNotificationService().notifyUser((String) null, ApiregistrybackofficeConstants.NOTIFICATION_TYPE,
					NotificationEvent.Level.WARNING, getLabel(NO_DETAILS));
			return true;
		}
		else {
			return false;
		}
	}

	private void convertGracePeriod(final FlowActionHandlerAdapter adapter,final String oldClientId, final ApiRegistryResetCredentialsForm resetCredentialsForm) {
		//convert grace period from seconds to milliseconds
		final Integer gracePeriod = resetCredentialsForm.getGracePeriod() * 1000;
		getCredentialService().resetCredentials(resetCredentialsForm.getImpactedCredentials(), resetCredentialsForm.getClientId(),
				resetCredentialsForm.getClientSecret(), gracePeriod);

		final String credentialMessage;
		if (resetCredentialsForm.getGracePeriod() > 0)
		{
			credentialMessage = getLabel(NEW_CLIENT_ACTIVE_GRACE_PERIOD, new String[]
					{ resetCredentialsForm.getClientId(), String.valueOf(resetCredentialsForm.getGracePeriod()) });
		}
		else
		{
			credentialMessage = getLabel(NEW_CLIENT_ACTIVE, new String[]
					{ resetCredentialsForm.getClientId() });
		}
		if (CollectionUtils.isEmpty(resetCredentialsForm.getImpactedDestinations()))
		{
			getNotificationService().notifyUser(adapter.getWidgetInstanceManager(), ApiregistrybackofficeConstants.NOTIFICATION_TYPE,
					NotificationEvent.Level.SUCCESS, credentialMessage + " " + getLabel(NO_DESTINATIONS_FOR_OAUTH_CLIENT, new String[]
							{ oldClientId }));
		}
		else
		{
			final List<String> failedDestinations = registerDestinations(resetCredentialsForm.getImpactedDestinations());

			if (CollectionUtils.isEmpty(failedDestinations))
			{
				getNotificationService().notifyUser(adapter.getWidgetInstanceManager(),
						ApiregistrybackofficeConstants.NOTIFICATION_TYPE,
						NotificationEvent.Level.SUCCESS, credentialMessage + " " + getLabel(ALL_DESTINATIONS_SUCCESS));
			}
			else
			{
				getNotificationService().notifyUser(adapter.getWidgetInstanceManager(),
						ApiregistrybackofficeConstants.NOTIFICATION_TYPE,
						NotificationEvent.Level.FAILURE, credentialMessage + " " + getLabel(SOME_DESTINATIONS_FAILED, new String[]
								{ String.join(REGISTRATION_FAILED_WEBSERVICES_DELIMITER, failedDestinations) }));
				return;
			}
		}
	}

	private boolean notifyUser(final ApiRegistryResetCredentialsForm resetCredentialsForm, final FlowActionHandlerAdapter adapter) {
		final OAuthClientDetailsModel existingClient = new OAuthClientDetailsModel();
		existingClient.setClientId(resetCredentialsForm.getClientId());
		final List<OAuthClientDetailsModel> modelsByExample = getFlexibleSearchService().getModelsByExample(existingClient);
		if (CollectionUtils.isNotEmpty(modelsByExample))
		{
			getNotificationService().notifyUser(adapter.getWidgetInstanceManager(), ApiregistrybackofficeConstants.NOTIFICATION_TYPE,
					NotificationEvent.Level.FAILURE, getLabel(INVALID_CLIENT_ID, new String[]
							{ existingClient.getClientId() }));
			return true;
		}
		else {
			return false;
		}
	}

	private List<String> registerDestinations(final List<ExposedDestinationModel> exposedDestinations)
	{
		final List<String> registrationFailedDestinations = new ArrayList<>();

		for (final ExposedDestinationModel exposedDestination : exposedDestinations)
		{
			try
			{
				getApiRegistrationService().registerExposedDestination(exposedDestination);
			}
			catch (final ApiRegistrationException e)
			{
				LOG.warn("Could not register exposed destination", e);
				registrationFailedDestinations.add(exposedDestination.getId());
			}
		}

		return registrationFailedDestinations;
	}

	protected NotificationService getNotificationService()
	{
		return notificationService;
	}

	@Required
	public void setNotificationService(final NotificationService notificationService)
	{
		this.notificationService = notificationService;
	}

	protected ApiRegistrationService getApiRegistrationService()
	{
		return apiRegistrationService;
	}

	@Required
	public void setApiRegistrationService(final ApiRegistrationService apiRegistrationService)
	{
		this.apiRegistrationService = apiRegistrationService;
	}

	protected CredentialService getCredentialService()
	{
		return credentialService;
	}

	@Required
	public void setCredentialService(final CredentialService credentialService)
	{
		this.credentialService = credentialService;
	}

	protected DestinationService<AbstractDestinationModel> getDestinationService()
	{
		return destinationService;
	}

	@Required
	public void setDestinationService(final DestinationService<AbstractDestinationModel> destinationService)
	{
		this.destinationService = destinationService;
	}

	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}
}
