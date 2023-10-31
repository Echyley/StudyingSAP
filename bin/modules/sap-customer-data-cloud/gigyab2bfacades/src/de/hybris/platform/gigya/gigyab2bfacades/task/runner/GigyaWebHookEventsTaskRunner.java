/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyab2bfacades.task.runner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.gigya.socialize.GSKeyNotFoundException;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.gigya.gigyab2bfacades.constants.Gigyab2bfacadesConstants;
import de.hybris.platform.gigya.gigyab2bfacades.data.GigyaWebHookEvent;
import de.hybris.platform.gigya.gigyab2bfacades.data.GigyaWebHookEventData;
import de.hybris.platform.gigya.gigyab2bfacades.data.GigyaWebHookRequest;
import de.hybris.platform.gigya.gigyafacades.login.GigyaLoginFacade;
import de.hybris.platform.gigya.gigyaservices.api.exception.GigyaApiException;
import de.hybris.platform.gigya.gigyaservices.login.GigyaLoginService;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskRunner;
import de.hybris.platform.task.TaskService;

/**
 * This class is used to process CDC Webhook Events data and create / update B2B
 * users in Commerce
 */
public class GigyaWebHookEventsTaskRunner implements TaskRunner<TaskModel> {
	private static final Logger LOG = LoggerFactory.getLogger(GigyaWebHookEventsTaskRunner.class);

	private GigyaLoginFacade gigyaLoginFacade;

	private GigyaLoginService gigyaLoginService;

	private BaseSiteService baseSiteService;

	@Override
	public void run(final TaskService taskService, final TaskModel taskModel) {
		if (taskModel.getContext() instanceof GigyaWebHookRequest) {
			final var gigyaWebHookRequest = ((GigyaWebHookRequest) taskModel.getContext());
			processGigyaWebHookEvents(gigyaWebHookRequest);

		}
	}

	@Override
	public void handleError(final TaskService taskService, final TaskModel taskModel, final Throwable error) {
		LOG.error("Error while processing CDC Webhook events ", error);
	}

	protected void processGigyaWebHookEvents(GigyaWebHookRequest gigyaWebHookRequest) {
		LOG.debug("Gigya Webhook Event(s) task begins execution.");
		 Map<String, Set<String>> uniqueUIDEventsMap;
		try {
			final String baseSiteId = gigyaWebHookRequest.getBaseSiteId();
			var gigyaConfig = getGigyaConfigModelFromBaseSiteId(baseSiteId);

			uniqueUIDEventsMap = getUniqueUidsFromWebhookEvents(gigyaWebHookRequest.getEvents());

			// No uids were found in the webhook event.
			if (uniqueUIDEventsMap.isEmpty()) {
				LOG.debug("No user uid found in Gigya WebHook request.");
			}

			// Create a customer if not exists otherwise update the user.
			createOrUpdateCustomer(uniqueUIDEventsMap, gigyaConfig);

		} catch (final GigyaApiException e) {
			LOG.error("Error procesing CDC Webhook events. ", e);
		}
		LOG.debug("Gigya Webhook Event(s) task completed.");
	}

	/**
	 * Get the unique uids from the incoming webhook events
	 * 
	 * @param webhookEvents List of webhook events
	 * @returns Map<String, Set<String>> Map of unique UIDs and the associated event ids in the Webhook request
	 */
	protected Map<String, Set<String>> getUniqueUidsFromWebhookEvents(List<GigyaWebHookEvent> webhookEvents) {
		final Set<String> targetWebhookEvents = Set.of(Gigyab2bfacadesConstants.ACCOUNT_CREATED_EVENT,
				Gigyab2bfacadesConstants.ACCOUNT_REGISTERED_EVENT, Gigyab2bfacadesConstants.ACCOUNT_UPDATED_EVENT);
		// Obtain a set of unique UIDs from the events
		Map<String, Set<String>> uniqueUIDEventsMap = new HashMap<>();
		if (CollectionUtils.isEmpty(webhookEvents)) {
			return uniqueUIDEventsMap;
		}
		for (GigyaWebHookEvent event : webhookEvents) {
			if (event.getType() != null && targetWebhookEvents.contains(event.getType())) { // only select needed events
				GigyaWebHookEventData data = event.getData();
				if (!StringUtils.isEmpty(data.getUid())) {
					uniqueUIDEventsMap.computeIfAbsent(data.getUid(), key -> new HashSet<>()).add(event.getId());
				}
			}
		}
		return uniqueUIDEventsMap;
	}

	/**
	 * Create or Update Customer from the unique uids
	 * 
	 * @param uniqueUIDs  Set of Unique Gigya UIDs present in the webhook request
	 * @param gigyaConfig Gigya Config Model for the base site
	 * @return boolean, true if the user(s) is created or updated successfully
	 */
	protected void createOrUpdateCustomer(Map<String, Set<String>> uniqueUIDEventsMap, GigyaConfigModel gigyaConfig) {
		for (Map.Entry<String, Set<String>> uidEntry : uniqueUIDEventsMap.entrySet()) {
			try {
				String uid = uidEntry.getKey();
				final UserModel gigyaUser = getGigyaLoginService().findCustomerByGigyaUid(uid);
				if (gigyaUser != null) {
					getGigyaLoginFacade().updateUser(gigyaConfig, gigyaUser);
				} else {
					// On creation all customer data from gigya is synched
					getGigyaLoginFacade().createNewCustomer(gigyaConfig, uid);
				}
			} catch (final DuplicateUidException e) {
				LOG.error("Error duplicate ID found for gigyaUid = {}; webhook ids: {}; Exception ", uidEntry.getKey(), uidEntry.getValue(), e);
			} catch (final GigyaApiException | GSKeyNotFoundException e) {
				LOG.error("Error updating user information for user with gigyaUid = {}; webhook ids: {}; Exception ", uidEntry.getKey(), uidEntry.getValue(), e);
			}
		}
	}

	/**
	 * Obtain the GigyaConfigModel from the base site using the base site id
	 * 
	 * @param baseSiteId Base site identifier
	 * @return GigyaConfigModel
	 */
	GigyaConfigModel getGigyaConfigModelFromBaseSiteId(String baseSiteId) {
		// Set the base site for the current request
		getBaseSiteService().setCurrentBaseSite(baseSiteId, true);
		final var cmsSiteModel = (CMSSiteModel) getBaseSiteService().getCurrentBaseSite();
		return cmsSiteModel.getGigyaConfig();
	}

	public GigyaLoginService getGigyaLoginService() {
		return gigyaLoginService;
	}

	public void setGigyaLoginService(GigyaLoginService gigyaLoginService) {
		this.gigyaLoginService = gigyaLoginService;
	}

	public GigyaLoginFacade getGigyaLoginFacade() {
		return gigyaLoginFacade;
	}

	public void setGigyaLoginFacade(GigyaLoginFacade gigyaLoginFacade) {
		this.gigyaLoginFacade = gigyaLoginFacade;
	}

	public BaseSiteService getBaseSiteService() {
		return baseSiteService;
	}

	public void setBaseSiteService(BaseSiteService baseSiteService) {
		this.baseSiteService = baseSiteService;
	}
}
