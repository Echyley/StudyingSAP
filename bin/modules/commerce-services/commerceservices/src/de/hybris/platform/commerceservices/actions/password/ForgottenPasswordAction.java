/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.actions.password;

import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.model.process.ForgottenPasswordProcessModel;
import de.hybris.platform.commerceservices.user.UserMatchingService;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.webservicescommons.util.YSanitizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.hybris.platform.core.model.security.PrincipalModel.UID;
import static de.hybris.platform.servicelayer.internal.i18n.I18NConstants.CURRENCY_SESSION_ATTR_KEY;
import static de.hybris.platform.servicelayer.internal.i18n.I18NConstants.LANGUAGE_SESSION_ATTR_KEY;


/**
 * A process action that runs password recovery
 */
public class ForgottenPasswordAction extends AbstractSimpleDecisionAction<ForgottenPasswordProcessModel>
{

	private static final Logger LOG = LoggerFactory.getLogger(ForgottenPasswordAction.class);
	private static final String CURRENT_SITE = "currentSite";

	private UserMatchingService userMatchingService;
	private SessionService sessionService;
	private CustomerAccountService customerAccountService;

	public SessionService getSessionService()
	{
		return sessionService;
	}

	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	public UserMatchingService getUserMatchingService()
	{
		return userMatchingService;
	}

	public void setUserMatchingService(final UserMatchingService userMatchingService)
	{
		this.userMatchingService = userMatchingService;
	}

	public CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}

	@Override
	public Transition executeAction(final ForgottenPasswordProcessModel forgottenPasswordProcessModel)
	{
		if (!getProcessParameterHelper().containsParameter(forgottenPasswordProcessModel, UID))
		{
			LOG.error("The field [uid] cannot be empty");
			return Transition.NOK;
		}

		if (forgottenPasswordProcessModel.getLanguage() == null)
		{
			LOG.error("The language cannot be empty");
			return Transition.NOK;
		}

		if (forgottenPasswordProcessModel.getCurrency() == null)
		{
			LOG.error("The currency cannot be empty");
			return Transition.NOK;
		}

		final String customerUid = (String) getProcessParameterHelper()
				.getProcessParameterByName(UID, forgottenPasswordProcessModel.getContextParameters()).getValue();
		getSessionService().setAttribute(CURRENT_SITE, forgottenPasswordProcessModel.getSite());
		getSessionService().setAttribute(LANGUAGE_SESSION_ATTR_KEY, forgottenPasswordProcessModel.getLanguage());
		getSessionService().setAttribute(CURRENCY_SESSION_ATTR_KEY, forgottenPasswordProcessModel.getCurrency());

		try
		{
			final CustomerModel customerModel = getUserMatchingService().getUserByProperty(customerUid, CustomerModel.class);
			getCustomerAccountService().forgottenPassword(customerModel);
			return Transition.OK;
		}
		catch (final UnknownIdentifierException uie)
		{
			final String sanitizedCustomerUid = YSanitizer.sanitize(customerUid);
			LOG.warn("User with unique property: {} does not exist in the database.", sanitizedCustomerUid);
			return Transition.NOK;
		}
	}
}
