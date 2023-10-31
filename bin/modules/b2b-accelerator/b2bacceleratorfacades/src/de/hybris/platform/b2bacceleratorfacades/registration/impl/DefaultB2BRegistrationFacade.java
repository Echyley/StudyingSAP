/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorfacades.registration.impl;

import de.hybris.bootstrap.util.LocaleHelper;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BRegistrationModel;
import de.hybris.platform.b2bacceleratorfacades.exception.CustomerAlreadyExistsException;
import de.hybris.platform.b2bacceleratorfacades.exception.RegistrationNotEnabledException;
import de.hybris.platform.b2bacceleratorfacades.registration.B2BRegistrationFacade;
import de.hybris.platform.b2bacceleratorfacades.registration.B2BRegistrationWorkflowFacade;
import de.hybris.platform.b2bcommercefacades.data.B2BRegistrationData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.workflow.WorkflowTemplateService;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link B2BRegistrationFacade}
 */
public class DefaultB2BRegistrationFacade implements B2BRegistrationFacade
{

	private static final Logger LOG = Logger.getLogger(DefaultB2BRegistrationFacade.class);

	private BaseSiteService baseSiteService;

	private CommonI18NService commonI18NService;

	private ModelService modelService;

	private BaseStoreService baseStoreService;

	private UserService userService;

	private B2BRegistrationWorkflowFacade b2bRegistrationWorkflowFacade;

	private WorkflowTemplateService workflowTemplateService;

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	/**
	 * @param baseSiteService
	 *           the cmsSiteService to set
	 */
	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	/**
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * @param b2bRegistrationWorkflowFacade
	 *           the b2bRegistrationWorkflowFacade to set
	 */
	@Required
	public void setB2bRegistrationWorkflowFacade(final B2BRegistrationWorkflowFacade b2bRegistrationWorkflowFacade)
	{
		this.b2bRegistrationWorkflowFacade = b2bRegistrationWorkflowFacade;
	}

	/**
	 * @param workflowTemplateService
	 *           the workflowTemplateService to set
	 */
	@Required
	public void setWorkflowTemplateService(final WorkflowTemplateService workflowTemplateService)
	{
		this.workflowTemplateService = workflowTemplateService;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.b2bcommercefacades.registration.B2BRegistrationFacade#register(de.hybris.platform.b2bcommercefacades.data.B2BRegistrationData)
	 */
	@Override
	public void register(final B2BRegistrationData data) throws CustomerAlreadyExistsException, RegistrationNotEnabledException
	{
		final boolean isRegistrationEnabled = baseSiteService.getCurrentBaseSite().isEnableRegistration();
		if (!isRegistrationEnabled)
		{
			LOG.debug("Registration is not enabled!");
			throw new RegistrationNotEnabledException("Registration is not enabled!");
		}

		final Transaction tx = Transaction.current();
		tx.begin();

		boolean success = false;

		try
		{
			// Check if a user using the same email exist, if so we need to abort the current operation!
			final boolean userExists = userService.isUserExisting(data.getEmail().toLowerCase(LocaleHelper.getPersistenceLocale()));
			if (userExists)
			{
				LOG.debug(String.format("user with uid '%s' already exists!",
						data.getEmail().toLowerCase(LocaleHelper.getPersistenceLocale())));
				throw new CustomerAlreadyExistsException(String.format("User with uid '%s' already exists!",
						data.getEmail().toLowerCase(LocaleHelper.getPersistenceLocale())));
			}

			// Save the customer. At this point, the customer is saved to generate emails and initiate the workflow. This customer will be deleted as part of the
			// cleanup of the workflow IF he is rejected. If approved, the customer will be deleted by the "approve workflow action" and will be re-created
			// as a B2BCustomer and assigned to the proper B2BUnit. At this stage, we can't create a B2BCustomer since we don't even have a B2BUnit (organization!).

			// Save the registration model so that it is accessible to the workflow actions. The registration model will be deleted as part of the cleanup
			// of the workflow.
			final CustomerModel customer = toCustomerModel(data);
			final B2BRegistrationModel registration = toRegistrationModel(data);
			registration.setCustomer(customer);
			modelService.saveAll(customer, registration);

			final WorkflowTemplateModel workflowTemplate = workflowTemplateService.getWorkflowTemplateForCode(
					B2BConstants.Workflows.REGISTRATION_WORKFLOW);

			LOG.debug(String.format("Created WorkflowTemplateModell using name '%s'", B2BConstants.Workflows.REGISTRATION_WORKFLOW));

			// Start the workflow
			b2bRegistrationWorkflowFacade.launchWorkflow(workflowTemplate, registration);

			tx.commit();
			success = true;

		}
		finally
		{
			if (!success)
			{
				tx.rollback();
			}
		}

	}

	/**
	 * Converts a {@link B2BRegistrationData} into a {@link CustomerModel}. Only keeps the most important fields to
	 * generate emails, the rest is ignored as this customer is to be deleted as part of the workflow execution
	 *
	 * @param data
	 *           The registration data
	 * @return An unsaved instance of {@link CustomerModel}
	 */
	protected CustomerModel toCustomerModel(final B2BRegistrationData data)
	{

		final CustomerModel model = modelService.create(CustomerModel.class);

		model.setName(WordUtils.capitalizeFully(data.getName()));
		model.setUid(data.getEmail());
		model.setLoginDisabled(true);
		model.setCustomerID(UUID.randomUUID().toString());
		model.setSessionLanguage(commonI18NService.getCurrentLanguage());
		model.setSessionCurrency(commonI18NService.getCurrentCurrency());
		
		// Title is optional
		if (StringUtils.isNotBlank(data.getTitleCode()))
		{
			final TitleModel title = userService.getTitleForCode(data.getTitleCode());
			model.setTitle(title);
		}
		
		return model;

	}

	/**
	 * Converts a {@link B2BRegistrationData} into a {@B2BRegistrationModel}
	 *
	 * @param data
	 *           The registration data
	 * @return An unsaved instance of type {@B2BRegistrationModel}
	 */
	protected B2BRegistrationModel toRegistrationModel(final B2BRegistrationData data)
	{

		final B2BRegistrationModel model = modelService.create(B2BRegistrationModel.class);

		// Use reflection to copy most properties and ignore these since we want to manage them manually
		BeanUtils.copyProperties(data, model, new String[]
		{ "titleCode", "companyAddressCountryIso", "companyAddressRegion", "baseStore", "cmsSite", "currency", "language" });

		// Title is optional
		if (StringUtils.isNotBlank(data.getTitleCode()))
		{
			final TitleModel title = userService.getTitleForCode(data.getTitleCode());
			model.setTitle(title);
		}

		// Country and region are optional
		if (StringUtils.isNotBlank(data.getCompanyAddressCountryIso()))
		{
			final CountryModel country = commonI18NService.getCountry(data.getCompanyAddressCountryIso());
			model.setCompanyAddressCountry(country);

			if (StringUtils.isNotBlank(data.getCompanyAddressRegion()))
			{
				final RegionModel region = commonI18NService.getRegion(country, data.getCompanyAddressRegion());
				model.setCompanyAddressRegion(region);
			}
		}

		// Get these from current context
		model.setBaseStore(baseStoreService.getCurrentBaseStore());
		model.setBaseSite(baseSiteService.getCurrentBaseSite());
		model.setCurrency(commonI18NService.getCurrentCurrency());
		model.setLanguage(commonI18NService.getCurrentLanguage());

		return model;

	}

}
