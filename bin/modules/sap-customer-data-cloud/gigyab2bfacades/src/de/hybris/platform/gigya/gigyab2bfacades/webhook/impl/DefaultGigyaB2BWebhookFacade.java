/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyab2bfacades.webhook.impl;

import java.time.Instant;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.gigya.gigyab2bfacades.data.GigyaWebHookRequest;
import de.hybris.platform.gigya.gigyab2bfacades.webhook.GigyaB2BWebhookFacade;
import de.hybris.platform.gigya.gigyaservices.login.GigyaLoginService;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;

/**
 * 
 * Gigya B2B Webhook Facade that handles verifies the JWT token sent by CDC webhook events and consumes
 * the payload to create or update B2B users in near real-time.
 */
public class DefaultGigyaB2BWebhookFacade implements GigyaB2BWebhookFacade {
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultGigyaB2BWebhookFacade.class);

	private BaseSiteService baseSiteService;

	private String gigyaWebHookEventsTaskRunnerBean;
	
	private GigyaLoginService gigyaLoginService;
	
	private TaskService taskService;
	
	private ModelService modelService;
	
	
	/**
	 * Processes the Gigya Webhook events and creates / updates users
	 * 
	 * @param jwtToken       The JWT Token for validation
	 * @param webhookRequest The Webhook Request payload
	 * @param gigyaConfig    Gigya Config Model for the base site
	 * @return boolean, true if the events are processed sucessfully
	 */
	@Override
	public void receiveGigyaWebHookEvents(final GigyaWebHookRequest webhookRequest) {
		LOG.info("Gigya Webhook Event(s) received.");
		scheduledWebHookEventsProcessing(webhookRequest);
		LOG.info("Gigya Webhook Event(s) task scheduled for execution.");
	}

	/**
	 * Schedule a task to process the CDC Webhook Request
	 * 
	 * @param gigyaWebHookRequest
	 */
	protected void scheduledWebHookEventsProcessing(final GigyaWebHookRequest gigyaWebHookRequest) {
		final TaskModel task = getModelService().create(TaskModel.class);
		task.setRunnerBean(gigyaWebHookEventsTaskRunnerBean);
		task.setExecutionDate(Date.from(Instant.now()));
		task.setContext(gigyaWebHookRequest);

		getTaskService().scheduleTask(task);
	}

	/**
	 * Validate the JWT Token for CDC
	 * 
	 * @param jwtToken Gigya JWT Token
	 * @return boolean, true if the JWT Token is valid
	 */
	@Override
	public boolean validateWebHookJWTToken(final String jwtToken, final String baseSiteId, final String payloadHash) {
		var jwtValid = false;
		if (StringUtils.isBlank(jwtToken)) {
			LOG.error("Gigya JWT Token is empty, Cannot verify.");
			return false;
		}
		if (StringUtils.isBlank(payloadHash)) {
			LOG.error("Gigya Webhook Payload Hash is empty, Cannot verify.");
			return false;
		}
		var gigyaConfig = getGigyaConfigModelFromBaseSiteId(baseSiteId);
		jwtValid = getGigyaLoginService().verifyGigyaCallIdTokenExpiryAndSignature(gigyaConfig, jwtToken)
				&& getGigyaLoginService().verifyGigyaIdTokenContainsPayloadHash(jwtToken, payloadHash);
		if (!jwtValid) {
			LOG.error("Gigya JWT Token is invalid, Verification failed.");
			return false;
		}
		return jwtValid;
	}
	
	
	/**
	 * Obtain the GigyaConfigModel from the base site using the base site id
	 * 
	 * @param baseSiteId Base site identifier
	 * @return GigyaConfigModel
	 */
	protected GigyaConfigModel getGigyaConfigModelFromBaseSiteId(String baseSiteId) {
		// Set the base site for the current request
		baseSiteService.setCurrentBaseSite(baseSiteId, true);
		final var cmsSiteModel = (CMSSiteModel) getBaseSiteService().getCurrentBaseSite();
		return cmsSiteModel.getGigyaConfig();
	}
	
	public BaseSiteService getBaseSiteService() {
		return baseSiteService;
	}

	public void setBaseSiteService(BaseSiteService baseSiteService) {
		this.baseSiteService = baseSiteService;
	}
	
	public GigyaLoginService getGigyaLoginService() {
		return gigyaLoginService;
	}

	public void setGigyaLoginService(GigyaLoginService gigyaLoginService) {
		this.gigyaLoginService = gigyaLoginService;
	}
	
	public TaskService getTaskService() {
		return taskService;
	}

	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}
	
	public ModelService getModelService() {
		return modelService;
	}

	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}

	public String getGigyaWebHookEventsTaskRunnerBean() {
		return gigyaWebHookEventsTaskRunnerBean;
	}

	public void setGigyaWebHookEventsTaskRunnerBean(String gigyaWebHookEventsTaskRunnerBean) {
		this.gigyaWebHookEventsTaskRunnerBean = gigyaWebHookEventsTaskRunnerBean;
	}
}
